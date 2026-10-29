package dev.legendtuner.app.access

import java.io.File
import java.io.FileOutputStream

/**
 * Runs inside Shizuku's UserService process as the shell UID (2000).
 * This process can read and write /sdcard/Android/data, unlike the app itself.
 */
class FileService : IFileService.Stub() {
    override fun exists(path: String): Boolean = runCatching { File(path).exists() }.getOrDefault(false)

    override fun read(path: String): ByteArray = File(path).readBytes()

    override fun write(path: String, data: ByteArray) {
        val file = File(path)
        if (!file.exists()) error("Target file does not exist")
        FileOutputStream(file, false).use { it.write(data) }
    }

    override fun stat(path: String): String {
        val file = File(path)
        if (!file.exists()) return ""
        return "${file.length()}|${file.lastModified() / 1000L}"
    }

    override fun forceStopGame(packageName: String): Boolean {
        return runCatching {
            val process = Runtime.getRuntime().exec(arrayOf("am", "force-stop", packageName))
            process.waitFor() == 0
        }.getOrDefault(false)
    }
}
