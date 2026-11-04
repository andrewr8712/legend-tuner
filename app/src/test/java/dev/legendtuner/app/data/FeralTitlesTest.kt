package dev.legendtuner.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeralTitlesTest {

    @Test
    fun everyTitleHasUniqueIdAndPackage() {
        val ids = FeralTitles.ALL.map { it.id }
        val packages = FeralTitles.ALL.map { it.packageName }
        assertEquals(ids.size, ids.toSet().size)
        assertEquals(packages.size, packages.toSet().size)
    }

    @Test
    fun everyTitleHasARegistryRootUnderCurrentUser() {
        FeralTitles.ALL.forEach { title ->
            val root = title.registryRoot
            assertTrue("${title.id} has no registry root", root != null && root.size >= 3)
            assertEquals("HKEY_CURRENT_USER", root?.first())
        }
    }

    @Test
    fun monogramIsTwoCharacters() {
        FeralTitles.ALL.forEach { title ->
            assertEquals(title.id, 2, title.monogram.length)
        }
        assertEquals("GA", FeralTitles.ALL.first { it.id == "grid-autosport" }.monogram)
        assertEquals("AI", FeralTitles.ALL.first { it.id == "alien-isolation" }.monogram)
        assertEquals("NA", FeralTitles.ALL.first { it.id == "napoleon" }.monogram)
    }

    @Test
    fun cataloguedTitlesAreGuidedAndOthersAreNot() {
        val catalogued = FeralTitles.ALL.filter { it.titleCatalog != null }.map { it.id }.toSet()
        assertEquals(
            setOf(
                "grid-autosport", "alien-isolation", "saints-row-3", "napoleon",
                "hitman-blood-money", "lara-croft-gol", "lara-croft-too"
            ),
            catalogued
        )
        assertTrue(FeralTitles.GRID_LEGENDS.guided)
        assertTrue(FeralTitles.ALL.all { it.guided })
    }
}
