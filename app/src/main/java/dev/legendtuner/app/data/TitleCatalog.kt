package dev.legendtuner.app.data

/**
 * A guided control for a non-GRID Feral title. [key] is the registry value name; [tokens] map the
 * stored text to a human label. Controls are read-only unless a catalogue row was verified against
 * a real device file — nothing here creates keys that the game did not write itself.
 */
enum class ControlKind { TOGGLE, CHOICE, STEPPER, RESOLUTION, READONLY }

data class ControlChoice(val token: String, val label: String)

data class Control(
    val key: String,
    val label: String,
    val description: String,
    val group: String,
    val kind: ControlKind,
    val choices: List<ControlChoice> = emptyList(),
    val min: Int = 0,
    val max: Int = 0,
    val suffix: String = "",
    val note: String? = null,
    /** Hitman stores "a, b, c" per preset; uniform triples are editable, ladders are not. */
    val triple: Boolean = false,
    /** Keys named Disable* store the inverse of the label. */
    val invert: Boolean = false,
    /** Engine-level keys (render height) have no preset suffix even when a family is selected. */
    val ignoreSuffix: Boolean = false,
    /** Keys that live outside the preset block (ScreenW/ScreenH) skip the family path. */
    val ignoreFamilyPath: Boolean = false,
    /** Full path from HKEY_CURRENT_USER for engine controls that live in another registry subtree. */
    val absolutePath: List<String>? = null,
    /** Resolution controls can target a title-specific pair instead of ScreenW/ScreenH. */
    val widthKey: String = "ScreenW",
    val heightKey: String = "ScreenH"
) {
    /**
     * The token the user is actually choosing after [invert] is applied. The stored
     * value stays raw in the document; only the UI compares and labels the logical state.
     */
    fun effectiveToken(token: String?): String? = token?.let { if (invert) flip(it) else it }

    fun labelFor(token: String?, raw: String? = null): String {
        if (token == null) return raw ?: "—"
        return when (kind) {
            ControlKind.TOGGLE, ControlKind.CHOICE -> {
                val effective = effectiveToken(token) ?: token
                choices.firstOrNull { it.token.equals(effective, ignoreCase = true) }?.label ?: token
            }
            ControlKind.STEPPER -> "$token$suffix"
            ControlKind.RESOLUTION -> token
            ControlKind.READONLY -> token
        }
    }
}

private fun flip(token: String): String = when (token.trim()) {
    "0" -> "1"
    "1" -> "0"
    else -> token
}

/** One selectable setting set inside a title: a preset block, a prefix or a suffix family. */
data class PresetFamily(
    val id: String,
    val label: String,
    val keyPrefix: String = "",
    val stripPrefix: String = "",
    val keySuffix: String = "",
    val pathSuffix: List<String> = emptyList()
)

/**
 * [keyPrefix] rewrites the value name for titles that keep several copies of the same setting set.
 * GRID Autosport's live block is `gfxconfig_*`; its cached sets are `gfxconfig_high_*` and
 * `gfxconfigpowersaver_*`, so a prefix swap is enough to address each one.
 */
data class TitleCatalog(
    val groups: List<String>,
    val controls: List<Control>,
    val keyPrefix: String = "",
    val stripPrefix: String = "",
    val keySuffix: String = "",
    /** Keys between the title's registry root and the values, e.g. `Setup/GraphicsPreset`. */
    val pathSuffix: List<String> = emptyList(),
    /** Path contributed by the selected preset family; engine-level controls can skip it. */
    val familyPathSuffix: List<String> = emptyList(),
    val families: List<PresetFamily> = emptyList()
) {
    fun controlsFor(group: String): List<Control> = controls.filter { it.group == group }

    fun find(key: String): Control? = controls.firstOrNull { it.key == key }

    fun keyFor(control: Control): String =
        keyPrefix + control.key.removePrefix(stripPrefix) + if (control.ignoreSuffix) "" else keySuffix

    fun pathFor(control: Control): List<String> =
        pathSuffix + if (control.ignoreFamilyPath) emptyList() else familyPathSuffix

    fun family(id: String?): PresetFamily? =
        families.firstOrNull { it.id == id } ?: families.firstOrNull()

    /** Applies a preset family on top of this catalogue's base paths. */
    fun withFamily(family: PresetFamily?): TitleCatalog {
        if (family == null) return this
        return copy(
            keyPrefix = family.keyPrefix.ifEmpty { keyPrefix },
            stripPrefix = family.stripPrefix.ifEmpty { stripPrefix },
            keySuffix = family.keySuffix,
            familyPathSuffix = family.pathSuffix
        )
    }
}

