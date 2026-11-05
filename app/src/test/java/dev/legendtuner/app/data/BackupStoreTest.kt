package dev.legendtuner.app.data

import java.io.File
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class BackupStoreTest {

    @get:Rule
    val temp = TemporaryFolder()

    private lateinit var store: BackupStore

    @Before
    fun setUp() {
        store = BackupStore(File(temp.root, "backups"))
    }

    @Test
    fun backupsAreIsolatedByTitle() {
        val a = store.save("game-a", "<a/>".toByteArray(), "a")
        val b = store.save("game-b", "<b/>".toByteArray(), "b")

        assertEquals(listOf(a.name), store.list("game-a").map { it.name })
        assertEquals(listOf(b.name), store.list("game-b").map { it.name })
        assertEquals("game-a", a.titleId)
        assertEquals("game-b", b.titleId)
        assertArrayEquals("<b/>".toByteArray(), store.read("game-b", b.name))

        assertThrows(IllegalArgumentException::class.java) {
            store.read("game-b", a.name)
        }

        store.delete("game-b", b.name)
        assertEquals(1, store.list("game-a").size)
        assertEquals(0, store.list("game-b").size)
    }

    @Test
    fun pruningIsPerTitle() {
        repeat(13) { index ->
            store.save("game-a", "a$index".toByteArray(), "Graphics")
        }
        store.save("game-b", "b".toByteArray(), "Graphics")

        assertEquals(12, store.list("game-a").size)
        assertEquals(1, store.list("game-b").size)
    }

    @Test
    fun preScopingBackupsAreNotExposed() {
        File(temp.root, "backups").mkdirs()
        File(temp.root, "backups/preferences-old.xml").writeText("<old/>")

        assertEquals(0, store.list("game-a").size)
    }

    @Test
    fun emptyBackupsAreRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            store.save("game-a", ByteArray(0), "Graphics")
        }
    }

    @Test
    fun backupNamesCannotEscapeTheTitleDirectory() {
        store.save("game-a", "<a/>".toByteArray(), "Graphics")

        assertThrows(IllegalArgumentException::class.java) {
            store.read("game-a", "../outside.xml")
        }
    }
}
