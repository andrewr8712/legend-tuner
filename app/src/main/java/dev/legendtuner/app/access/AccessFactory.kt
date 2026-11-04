package dev.legendtuner.app.access

import android.content.Context
import android.os.Build
import java.io.File

object AccessFactory {
    suspend fun tryRoot(context: Context, packageName: String): RootAccess? {
        if (!RootShell.isAvailable()) return null
        return RootAccess(context.applicationContext, packageName)
    }

    fun tryDirect(context: Context, packageName: String): DirectFileAccess? {
        for (candidate in GamePaths.candidates(context, packageName)) {
            val file = File(candidate.path)
            if (file.exists() && file.canRead()) return DirectFileAccess(candidate)
        }
        return null
    }

    fun directPossible(context: Context, packageName: String): Boolean =
        GamePaths.candidates(context, packageName).any { File(it.path).canRead() }

    fun requiresLegacyStoragePermission(): Boolean = Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2
}