object TitleCatalogEngine {
    /** Resolves an absolute engine path when present, otherwise the title root plus catalogue path. */
    fun targetPath(root: List<String>, catalog: TitleCatalog, control: Control): List<String> =
        control.absolutePath ?: root + catalog.pathFor(control)

    fun value(doc: ConfigDocument, root: List<String>, catalog: TitleCatalog, control: Control): ConfigDocument.ConfigValue? =
        doc.value(targetPath(root, catalog, control), catalog.keyFor(control))

    fun tokens(raw: String?): List<String> =
        raw?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

    fun isUniform(raw: String?): Boolean {
        val list = tokens(raw)
        return list.isNotEmpty() && list.distinct().size == 1
    }

    /** A single token for editable controls; null when a triple is a ladder rather than one value. */
    fun token(doc: ConfigDocument, root: List<String>, catalog: TitleCatalog, control: Control): String? {
        val raw = value(doc, root, catalog, control)?.text ?: return null
        if (!control.triple) return raw.trim()
        return if (isUniform(raw)) tokens(raw).first() else null
    }

    fun display(doc: ConfigDocument, root: List<String>, catalog: TitleCatalog, control: Control): String {
        if (control.kind == ControlKind.RESOLUTION) {
            return resolutionToken(doc, root, catalog, control) ?: "—"
        }
        val raw = value(doc, root, catalog, control)?.text
        val token = token(doc, root, catalog, control)
        if (token == null) return raw?.trim() ?: "—"
        return control.labelFor(token, raw)
    }

    /** A configured width/height pair, surfaced as one "1280x800" token. */
    fun resolutionToken(doc: ConfigDocument, root: List<String>, catalog: TitleCatalog, control: Control): String? {
        val path = targetPath(root, catalog, control)
        val width = doc.value(path, control.widthKey)?.firstIntOrNull ?: return null
        val height = doc.value(path, control.heightKey)?.firstIntOrNull ?: return null
        return "${width}x$height"
    }

    fun writeResolution(
        doc: ConfigDocument,
        root: List<String>,
        catalog: TitleCatalog,
        control: Control,
        token: String
    ): Boolean {
        val parts = token.lowercase().split('x')
        if (parts.size != 2) return false
        val width = parts[0].trim().toIntOrNull() ?: return false
        val height = parts[1].trim().toIntOrNull() ?: return false
        val path = targetPath(root, catalog, control)
        if (doc.value(path, control.widthKey) == null || doc.value(path, control.heightKey) == null) return false
        val okWidth = doc.setValue(path, control.widthKey, width.toString())
        val okHeight = doc.setValue(path, control.heightKey, height.toString())
        return okWidth && okHeight
    }

    fun write(
        doc: ConfigDocument,
        root: List<String>,
        catalog: TitleCatalog,
        control: Control,
        token: String
    ): Boolean {
        val stored = if (control.invert) flip(token) else token
        val text = if (control.triple) "$stored, $stored, $stored" else stored
        return doc.setValue(targetPath(root, catalog, control), catalog.keyFor(control), text)
    }
}

private fun quality5(prefix: String = "q"): List<ControlChoice> = listOf(
    ControlChoice("ultralow", "Ultra Low"),
    ControlChoice("low", "Low"),
    ControlChoice("medium", "Medium"),
    ControlChoice("high", "High"),
    ControlChoice("ultra", "Ultra")
)

private fun onOffText(): List<ControlChoice> = listOf(
    ControlChoice("off", "Off"),
    ControlChoice("on", "On")
)

