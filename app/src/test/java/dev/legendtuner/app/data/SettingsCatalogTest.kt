package dev.legendtuner.app.data

import dev.legendtuner.app.ui.LegendIcons
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SettingsCatalogTest {

    @Test
    fun allLocalIconsParseIntoImageVectors() {
        val icons = listOf(
            LegendIcons.Dashboard, LegendIcons.Tune, LegendIcons.Backups, LegendIcons.Code,
            LegendIcons.Settings, LegendIcons.Warning, LegendIcons.MoreVert, LegendIcons.Delete,
            LegendIcons.Download, LegendIcons.Restore
        )
        icons.forEach { icon ->
            assertNotNull(icon)
            assertEquals(24f, icon.viewportWidth)
            assertEquals(24f, icon.viewportHeight)
        }
    }

    @Test
    fun everyFriendlySettingHasAUniqueKeyAndValidRange() {
        val keys = SettingsCatalog.settings.map { it.key }
        assertEquals(keys.size, keys.toSet().size)
        SettingsCatalog.settings.forEach { def ->
            assertNotNull(SettingsCatalog.find(def.key))
            if (def.type == SettingType.PERCENT || def.type == SettingType.INTEGER) {
                assert(def.max >= def.min) { "${def.key} has an invalid range" }
            }
        }
    }

    @Test
    fun toggleAndLevelLabelsAreHumanReadable() {
        val shadows = SettingsCatalog.find("Shadows")!!
        assertEquals("High", shadows.displayValue(3))
        assertEquals("Off", shadows.displayValue(0))
        val hdr = SettingsCatalog.find("HDR")!!
        assertEquals("On", hdr.displayValue(1))
        val fps = SettingsCatalog.find("FramerateLimit")!!
        assertEquals("144 fps", fps.displayValue(144))
    }
}
