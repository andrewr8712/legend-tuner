package dev.legendtuner.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfigDocumentTest {

    private fun sample(): ConfigDocument {
        val bytes = javaClass.classLoader!!.getResourceAsStream("preferences-sample.xml")!!.readBytes()
        return ConfigDocument.parse(bytes)
    }

    @Test
    fun parsesTheRealRegistryAndDetectsTheActivePreset() {
        val doc = sample()
        doc.validate()
        assertEquals(Preset.GRAPHICS, doc.activePreset())
        assertEquals(1, doc.int(ConfigDocument.GRAPHICS_SETTINGS_PATH, "DevicePreset"))
        assertEquals(60, doc.presetValue(Preset.GRAPHICS, "FramerateLimit")?.firstIntOrNull)
        assertEquals(3, doc.presetValue(Preset.GRAPHICS, "TextureQuality")?.firstIntOrNull)
        assertNotNull(doc.value(ConfigDocument.SETUP_PATH, "GameVersionString"))
    }

    @Test
    fun roundTripKeepsEveryPathNameTypeAndValue() {
        val doc = sample()
        val before = doc.allValues()
        val reparsed = ConfigDocument.parse(doc.serialize())
        val after = reparsed.allValues()
        assertEquals(before.size, after.size)
        before.zip(after).forEach { (a, b) ->
            assertEquals(a.path, b.path)
            assertEquals(a.name, b.name)
            assertEquals(a.type, b.type)
            assertEquals(a.text, b.text)
        }
    }

    @Test
    fun editingTheFirstPresetTokenPreservesTheTrailingTokens() {
        val doc = sample()
        assertEquals("3, 0, 0", doc.presetValue(Preset.GRAPHICS, "VehicleReflections")?.text)
        assertTrue(doc.setPresetFirstToken(Preset.GRAPHICS, "VehicleReflections", 2))
        assertEquals("2, 0, 0", doc.presetValue(Preset.GRAPHICS, "VehicleReflections")?.text)
    }

    @Test
    fun applyingAPresetMirrorsLiveIntegersAndSetsDevicePreset() {
        val doc = sample()
        assertTrue(doc.setPresetFirstToken(Preset.PERFORMANCE, "Shadows", 3))
        doc.applyPresetToLive(Preset.PERFORMANCE)
        assertEquals(Preset.PERFORMANCE, doc.activePreset())
        assertEquals(3, doc.int(ConfigDocument.GRAPHICS_SETTINGS_PATH, "Shadows"))
        assertEquals(2, doc.int(ConfigDocument.GRAPHICS_SETTINGS_PATH, "DevicePreset"))
    }

    @Test
    fun unknownValuesAreRejectedInsteadOfCreated() {
        val doc = sample()
        assertNull(doc.value(ConfigDocument.GRAPHICS_SETTINGS_PATH, "ThisDoesNotExist"))
        assertTrue(!doc.setValue(ConfigDocument.GRAPHICS_SETTINGS_PATH, "ThisDoesNotExist", "1"))
    }

    @Test(expected = ConfigParseException::class)
    fun malformedXmlThrowsAParseException() {
        ConfigDocument.parse("<registry><key></registry>")
    }
}
