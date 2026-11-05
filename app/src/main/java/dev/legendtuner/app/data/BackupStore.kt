package dev.legendtuner.app.data

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupInfo(
    val titleId: String,
    val file: File,
    val name: String,
    val size: Long,
    val modified: Long
) {
    fun displayTime(): String = SimpleDateFormat("d MMM yyyy · HH:mm", Locale.getDefault()).format(Date(modified))
    fun displaySize(): String = when {
        size >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB", size / (1024f * 1024f))
        size >= 1024 -> String.format(Locale.US, "%.1f KB", size / 1024f)
        else -> "$size B"
    }
}

/**
 * Snapshots are grouped by title id. A title's backups are never listed, read, deleted, or pruned
 * through another title's directory, so a restore cannot move a GRID registry into Hitman (or any
 * other title) by mistake.
 *
 * Backups created before title scoping are left in [rootDir] untouched but are deliberately not
 * exposed: they have no title identity, so restoring one cannot be made safe automatically.
 */
class BackupStore(private val rootDir: File) {

    constructor(context: Context) : this(File(context.filesDir, "backups"))

    init {
        rootDir.mkdirs()
    }

    fun save(titleId: String, bytes: ByteArray, label: String): BackupInfo {
        require(bytes.isNotEmpty()) { "Refusing to save an empty backup" }
        val dir = titleDir(titleId)
        val safeLabel = label.replace(Regex("[^A-Za-z0-9_-]"), "-").take(32).ifBlank { "config" }
        val stamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        var file = File(dir, "preferences-$stamp-$safeLabel.xml")
        var counter = 1
        while (file.exists()) {
            file = File(dir, "preferences-$stamp-$safeLabel-${counter++}.xml")
        }
        val temp = File(dir, "${file.name}.tmp")
        temp.writeBytes(bytes)
        if (!temp.renameTo(file)) {
            temp.copyTo(file, overwrite = true)
            temp.delete()
        }
        prune(titleId)
        return BackupInfo(titleId, file, file.name, file.length(), file.lastModified())
    }

    fun list(titleId: String): List<BackupInfo> {
        val dir = titleDir(titleId)
        val files = dir.listFiles { f -> f.isFile && f.extension.equals("xml", ignoreCase = true) } ?: emptyArray()
        return files.sortedByDescending { it.lastModified() }
            .map { BackupInfo(titleId, it, it.name, it.length(), it.lastModified()) }
    }

    fun read(titleId: String, name: String): ByteArray {
        val dir = titleDir(titleId)
        val safe = File(name).name
        val file = File(dir, safe)
        require(file.parentFile == dir && file.exists()) { "Backup not found for this title" }
        return file.readBytes()
    }

    fun delete(titleId: String, name: String) {
        val dir = titleDir(titleId)
        val safe = File(name).name
        val file = File(dir, safe)
        if (file.parentFile == dir) file.delete()
    }

    private fun prune(titleId: String, max: Int = 12) {
        list(titleId).drop(max).forEach { it.file.delete() }
    }

    private fun titleDir(titleId: String): File {
        val safeTitle = titleId.replace(Regex("[^A-Za-z0-9_-]"), "-").take(64).ifBlank { "manual" }
        return File(rootDir, safeTitle).apply { mkdirs() }
    }
}
