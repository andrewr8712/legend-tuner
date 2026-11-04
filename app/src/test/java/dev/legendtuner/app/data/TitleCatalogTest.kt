package dev.legendtuner.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TitleCatalogTest {

    private val catalogs = FeralTitles.ALL.mapNotNull { it.titleCatalog }

    @Test
    fun everyControlIsFullyDescribed() {
        catalogs.forEach { catalog ->
            catalog.controls.forEach { control ->
                assertTrue("${control.key} has no label", control.label.isNotBlank())
                assertTrue(
                    "${control.key} description is too short: '${control.description}'",
                    control.description.length >= 40
                )
            }
        }
    }

    @Test
    fun keysAndGroupsAreConsistent() {
        catalogs.forEach { catalog ->
            val keys = catalog.controls.map { it.key }
            assertEquals("duplicate keys", keys.size, keys.toSet().size)
            catalog.controls.forEach { control ->
                assertTrue(
                    "${control.key} uses unknown group ${control.group}",
                    control.group in catalog.groups
                )
            }
        }
    }

    @Test
    fun selectableControlsOfferOptions() {
        catalogs.forEach { catalog ->
            catalog.controls
                .filter { it.kind == ControlKind.TOGGLE || it.kind == ControlKind.CHOICE }
                .forEach { control ->
                    assertTrue("${control.key} has fewer than two options", control.choices.size >= 2)
                    assertEquals(
                        "${control.key} has duplicate option tokens",
                        control.choices.size,
                        control.choices.map { it.token }.toSet().size
                    )
                }
        }
    }

    @Test
    fun invertedControlsMapStoredTokenToDisplayedState() {
        val catalog = FeralTitles.ALL.first { it.id == "hitman-blood-money" }.titleCatalog!!
        val bloom = catalog.find("DisableBloom")!!
        assertEquals("0", bloom.effectiveToken("1"))
        assertEquals("1", bloom.effectiveToken("0"))
        assertEquals("Off", bloom.labelFor("1"))
        assertEquals("On", bloom.labelFor("0"))

        val shadows = catalog.find("EnableDropShadows")!!
        assertEquals("1", shadows.effectiveToken("1"))
    }

    @Test
    fun gridAutosportCoversItsThreePresetFamilies() {
        val catalog = FeralTitles.ALL.first { it.id == "grid-autosport" }.titleCatalog!!
        assertEquals("gfxconfig_shadows", catalog.keyFor(catalog.find("gfxconfig_shadows")!!))
        val high = catalog.copy(keyPrefix = "gfxconfig_high_", stripPrefix = "gfxconfig_")
        assertEquals("gfxconfig_high_shadows", high.keyFor(high.find("gfxconfig_shadows")!!))
        val power = catalog.copy(keyPrefix = "gfxconfigpowersaver_", stripPrefix = "gfxconfig_")
        assertEquals("gfxconfigpowersaver_shadows", power.keyFor(power.find("gfxconfig_shadows")!!))
    }
}
