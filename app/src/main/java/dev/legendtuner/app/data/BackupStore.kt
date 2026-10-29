package dev.legendtuner.app.data

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupInfo(
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

class BackupStore(context: Context) {
    private val dir = File(context.filesDir, "backups").apply { mkdirs() }
    private val stamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)

    fun save(bytes: ByteArray, label: String): BackupInfo {
        require(bytes.isNotEmpty()) { "Refusing to save an empty backup" }
        val safeLabel = label.replace(Regex("[^A-Za-z0-9_-]"), "-").take(32).ifBlank { "config" }
        var file = File(dir, "preferences-${stamp.format(Date())}-$safeLabel.xml")
        var counter = 1
        while (file.exists()) {
            file = File(dir, "preferences-${stamp.format(Date())}-$safeLabel-${counter++}.xml")
        }
        val temp = File(dir, "${file.name}.tmp")
        temp.writeBytes(bytes)
        if (!temp.renameTo(file)) {
            temp.copyTo(file, overwrite = true)
            temp.delete()
        }
        prune()
        return BackupInfo(file, file.name, file.length(), file.lastModified())
    }

    fun list(): List<BackupInfo> {
        val files = dir.listFiles { f -> f.isFile && f.extension.equals("xml", ignoreCase = true) } ?: emptyArray()
        return files.sortedByDescending { it.lastModified() }
            .map { BackupInfo(it, it.name, it.length(), it.lastModified()) }
    }

    fun read(name: String): ByteArray {
        val safe = File(name).name
        val file = File(dir, safe)
        require(file.parentFile == dir && file.exists()) { "Backup not found" }
        return file.readBytes()
    }

    fun delete(name: String) {
        val safe = File(name).name
        val file = File(dir, safe)
        if (file.parentFile == dir) file.delete()
    }

    private fun prune(max: Int = 12) {
        list().drop(max).forEach { it.file.delete() }
    }
}
