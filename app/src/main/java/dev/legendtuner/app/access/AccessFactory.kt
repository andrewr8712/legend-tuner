package dev.legendtuner.app.access

import android.content.Context
import android.os.Build
import java.io.File

object AccessFactory {
    suspend fun tryRoot(context: Context): RootAccess? {
        if (!RootShell.isAvailable()) return null
        return RootAccess(context.applicationContext)
    }

    fun tryDirect(context: Context): DirectFileAccess? {
        for (candidate in GamePaths.candidates(context)) {
            val file = File(candidate.path)
            if (file.exists() && file.canRead()) return DirectFileAccess(candidate)
        }
        return null
    }

    fun directPossible(context: Context): Boolean = GamePaths.candidates(context).any { File(it.path).canRead() }

    fun requiresLegacyStoragePermission(): Boolean = Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2
}
