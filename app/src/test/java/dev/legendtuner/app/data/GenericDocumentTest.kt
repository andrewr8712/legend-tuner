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
