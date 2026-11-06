package dev.legendtuner.app

import dev.legendtuner.app.access.AccessMode
import dev.legendtuner.app.access.FileAccess
import dev.legendtuner.app.access.LocatedFile
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfigFileLoaderTest {

    private fun fixture(): ByteArray =
        javaClass.classLoader!!.getResourceAsStream("tomb-raider-preferences-sample.xml")!!.readBytes()

    private class FakeAccess(
        private val file: LocatedFile?,
        private val bytes: ByteArray
    ) : FileAccess {
        override val mode = AccessMode.MANUAL
        override suspend fun locate(): LocatedFile? = file
        override suspend fun read(path: String): ByteArray = bytes
        override suspend fun write(path: String, bytes: ByteArray) = error("not used")
        override suspend fun forceStop(): Boolean = false
    }

    @Test
    fun loadsAndValidatesARegistryFile() = runBlocking {
        val bytes = fixture()
        val file = LocatedFile("content://tomb", "tomb.xml", "Manual file", bytes.size.toLong(), 0L)
        val result = ConfigFileLoader().load(FakeAccess(file, bytes), null)

        assertTrue(result is ConfigLoadResult.Success)
        val loaded = (result as ConfigLoadResult.Success).config
        assertEquals(bytes.size, loaded.originalBytes.size)
        assertEquals("Manual file", loaded.file.volumeLabel)
    }

    @Test
    fun reportsAMissingFileWithoutThrowing() = runBlocking {
        val result = ConfigFileLoader().load(FakeAccess(null, ByteArray(0)), null)

        assertTrue(result is ConfigLoadResult.Failure)
        val failure = (result as ConfigLoadResult.Failure).failure
        assertTrue(failure.notFound)
        assertTrue(failure.message.contains("was not found"))
    }

    @Test
    fun reportsReadFailureWithoutCrashing() = runBlocking {
        val file = LocatedFile("content://locked", "locked.xml", "Manual file", 4L, 0L)
        val access = object : FileAccess {
            override val mode = AccessMode.MANUAL
            override suspend fun locate() = file
            override suspend fun read(path: String): ByteArray =
                throw SecurityException("Permission denied")
            override suspend fun write(path: String, bytes: ByteArray) = Unit
            override suspend fun forceStop() = false
        }

        val result = ConfigFileLoader().load(access, null)

        assertTrue(result is ConfigLoadResult.Failure)
        assertTrue((result as ConfigLoadResult.Failure).failure.message.contains("Could not read"))
    }

    @Test
    fun reportsMalformedXml() = runBlocking {
        val file = LocatedFile("content://bad", "bad.xml", "Manual file", 4L, 0L)
        val result = ConfigFileLoader().load(FakeAccess(file, "nope".toByteArray()), null)

        assertTrue(result is ConfigLoadResult.Failure)
        assertTrue((result as ConfigLoadResult.Failure).failure.message.contains("Could not parse"))
    }
}
