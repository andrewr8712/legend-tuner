package dev.legendtuner.app.access

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class AccessMode(val label: String) {
    ROOT("Root"),
    SHIZUKU("Shizuku"),
    MANUAL("Manual file"),
    DIRECT("Direct")
}

data class LocatedFile(
    val path: String,
    val displayPath: String,
    val volumeLabel: String,
    val size: Long,
    val modified: Long
)

interface FileAccess {
    val mode: AccessMode
    suspend fun locate(): LocatedFile?
    suspend fun read(path: String): ByteArray
    suspend fun write(path: String, bytes: ByteArray)
    suspend fun forceStop(): Boolean
}

class DirectFileAccess(
    private val candidate: GamePaths.Candidate
) : FileAccess {
    override val mode = AccessMode.DIRECT

    override suspend fun locate(): LocatedFile? {
        val file = File(candidate.path)
        if (!file.exists() || !file.canRead()) return null
        return LocatedFile(
            path = file.absolutePath,
            displayPath = file.absolutePath,
            volumeLabel = candidate.volumeLabel,
            size = file.length(),
            modified = file.lastModified()
        )
    }

    override suspend fun read(path: String): ByteArray = File(path).readBytes()

    override suspend fun write(path: String, bytes: ByteArray) {
        FileOutputStream(path, false).use { it.write(bytes) }
    }

    override suspend fun forceStop(): Boolean = false
}

class SafAccess(
    private val context: Context,
    private val uri: Uri
) : FileAccess {
    override val mode = AccessMode.MANUAL

    override suspend fun locate(): LocatedFile? = withContext(Dispatchers.IO) {
        var name = uri.lastPathSegment ?: "preferences"
        var size = 0L
        var modified = 0L
        runCatching {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    val modifiedIndex = cursor.getColumnIndex("last_modified")
                    if (nameIndex >= 0) name = cursor.getString(nameIndex) ?: name
                    if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) size = cursor.getLong(sizeIndex)
                    if (modifiedIndex >= 0 && !cursor.isNull(modifiedIndex)) modified = cursor.getLong(modifiedIndex)
                }
            }
        }
        LocatedFile(
            path = uri.toString(),
            displayPath = name,
            volumeLabel = "Manual file",
            size = size,
            modified = modified
        )
    }

    override suspend fun read(path: String): ByteArray = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: error("Could not open the selected file")
    }

    override suspend fun write(path: String, bytes: ByteArray) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(uri, "wt")?.use { it.write(bytes) }
            ?: error("The selected file is not writable. Re-import it with write access.")
        Unit
    }

    override suspend fun forceStop(): Boolean = false
}
