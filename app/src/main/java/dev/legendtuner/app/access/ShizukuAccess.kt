package dev.legendtuner.app.access

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import rikka.shizuku.Shizuku
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ShizukuAccess private constructor(
    private val context: Context,
    private val service: IFileService
) : FileAccess {
    override val mode = AccessMode.SHIZUKU

    override suspend fun locate(): LocatedFile? = withContext(Dispatchers.IO) {
        for (candidate in GamePaths.candidates(context)) {
            val exists = runCatching { service.exists(candidate.path) }.getOrDefault(false)
            if (!exists) continue
            val rawStat = runCatching { service.stat(candidate.path) }.getOrDefault("")
            val parts = rawStat.split('|')
            return@withContext LocatedFile(
                path = candidate.path,
                displayPath = candidate.path,
                volumeLabel = candidate.volumeLabel,
                size = parts.getOrNull(0)?.toLongOrNull() ?: 0L,
                modified = (parts.getOrNull(1)?.toLongOrNull() ?: 0L) * 1000L
            )
        }
        null
    }

    override suspend fun read(path: String): ByteArray = withContext(Dispatchers.IO) {
        runCatching { service.read(path) }
            .getOrElse { throw IllegalStateException("Shizuku could not read the file: ${it.message}", it) }
    }

    override suspend fun write(path: String, bytes: ByteArray) = withContext(Dispatchers.IO) {
        runCatching { service.write(path, bytes) }
            .getOrElse { throw IllegalStateException("Shizuku could not write the file: ${it.message}", it) }
        Unit
    }

    override suspend fun forceStop(): Boolean = withContext(Dispatchers.IO) {
        runCatching { service.forceStopGame(GamePaths.GAME_PACKAGE) }.getOrDefault(false)
    }

    companion object {
        private var activeConnection: ServiceConnection? = null

        suspend fun connect(context: Context): ShizukuAccess {
            if (!ShizukuManager.isBinderAlive()) {
                throw IllegalStateException("Shizuku is not running. Open Shizuku and start the service first.")
            }
            if (!ShizukuManager.hasPermission()) {
                throw SecurityException("Shizuku permission has not been granted.")
            }
            val service = withTimeout(15_000) { bind(context.applicationContext) }
            return ShizukuAccess(context.applicationContext, service)
        }

        private suspend fun bind(context: Context): IFileService = suspendCancellableCoroutine { cont ->
            val args = Shizuku.UserServiceArgs(
                ComponentName(context.packageName, FileService::class.java.name)
            )
                .daemon(false)
                .processNameSuffix("file")
                .debuggable(false)
                .version(1)

            val connection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                    if (!cont.isActive) return
                    if (binder == null) {
                        cont.resumeWithException(IllegalStateException("Shizuku returned an empty binder"))
                    } else {
                        cont.resume(IFileService.Stub.asInterface(binder))
                    }
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    // The bound service is gone; a new connect() will recreate it.
                }
            }
            activeConnection = connection
            runCatching { Shizuku.bindUserService(args, connection) }
                .onFailure { cont.resumeWithException(it) }
            cont.invokeOnCancellation { runCatching { Shizuku.unbindUserService(args, connection, true) } }
        }
    }
}
