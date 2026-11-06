package dev.legendtuner.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.legendtuner.app.AppState
import dev.legendtuner.app.LoadedConfig
import dev.legendtuner.app.TitleStatus
import dev.legendtuner.app.access.AccessMode
import dev.legendtuner.app.access.FileAccess
import dev.legendtuner.app.access.LocatedFile
import dev.legendtuner.app.data.ConfigDocument
import dev.legendtuner.app.data.Control
import dev.legendtuner.app.data.ControlChoice
import dev.legendtuner.app.data.ControlKind
import dev.legendtuner.app.data.FeralTitle
import dev.legendtuner.app.data.TitleCatalog
import dev.legendtuner.app.ui.theme.LegendTunerTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PickerEditorSmokeTest {

    @get:Rule
    val compose = createComposeRule()

    private val title = FeralTitle(
        id = "test-title",
        label = "A Very Long Test Title That Must Stay Reachable",
        packageName = "com.example.test"
    )
    private val root = listOf("HKEY_CURRENT_USER", "Software", "Test", "Setup")
    private val catalog = TitleCatalog(
        groups = listOf("Display"),
        controls = listOf(
            Control(
                key = "Resolution",
                label = "Resolution",
                description = "The stored output width and height.",
                group = "Display",
                kind = ControlKind.RESOLUTION,
                choices = listOf(ControlChoice("1280x800", "1280 × 800"))
            )
        )
    )
    private val document = ConfigDocument.parse(
        """
        <registry>
          <key name="HKEY_CURRENT_USER">
            <key name="Software">
              <key name="Test">
                <key name="Setup">
                  <value name="ScreenW" type="integer">1280</value>
                  <value name="ScreenH" type="integer">800</value>
                </key>
              </key>
            </key>
          </key>
        </registry>
        """.trimIndent()
    )

    @Test
    fun pickerToEditorSaveAndBack() {
        var screen by mutableStateOf("picker")
        var saved = false
        var imported = false
        val pickerState = AppState(
            loading = false,
            titleStatuses = listOf(TitleStatus(title = title, installed = true, readable = true))
        )
        val editorState = AppState(
            loading = false,
            config = LoadedConfig(document, FakeAccess, FakeAccess.file, document.serialize()),
            activeTitle = title,
            titleStatuses = emptyList(),
            dirty = true
        )

        compose.setContent {
            LegendTunerTheme {
                if (screen == "picker") {
                    PickerScreen(
                        state = pickerState,
                        wide = false,
                        onOpenTitle = { screen = "editor" },
                        onOpenMissing = {},
                        onOpenFile = { imported = true },
                        onOpenAccess = {},
                        onRefresh = {}
                    )
                } else {
                    TitleEditorScreen(
                        state = editorState,
                        catalog = catalog,
                        root = root,
                        activeFamilyId = null,
                        dockedInspector = false,
                        twoColumns = false,
                        onSelect = {},
                        onUpdate = { _, _ -> },
                        onSelectFamily = {},
                        onSave = { saved = true },
                        onDiscard = { screen = "picker" },
                        onOpenAccess = {}
                    )
                }
            }
        }

        awaitPicker()
        compose.onNodeWithContentDescription(title.label, substring = true).assertExists()
        compose.onNodeWithTag("title-test-title").performClick()
        compose.onNodeWithTag("editor").assertExists()
        compose.onNodeWithTag("save").assertIsEnabled().performClick()
        assertTrue(saved)
        compose.onNodeWithTag("discard").performClick()
        compose.onNodeWithTag("picker").assertExists()
        assertTrue(!imported)
    }

    @Test
    fun emptyPickerOffersManualFile() {
        var imported = false
        compose.setContent {
            LegendTunerTheme {
                PickerScreen(
                    state = AppState(
                        loading = false,
                        titleStatuses = listOf(TitleStatus(title = title, installed = false, readable = false))
                    ),
                    wide = false,
                    onOpenTitle = {},
                    onOpenMissing = {},
                    onOpenFile = { imported = true },
                    onOpenAccess = {},
                    onRefresh = {}
                )
            }
        }

        awaitPicker()
        compose.onNodeWithTag("empty-state").assertExists()
        compose.onNodeWithTag("empty-open-file").performClick()
        assertTrue(imported)
    }

    private fun awaitPicker() {
        compose.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                compose.onAllNodesWithTag("picker").fetchSemanticsNodes().isNotEmpty()
            }.getOrDefault(false)
        }
    }

    private object FakeAccess : FileAccess {
        override val mode = AccessMode.MANUAL
        val file = LocatedFile("test://preferences", "preferences.xml", "Manual file", 1L, 0L)
        override suspend fun locate() = file
        override suspend fun read(path: String) = ByteArray(0)
        override suspend fun write(path: String, bytes: ByteArray) = Unit
        override suspend fun forceStop() = false
    }
}
