package dev.legendtuner.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HeightDropdownTest {
    @Test
    fun fixedRenderHeightUsesCommonResolutionChoices() {
        val def = SettingsCatalog.find("FixedScreenHeight")!!
        assertEquals(SettingType.DROPDOWN, def.type)
        assertTrue(720 in def.options)
        assertTrue(1080 in def.options)
        assertTrue(1840 in def.options)
        assertTrue(2160 in def.options)
        assertEquals("1840 px", def.displayValue(1840))
    }
}
