package dev.legendtuner.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GenericDocumentTest {

    private val fixtures = mapOf(
        "grid-autosport" to "grid-autosport-preferences-sample.xml",
        "alien-isolation" to "alien-isolation-preferences-sample.xml",
        "saints-row-3" to "saintsrow3-preferences-sample.xml",
        "napoleon" to "napoleon-preferences-sample.xml",
        "hitman-blood-money" to "hitman-blood-money-preferences-sample.xml",
        "lara-croft-gol" to "lara-croft-gol-preferences-sample.xml",
        "lara-croft-too" to "lara-croft-too-preferences-sample.xml"
    )

    private fun load(name: String): ConfigDocument {
        val bytes = javaClass.classLoader!!.getResourceAsStream(name)?.readBytes()
            ?: error("Missing test fixture $name")
        return ConfigDocument.parse(bytes)
    }

    @Test
    fun everyCatalogueFixtureParsesItsRootAndDisplaysEveryControl() {
        fixtures.forEach { (id, file) ->
            val title = FeralTitles.ALL.first { it.id == id }
            val catalog = title.titleCatalog!!
            val root = title.valueRoot!!
            val doc = load(file)

            doc.validateRegistry()
            assertTrue("$id has no values", doc.allValues().isNotEmpty())
            assertNotNull("$id has no registry root in $file", doc.key(root))

            catalog.controls.forEach { control ->
                // Display must never throw for a missing, laddered, or unexpected value.
                TitleCatalogEngine.display(doc, root, catalog, control)
            }
        }
    }

    @Test
    fun alienFpsCapUsesTheIndirectXEngineRoot() {
        val title = FeralTitles.ALL.first { it.id == "alien-isolation" }
        val catalog = title.titleCatalog!!
        val root = title.valueRoot!!
        val control = catalog.find("MaxFramesPerSecond")
        assertNotNull("Alien must expose the engine FPS cap", control)
        val doc = load("alien-isolation-preferences-sample.xml")

        val presetCap = listOf(
            "HKEY_CURRENT_USER", "Software", "Feral Interactive", "Alien Isolation",
            "Setup", "PerformancePresets"
        )
        assertEquals("33", TitleCatalogEngine.value(doc, root, catalog, control!!)?.text)
        assertEquals("33", doc.value(presetCap, "GraphicsMaxFramerate")?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, control, "60"))
        assertEquals("60", TitleCatalogEngine.value(doc, root, catalog, control)?.text)
        assertEquals("60", doc.value(presetCap, "GraphicsMaxFramerate")?.text)
    }

    @Test
    fun napoleonFpsCapsUseTheIndirectXEngineRoot() {
        val title = FeralTitles.ALL.first { it.id == "napoleon" }
        val catalog = title.titleCatalog!!
        val root = title.valueRoot!!
        val battle = catalog.find("FramerateManagerBattleResponsiveFPS")
        val campaign = catalog.find("FramerateManagerCampaignResponsiveFPS")
        assertNotNull("Napoleon must expose the battle frame cap", battle)
        assertNotNull("Napoleon must expose the campaign frame cap", campaign)
        val doc = load("napoleon-preferences-sample.xml")

        assertEquals("30", TitleCatalogEngine.value(doc, root, catalog, battle!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, battle, "60"))
        assertEquals("60", TitleCatalogEngine.value(doc, root, catalog, battle)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, campaign!!, "60"))
        assertEquals("60", TitleCatalogEngine.value(doc, root, catalog, campaign)?.text)
    }

    @Test
    fun laraTooExposesTheFullscreenResolutionPair() {
        val title = FeralTitles.ALL.first { it.id == "lara-croft-too" }
        val catalog = title.titleCatalog!!
        val root = title.valueRoot!!
        val control = catalog.find("Resolution")
        assertNotNull("Lara Croft: Temple of Osiris must expose Resolution", control)
        val doc = load("lara-croft-too-preferences-sample.xml")

        assertEquals("1728x1080", TitleCatalogEngine.resolutionToken(doc, root, catalog, control!!))
        assertTrue(TitleCatalogEngine.writeResolution(doc, root, catalog, control, "1280x720"))
        assertEquals("1280x720", TitleCatalogEngine.resolutionToken(doc, root, catalog, control))
    }

    @Test
    fun napoleonExposesRenderHeightAndResolutionScale() {
        val title = FeralTitles.ALL.first { it.id == "napoleon" }
        val catalog = title.titleCatalog!!
        val root = title.valueRoot!!
        val height = catalog.find("FixedScreenHeight")
        val scale = catalog.find("GameResolutionMultiplier")
        assertNotNull("Napoleon must expose render height", height)
        assertNotNull("Napoleon must expose resolution scale", scale)
        val doc = load("napoleon-preferences-sample.xml")

        assertTrue(TitleCatalogEngine.write(doc, root, catalog, height!!, "720"))
        assertEquals("720", TitleCatalogEngine.value(doc, root, catalog, height)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, scale!!, "0.750000"))
        assertEquals("0.750000", TitleCatalogEngine.value(doc, root, catalog, scale)?.text)
    }

    @Test
    fun companyOfHeroesExposesDisplayGraphicsAndFrameControls() {
        val title = FeralTitles.ALL.first { it.id == "company-of-heroes" }
        val catalog = title.titleCatalog!!
        val root = title.valueRoot!!
        val doc = load("company-of-heroes-preferences-sample.xml")
        val fullscreen = catalog.find("FullScreen")
        val aa = catalog.find("antialiasing")
        val fps = catalog.find("MaxFramesPerSecond")
        assertNotNull("Company of Heroes must expose fullscreen", fullscreen)
        assertNotNull("Company of Heroes must expose anti-aliasing", aa)
        assertNotNull("Company of Heroes must expose the frame cap", fps)

        assertEquals("1", TitleCatalogEngine.value(doc, root, catalog, fullscreen!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, fullscreen, "0"))
        assertEquals("0", TitleCatalogEngine.value(doc, root, catalog, fullscreen)?.text)

        assertEquals("0", TitleCatalogEngine.value(doc, root, catalog, aa!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, aa, "1"))
        assertEquals("1", TitleCatalogEngine.value(doc, root, catalog, aa)?.text)

        assertEquals("30", TitleCatalogEngine.value(doc, root, catalog, fps!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, fps, "60"))
        assertEquals("60", TitleCatalogEngine.value(doc, root, catalog, fps)?.text)
    }

    @Test
    fun tombRaiderExposesPresetDisplayAndEngineControls() {
        val title = FeralTitles.ALL.first { it.id == "tomb-raider" }
        val catalog = title.titleCatalog!!
        val root = title.valueRoot!!
        val doc = load("tomb-raider-preferences-sample.xml")
        val resolution = catalog.find("Resolution")
        val shadow = catalog.find("ShadowMode")
        val fps = catalog.find("MaxFramesPerSecond")
        assertNotNull("Tomb Raider must expose resolution", resolution)
        assertNotNull("Tomb Raider must expose shadow mode", shadow)
        assertNotNull("Tomb Raider must expose the engine frame cap", fps)
        assertEquals("graphics", catalog.activeFamilyId(doc))

        assertEquals("1526x1080", TitleCatalogEngine.display(doc, root, catalog, resolution!!))
        assertTrue(TitleCatalogEngine.writeResolution(doc, root, catalog, resolution, "1920x1080"))
        assertEquals("1920x1080", TitleCatalogEngine.display(doc, root, catalog, resolution))
        assertEquals("1920", doc.value(root, "ScreenW")?.text)
        assertEquals("1080", doc.value(root, "ScreenH")?.text)

        val graphics = catalog.withFamily(catalog.family("graphics"))
        assertEquals("1", TitleCatalogEngine.value(doc, root, graphics, shadow!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, graphics, shadow, "2"))
        assertEquals("2", TitleCatalogEngine.value(doc, root, graphics, shadow)?.text)

        assertEquals("60", TitleCatalogEngine.value(doc, root, catalog, fps!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, fps, "40"))
        assertEquals("40", TitleCatalogEngine.value(doc, root, catalog, fps)?.text)
    }

    @Test
    fun hitmanAbsolutionExposesProfileDisplayAndFrameControls() {
        val title = FeralTitles.ALL.first { it.id == "hitman-absolution" }
        val catalog = title.titleCatalog!!
        val root = title.valueRoot!!
        val doc = load("hitman-absolution-preferences-sample.xml")
        val resolution = catalog.find("Resolution")
        val shadow = catalog.find("ShadowQuality")
        val fps = catalog.find("FrameRateLimit")
        assertNotNull("Hitman: Absolution must expose resolution", resolution)
        assertNotNull("Hitman: Absolution must expose shadow quality", shadow)
        assertNotNull("Hitman: Absolution must expose the frame limit", fps)

        assertEquals("1526x1080", TitleCatalogEngine.display(doc, root, catalog, resolution!!))
        assertTrue(TitleCatalogEngine.writeResolution(doc, root, catalog, resolution, "1920x1080"))
        assertEquals("1920x1080", TitleCatalogEngine.display(doc, root, catalog, resolution))
        assertEquals("1920", doc.value(root, "ResolutionWidth")?.text)
        assertEquals("1080", doc.value(root, "ResolutionHeight")?.text)

        val graphics = catalog.withFamily(catalog.family("graphics"))
        assertEquals("4", TitleCatalogEngine.value(doc, root, graphics, shadow!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, graphics, shadow, "3"))
        assertEquals("3", TitleCatalogEngine.value(doc, root, graphics, shadow)?.text)

        assertEquals("60", TitleCatalogEngine.value(doc, root, catalog, fps!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, fps, "40"))
        assertEquals("40", TitleCatalogEngine.value(doc, root, catalog, fps)?.text)
    }

    @Test
    fun railroadsExposesDisplayQualityAndFrameCapControls() {
        val title = FeralTitles.ALL.first { it.id == "railroads" }
        val catalog = title.titleCatalog!!
        val root = title.valueRoot!!
        val doc = load("railroads-preferences-sample.xml")
        val resolution = catalog.find("Resolution")
        val shader = catalog.find("ShaderQuality")
        val fps = catalog.find("MaxFramesPerSecond")
        assertNotNull("Railroads must expose resolution", resolution)
        assertNotNull("Railroads must expose shader quality", shader)
        assertNotNull("Railroads must expose the frame cap", fps)

        assertEquals("1440x900", TitleCatalogEngine.display(doc, root, catalog, resolution!!))
        assertTrue(TitleCatalogEngine.writeResolution(doc, root, catalog, resolution, "1920x1080"))
        assertEquals("1920x1080", TitleCatalogEngine.display(doc, root, catalog, resolution))
        assertEquals("1920", doc.value(root, "ScreenW")?.text)
        assertEquals("1080", doc.value(root, "ScreenH")?.text)

        assertEquals("2", TitleCatalogEngine.value(doc, root, catalog, shader!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, shader, "1"))
        assertEquals("1", TitleCatalogEngine.value(doc, root, catalog, shader)?.text)

        assertEquals("40", TitleCatalogEngine.value(doc, root, catalog, fps!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, fps, "60"))
        assertEquals("60", TitleCatalogEngine.value(doc, root, catalog, fps)?.text)
    }

    @Test
    fun saintsRowExposesMotionAimAndGyroControls() {
        val title = FeralTitles.ALL.first { it.id == "saints-row-3" }
        val catalog = title.titleCatalog!!
        val root = title.valueRoot!!
        val motion = catalog.find("Enabled")
        val gyro = catalog.find("VcpEnableGyroControls")
        val sensitivity = catalog.find("SensitivityX")
        assertNotNull("Saints Row must expose Motion Aim", motion)
        assertNotNull("Saints Row must expose Gyro Controls", gyro)
        assertNotNull("Saints Row must expose Motion Aim sensitivity", sensitivity)
        val doc = load("saintsrow3-preferences-sample.xml")

        assertEquals("0", TitleCatalogEngine.value(doc, root, catalog, motion!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, motion, "1"))
        assertEquals("1", TitleCatalogEngine.value(doc, root, catalog, motion)?.text)

        assertEquals("0", TitleCatalogEngine.value(doc, root, catalog, gyro!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, gyro, "1"))
        assertEquals("1", TitleCatalogEngine.value(doc, root, catalog, gyro)?.text)

        assertEquals("400.000000", TitleCatalogEngine.value(doc, root, catalog, sensitivity!!)?.text)
        assertTrue(TitleCatalogEngine.write(doc, root, catalog, sensitivity, "600.000000"))
        assertEquals("600.000000", TitleCatalogEngine.value(doc, root, catalog, sensitivity)?.text)
        val motionPath = listOf(
            "HKEY_CURRENT_USER", "Software", "Feral Interactive", "Saints Row The Third",
            "Setup", "MotionAimSettings"
        )
        assertEquals("600.000000", doc.value(motionPath, "SensitivityY")?.text)
    }

    @Test
    fun invertedHitmanToggleWritesTheRawInverseAndDisplaysTheLogicalValue() {
        val title = FeralTitles.ALL.first { it.id == "hitman-blood-money" }
        val catalog = title.titleCatalog!!.withFamily(title.titleCatalog!!.family("graphics"))
        val control = catalog.find("DisableBloom")!!
        val root = title.valueRoot!!
        val doc = load("hitman-blood-money-preferences-sample.xml")

        assertTrue(TitleCatalogEngine.write(doc, root, catalog, control, "0"))
        assertEquals(
            "1, 1, 1",
            TitleCatalogEngine.value(doc, root, catalog, control)?.text
        )
        assertEquals("Off", TitleCatalogEngine.display(doc, root, catalog, control))
    }

    @Test
    fun writeNeverCreatesAMissingKey() {
        val title = FeralTitles.ALL.first { it.id == "alien-isolation" }
        val catalog = title.titleCatalog!!
        val root = title.valueRoot!!
        val missing = Control(
            "ThisKeyDoesNotExist",
            "Missing",
            "Fixture guard",
            catalog.groups.first(),
            ControlKind.TOGGLE,
            listOf(ControlChoice("0", "Off"), ControlChoice("1", "On"))
        )

        assertFalse(TitleCatalogEngine.write(load("alien-isolation-preferences-sample.xml"), root, catalog, missing, "1"))
    }

    @Test
    fun resolutionWritesBothScreenValuesWhenTheFixtureHasThePair() {
        val title = FeralTitles.ALL.first { it.id == "grid-autosport" }
        val catalog = title.titleCatalog!!
        val resolution = catalog.controls.firstOrNull { it.kind == ControlKind.RESOLUTION } ?: return
        val root = title.valueRoot!!
        val doc = load("grid-autosport-preferences-sample.xml")

        val current = TitleCatalogEngine.resolutionToken(doc, root, catalog, resolution) ?: return
        assertNotNull(current)
        assertTrue(TitleCatalogEngine.writeResolution(doc, root, catalog, resolution, "1280x720"))
        assertEquals("1280x720", TitleCatalogEngine.resolutionToken(doc, root, catalog, resolution))
    }
}
