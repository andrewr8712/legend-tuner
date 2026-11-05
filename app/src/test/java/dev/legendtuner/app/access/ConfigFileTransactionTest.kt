package dev.legendtuner.app.access

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfigFileTransactionTest {

    private class FakeAccess(var bytes: ByteArray) : FileAccess {
        override val mode = AccessMode.DIRECT

        var readCount = 0
        var writeCount = 0
        var corruptReadAt = -1
        var failWriteAt = -1

        override suspend fun locate(): LocatedFile =
            LocatedFile("fake", "fake", "Fake", bytes.size.toLong(), 0)

        override suspend fun read(path: String): ByteArray {
            readCount++
            return if (readCount == corruptReadAt) "corrupt".toByteArray() else bytes
        }

        override suspend fun write(path: String, bytes: ByteArray) {
            writeCount++
            if (writeCount == failWriteAt) error("write failed")
            this.bytes = bytes
        }

        override suspend fun forceStop(): Boolean = false
    }

    @Test
    fun successfulReplacementReturnsTheReadBackBytes() = runBlocking {
        val access = FakeAccess("old".toByteArray())

        val result = replaceWithRollback(
            access = access,
            path = "preferences",
            replacement = "new".toByteArray(),
            rollback = "old".toByteArray()
        ) { bytes -> require(String(bytes) != "corrupt") }

        assertArrayEquals("new".toByteArray(), result)
        assertArrayEquals("new".toByteArray(), access.bytes)
    }

    @Test
    fun verificationFailureRollsBackAndVerifiesTheOriginal() = runBlocking {
        val access = FakeAccess("old".toByteArray()).apply { corruptReadAt = 1 }

        val failure = assertThrows(FileReplaceException::class.java) {
            runBlocking {
                replaceWithRollback(
                    access = access,
                    path = "preferences",
                    replacement = "new".toByteArray(),
                    rollback = "old".toByteArray()
                ) { bytes -> require(String(bytes) != "corrupt") }
            }
        }

        assertTrue(failure.rolledBack)
        assertTrue(failure.rollbackVerified)
        assertArrayEquals("old".toByteArray(), access.bytes)
    }

    @Test
    fun rollbackWriteFailureIsReported() {
        val access = FakeAccess("old".toByteArray()).apply {
            corruptReadAt = 1
            failWriteAt = 2
        }

        val failure = assertThrows(FileReplaceException::class.java) {
            runBlocking {
                replaceWithRollback(
                    access = access,
                    path = "preferences",
                    replacement = "new".toByteArray(),
                    rollback = "old".toByteArray()
                ) { bytes -> require(String(bytes) != "corrupt") }
            }
        }

        assertFalse(failure.rolledBack)
        assertFalse(failure.rollbackVerified)
        assertEquals("new", String(access.bytes))
    }
}
