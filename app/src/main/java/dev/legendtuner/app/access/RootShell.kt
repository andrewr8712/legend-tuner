package dev.legendtuner.app.access

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class ShellResult(val code: Int, val stdout: ByteArray, val stderr: String) {
    val text: String get() = stdout.toString(Charsets.UTF_8)
}

object RootShell {
    private val suBinaries = listOf(
        "/system/bin/su", "/system/xbin/su", "/sbin/su", "/su/bin/su",
        "/data/adb/magisk/su", "/debug_ramdisk/su"
    )

    suspend fun hasSuBinary(): Boolean = withContext(Dispatchers.IO) {
        if (suBinaries.any { java.io.File(it).exists() }) return@withContext true
        runCatching {
            val p = ProcessBuilder("sh", "-c", "command -v su").start()
            val found = p.inputStream.bufferedReader().readText().trim()
            p.waitFor(3, TimeUnit.SECONDS)
            found.isNotBlank()
        }.getOrDefault(false)
    }

    suspend fun isAvailable(): Boolean {
        if (!hasSuBinary()) return false
        val result = run("id -u", timeoutSeconds = 8)
        return result.code == 0 && result.text.trim() == "0"
    }

    suspend fun run(
        command: String,
        stdin: ByteArray? = null,
        timeoutSeconds: Long = 25
    ): ShellResult = withContext(Dispatchers.IO) {
        val process = ProcessBuilder("su", "-c", command)
            .redirectErrorStream(false)
            .start()
        var stdout = ByteArray(0)
        var stderr = ""
        val stdoutThread = kotlin.concurrent.thread(start = true, isDaemon = true) {
            runCatching { process.inputStream.use { it.readBytes() } }.getOrNull()?.let { stdout = it }
        }
        val stderrThread = kotlin.concurrent.thread(start = true, isDaemon = true) {
            runCatching {
                stderr = process.errorStream.use { it.readBytes() }.toString(Charsets.UTF_8)
            }
        }
        try {
            if (stdin != null) {
                process.outputStream.use { it.write(stdin) }
            } else {
                process.outputStream.close()
            }
            val finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
            if (!finished) {
                process.destroyForcibly()
                stdoutThread.join(1500)
                stderrThread.join(1500)
                return@withContext ShellResult(-1, stdout, "Root command timed out")
            }
            stdoutThread.join(2000)
            stderrThread.join(2000)
            ShellResult(process.exitValue(), stdout, stderr)
        } catch (e: Exception) {
            process.destroyForcibly()
            stdoutThread.join(1000)
            stderrThread.join(1000)
            ShellResult(-1, stdout, e.message ?: "Root command failed")
        }
    }

    fun quote(path: String): String = "'" + path.replace("'", "'\\''") + "'"
}

class RootAccess(private val context: android.content.Context) : FileAccess {
    override val mode = AccessMode.ROOT

    override suspend fun locate(): LocatedFile? {
        for (candidate in GamePaths.candidates(context)) {
            val test = RootShell.run("test -f ${RootShell.quote(candidate.path)} && echo yes")
            if (test.code == 0 && test.text.lineSequence().any { it.trim() == "yes" }) {
                val stat = RootShell.run("stat -c '%s|%Y' ${RootShell.quote(candidate.path)}")
                val statLine = stat.text.lineSequence().lastOrNull { it.contains('|') }?.trim().orEmpty()
                val parts = statLine.split('|')
                return LocatedFile(
                    path = candidate.path,
                    displayPath = candidate.path,
                    volumeLabel = candidate.volumeLabel,
                    size = parts.getOrNull(0)?.toLongOrNull() ?: 0L,
                    modified = (parts.getOrNull(1)?.toLongOrNull() ?: 0L) * 1000L
                )
            }
        }
        return null
    }

    override suspend fun read(path: String): ByteArray {
        val result = RootShell.run("cat ${RootShell.quote(path)}")
        if (result.code != 0) error(result.stderr.ifBlank { "Could not read the config file" })
        return result.stdout
    }

    override suspend fun write(path: String, bytes: ByteArray) {
        val result = RootShell.run("cat > ${RootShell.quote(path)}", stdin = bytes)
        if (result.code != 0) error(result.stderr.ifBlank { "Could not write the config file" })
    }

    override suspend fun forceStop(): Boolean {
        val result = RootShell.run("am force-stop ${GamePaths.GAME_PACKAGE}")
        return result.code == 0
    }
}
