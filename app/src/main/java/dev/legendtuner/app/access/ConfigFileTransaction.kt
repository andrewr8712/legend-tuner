package dev.legendtuner.app.access

/**
 * Writes [replacement] and verifies it by reading the path back. If any step fails, attempts to
 * write [rollback] and verifies that too; the caller can tell the user whether the previous file
 * was actually restored.
 *
 * This is deliberately separate from [FileAccess] so the restore path is testable with an
 * in-memory access implementation.
 */
internal suspend fun replaceWithRollback(
    access: FileAccess,
    path: String,
    replacement: ByteArray,
    rollback: ByteArray,
    validate: (ByteArray) -> Unit
): ByteArray {
    try {
        access.write(path, replacement)
        val readBack = access.read(path)
        validate(readBack)
        return readBack
    } catch (failure: Exception) {
        val rolledBack = runCatching { access.write(path, rollback) }.isSuccess
        val rollbackVerified = if (rolledBack) {
            runCatching { validate(access.read(path)) }.isSuccess
        } else {
            false
        }
        throw FileReplaceException(failure, rolledBack, rollbackVerified)
    }
}

internal class FileReplaceException(
    cause: Throwable,
    val rolledBack: Boolean,
    val rollbackVerified: Boolean
) : Exception(cause.message ?: "Could not replace the file", cause)