internal val commonScreenHeights = listOf(480, 540, 600, 720, 768, 800, 900, 1080, 1200, 1440, 1600, 1800, 2160)
internal val commonFrameRates = listOf(24, 30, 40, 45, 48, 60, 72, 90, 120, 144)

internal fun heightChoices(): List<ControlChoice> =
    commonScreenHeights.map { ControlChoice(it.toString(), "$it px") }

internal fun resolutionChoices(): List<ControlChoice> = listOf(
    ControlChoice("1280x720", "1280 × 720"),
    ControlChoice("1280x800", "1280 × 800"),
    ControlChoice("1600x900", "1600 × 900"),
    ControlChoice("1920x1080", "1920 × 1080"),
    ControlChoice("1920x1200", "1920 × 1200"),
    ControlChoice("2560x1440", "2560 × 1440"),
    ControlChoice("2560x1600", "2560 × 1600")
)

internal fun fpsChoices(): List<ControlChoice> =
    commonFrameRates.map { ControlChoice(it.toString(), "$it fps") }

private fun qualityLadder(): List<ControlChoice> = listOf(
    ControlChoice("0", "Very Low"),
    ControlChoice("1", "Low"),
    ControlChoice("2", "Medium"),
    ControlChoice("3", "High"),
    ControlChoice("4", "Ultra")
)

object GridAutosportCatalog {
    private const val IMAGE = "Image quality"
    private const val DETAIL = "Detail"
    private const val LIGHT = "Lighting & atmosphere"
    private const val PERF = "Performance"

    private fun q(key: String, label: String, description: String, group: String = IMAGE, note: String? = null) =
        Control(key, label, description, group, ControlKind.CHOICE, quality5(), note = note)

    private fun toggle(key: String, label: String, description: String, group: String = IMAGE) =
        Control(key, label, description, group, ControlKind.TOGGLE, onOffText())

