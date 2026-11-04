package dev.legendtuner.app.access

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import java.io.File

object GamePaths {
    data class Candidate(val path: String, val volumeLabel: String)

    fun relativeConfigPath(packageName: String): String =
        "Android/data/$packageName/files/feral_app_support/preferences"

    fun candidates(context: Context, packageName: String): List<Candidate> {
        val roots = LinkedHashMap<String, String>() // canonical root -> label
        fun addRoot(root: File?, label: String) {
            if (root == null) return
            val path = root.absolutePath.trimEnd('/')
            if (path.isBlank()) return
            roots.putIfAbsent(path, label)
        }

        val primary = Environment.getExternalStorageDirectory()
        addRoot(primary, "Internal storage")

        // getExternalFilesDirs returns app-specific dirs on every mounted volume,
        // which lets us discover the SD card path without broad storage access.
        context.getExternalFilesDirs(null).filterNotNull().forEach { appDir ->
            val full = appDir.absolutePath
            val marker = "/Android/data/"
            val index = full.indexOf(marker)
            if (index > 0) {
                val root = full.substring(0, index)
                addRoot(File(root), if (root == primary.absolutePath.trimEnd('/')) "Internal storage" else "SD card")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            runCatching {
                val sm = context.getSystemService(StorageManager::class.java)
                sm.storageVolumes.forEachIndexed { i, volume ->
                    val dir = runCatching { volume.directory }.getOrNull()
                    val description = runCatching { volume.getDescription(context) }.getOrNull()
                    addRoot(dir, description?.takeIf { it.isNotBlank() } ?: if (i == 0) "Internal storage" else "SD card")
                }
            }
        }

        val relative = relativeConfigPath(packageName)
        return roots.keys.map { root ->
            Candidate(
                path = "$root/$relative",
                volumeLabel = roots[root] ?: "Storage"
            )
        }
    }

    fun primaryCandidate(context: Context, packageName: String): Candidate =
        candidates(context, packageName).firstOrNull() ?: Candidate(
            "${Environment.getExternalStorageDirectory().absolutePath}/${relativeConfigPath(packageName)}",
            "Internal storage"
        )

    fun isInstalled(context: Context, packageName: String): Boolean = runCatching {
        context.packageManager.getPackageInfo(packageName, 0)
        true
    }.getOrDefault(false)

    /** True when the preferences file can be stat'ed directly (old Android / permissive devices). */
    fun isReadableDirectly(context: Context, packageName: String): Boolean =
        candidates(context, packageName).any { File(it.path).canRead() }
}