    val catalog = TitleCatalog(
        groups = listOf(IMAGE, DETAIL, LIGHT, PERF),
        families = listOf(
            PresetFamily("current", "Current"),
            PresetFamily("high", "Performance cache", keyPrefix = "gfxconfig_high_", stripPrefix = "gfxconfig_"),
            PresetFamily("power", "Power saver cache", keyPrefix = "gfxconfigpowersaver_", stripPrefix = "gfxconfig_")
        ),
        controls = listOf(
            q(
                "gfxconfig_shadows", "Shadows",
                "Dynamic shadow maps for cars, trackside furniture and the crowd. Higher tiers raise map resolution, cascade distance and the number of casters."
            ),
            q(
                "gfxconfig_shaders", "Shaders",
                "Shading tier for track and car materials: surface response, specular detail and lighting model complexity."
            ),
            q(
                "gfxconfig_textures", "Textures",
                "Texture resolution and mip selection for tracks, cars and props. High needs the HQ texture packs and costs VRAM and bandwidth."
            ),
            q(
                "gfxconfig_car_reflection", "Car reflections",
                "Environment reflection resolution and update rate on car paint. Visible on every car in frame."
            ),
            q(
                "gfxconfig_mirrors", "Mirrors",
                "Planar mirror pass for the rear-view mirrors. Renders the scene a second time, so it scales with track complexity."
            ),
            q(
                "gfxconfig_dynamic_ambient_occ", "Ambient occlusion",
                "Screen-space contact shadows in creases and under cars. Noticeable on undersides and kerbs."
            ),
            toggle(
                "gfxconfig_dynamic_ambient_occ_soft", "Soft ambient occlusion",
                "Blurs the AO result for a softer, wider contact shadow. Small extra cost over ambient occlusion."
            ),
            toggle(
                "gfxconfig_avsm", "AVSM",
                "Adaptive volumetric shadow maps for particle shadows. Expensive, and off by default on this device."
            ),
            toggle(
                "gfxconfig_intel_aoit", "Intel AOIT",
                "Order-independent transparency for foliage and particles on supported GPUs. Vendor-specific; ignored elsewhere."
            ),
            q(
                "gfxconfig_anisotropic_filtering", "Anisotropic filtering",
                "Texture sharpness on surfaces seen at a grazing angle, such as the road ahead. Cheap, so raise it first."
            ),
            Control(
                "gfxconfig_multisampling", "Anti-aliasing",
                "MSAA sample count for geometric edges. 8x is a large bandwidth cost on a tablet.",
                IMAGE, ControlKind.CHOICE,
                listOf(
                    ControlChoice("off", "Off"),
                    ControlChoice("4xmsaa", "4x"),
                    ControlChoice("8xmsaa", "8x")
                )
            ),

            q(
                "gfxconfig_vehicles", "Vehicles",
                "Opponent car mesh detail, materials and LOD bias. Scales with grid size.",
                DETAIL
            ),
            q(
                "gfxconfig_trees", "Trees",
                "Track-side vegetation density, LOD and billboarding distance. Alpha-tested foliage is fill-rate sensitive.",
                DETAIL
            ),
            q(
                "gfxconfig_objects", "Objects",
                "Props, barriers, pit equipment and trackside clutter. Draw-call heavy in pit lanes.",
                DETAIL
            ),
            q(
                "gfxconfig_track", "Track detail",
                "Road surface decals, markings and track-side detail layers. Small cost, visible at speed.",
                DETAIL
            ),
            q(
                "gfxconfig_crowd", "Crowd",
                "Spectator count, animation rate and draw distance. CPU-heavy on grandstands; low tiers thin the crowd.",
                DETAIL
            ),
            q(
                "gfxconfig_groundCover", "Ground cover",
                "Grass, gravel scatter and small ground clutter. Cheap at low tiers; fills gaps between track and scenery.",
                DETAIL
            ),
            q(
                "gfxconfig_water", "Water",
                "Water shading, reflections and tessellation on wet circuits. Only visible in wet races.",
                DETAIL
            ),
            q(
                "gfxconfig_cloth", "Cloth",
                "Flags, banners and soft-body cloth shading and simulation detail. Minor unless many flags are on screen.",
                DETAIL
            ),
            Control(
                "gfxconfig_skidmarks", "Skidmarks",
                "Rubber marks left on the road and how long they persist. Persistent decals cost memory.",
                DETAIL, ControlKind.TOGGLE, onOffText()
            ),
            q(
                "gfxconfig_particles", "Particles",
                "Density and softness of dust, smoke, sparks and tyre effects. Spikes at race starts and collisions.",
                DETAIL
            ),

            q(
                "gfxconfig_night_lighting", "Night lighting",
                "Lightmap and headlight quality for night races. No effect in daylight.",
                LIGHT
            ),
            toggle(
                "gfxconfig_global_illumination", "Global illumination",
                "Indirect light from track and sky, softening shadowed areas. Medium GPU cost, large visual difference.",
                LIGHT
            ),
            toggle(
                "gfxconfig_advanced_lighting", "Advanced lighting",
                "Extra per-light passes for headlights, brake lights and trackside lamps. Cost grows with the number of lights.",
                LIGHT
            ),
            toggle(
                "gfxconfig_advanced_fog", "Advanced fog",
                "Layered distance fog and atmospheric scattering. Cheap, but washes out distance at low tiers.",
                LIGHT
            ),

            Control(
                "gfxconfig_max_fps", "Frame rate limit",
                "Cap applied by this graphics set. The live set and the cached sets can hold different caps.",
                PERF, ControlKind.CHOICE,
                listOf(ControlChoice("30", "30 fps"), ControlChoice("60", "60 fps")),
                suffix = " fps"
            ),
            q(
                "gfxconfig_ai_count", "AI count",
                "Density of AI-car visual and audio updates. The least certain row here: confirm in-game before trusting the tier names.",
                PERF,
                note = "Semantics unverified — the tier labels are inferred from the key name."
            ),
            q(
                "gfxconfig_voice_count", "Voice count",
                "Maximum simultaneous audio voices for engines, tyres and ambience. Audio rather than rendering cost.",
                PERF
            ),
            q(
                "gfxconfig_second_screen", "Second screen",
                "Detail level for the second-screen view. No effect in single-screen use.",
                PERF
            )
        )
    )
}

object AlienIsolationCatalog {
    private const val IMAGE = "Image quality"
    private const val EFFECTS = "Effects"
    private const val DISPLAY = "Display"
    private val DIRECT3D_CONFIG = listOf("HKEY_CURRENT_USER", "Software", "IndirectX", "Direct3D", "Config")

    private fun toggle(key: String, label: String, description: String, group: String) =
        Control(
            key, label, description, group, ControlKind.TOGGLE,
            listOf(ControlChoice("0", "Off"), ControlChoice("1", "On"))
        )

    private fun ladder(key: String, label: String, description: String, group: String = IMAGE) =
        Control(key, label, description, group, ControlKind.CHOICE, qualityLadder())

    val catalog = TitleCatalog(
        groups = listOf(IMAGE, EFFECTS, DISPLAY),
        controls = listOf(
            Control(
                "VideoSetting_Texture Filtering", "Texture Filtering",
                "Adjust the texture filtering quality. The stored number indexes the game's filter list; the names are the game's own.",
                IMAGE, ControlKind.STEPPER, min = 0, max = 9,
                note = "Index mapping unverified — Bilinear, Anisotropic 1x-16x and Box/Gaussian share this key."
            ),
            ladder(
                "VideoSetting_ShadowMapping", "Shadow Mapping",
                "Adjust shadow mapping quality. Filtering and cascade coverage for dynamic shadows."
            ),
            ladder(
                "VideoSetting_ShadowMapResolution", "Shadow Map Resolution",
                "Adjust the resolution of shadow maps. Texel density of the shadow atlas; the main shadow cost multiplier."
            ),
            toggle(
                "VideoSetting_Planar Reflections", "Planar Reflections",
                "Toggle planar reflections on/off. Mirrored pass for floors, water and glass, doubling draw cost in reflective rooms.",
                IMAGE
            ),

            toggle(
                "VideoSetting_Volumetric Lighting", "Volumetric Lighting",
                "Toggle volumetric lighting on/off. Light shafts and fog scattering; expensive in smoky corridors.",
                EFFECTS
            ),
            toggle(
                "VideoSetting_Depth of Field", "Depth of Field",
                "Toggle depth of field on/off. Blurs out-of-focus depth ranges around the focal plane.",
                EFFECTS
            ),
            toggle(
                "VideoSetting_MotionBlur", "Motion Blur",
                "Toggle motion blur on/off. Camera-space blur on fast movement; off by default on this device.",
                EFFECTS
            ),
            ladder(
                "VideoSetting_Particles", "Particles",
                "Adjust particle effect quality. Density and resolution of steam, sparks and ambient VFX; heaviest in engineering.",
                EFFECTS
            ),
            toggle(
                "VideoSetting_ChromaticAberrations", "Chromatic Aberration",
                "Toggle chromatic aberration on/off. Separates red and blue channels at the frame edges for a lens effect.",
                EFFECTS
            ),

            toggle(
                "VideoSetting_Deep Colour (30bpp)", "Deep Color",
                "Toggle deep color on/off. Requests a 10-bit swapchain where the panel supports it; no performance cost.",
                DISPLAY
            ),
            toggle(
                "FullScreen", "Fullscreen",
                "Engine fullscreen flag. On Android the surface is always full-bleed; kept for parity with the desktop config.",
                DISPLAY
            ),
            Control(
                "MaxFramesPerSecond", "Frame rate cap",
                "Engine-level FPS cap stored outside the Alien Setup block. 60 unlocks the 30 FPS mode; 120 is useful on a high-refresh display.",
                DISPLAY, ControlKind.CHOICE,
                listOf(ControlChoice("30", "30 fps"), ControlChoice("60", "60 fps"), ControlChoice("120", "120 fps")),
                absolutePath = DIRECT3D_CONFIG,
                ignoreSuffix = true,
                note = "Engine key in IndirectX/Direct3D/Config. The game may re-clamp on launch."
            ),
            Control(
                "FixedScreenHeight", "Render height",
                "Internal render height in pixels. The game scales to the panel; lower values raise frame rate at the cost of sharpness.",
                DISPLAY, ControlKind.CHOICE, heightChoices()
            )
        )
    )
}
