package dev.legendtuner.app.data

/**
 * A guided control for a non-GRID Feral title. [key] is the registry value name; [tokens] map the
 * stored text to a human label. Controls are read-only unless a catalogue row was verified against
 * a real device file — nothing here creates keys that the game did not write itself.
 */
enum class ControlKind { TOGGLE, CHOICE, STEPPER, READONLY }

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
    val note: String? = null
) {
    fun labelFor(token: String?): String {
        if (token == null) return "—"
        return when (kind) {
            ControlKind.TOGGLE, ControlKind.CHOICE ->
                choices.firstOrNull { it.token.equals(token, ignoreCase = true) }?.label ?: token
            ControlKind.STEPPER -> "$token$suffix"
            ControlKind.READONLY -> token
        }
    }
}

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
    /** Keys between the title's registry root and the values, e.g. `Setup/GraphicsPreset`. */
    val pathSuffix: List<String> = emptyList()
) {
    fun controlsFor(group: String): List<Control> = controls.filter { it.group == group }

    fun find(key: String): Control? = controls.firstOrNull { it.key == key }

    fun keyFor(control: Control): String = keyPrefix + control.key.removePrefix(stripPrefix)
}

object TitleCatalogEngine {
    fun value(doc: ConfigDocument, root: List<String>, catalog: TitleCatalog, control: Control): ConfigDocument.ConfigValue? =
        doc.value(root + catalog.pathSuffix, catalog.keyFor(control))

    fun token(doc: ConfigDocument, root: List<String>, catalog: TitleCatalog, control: Control): String? =
        value(doc, root, catalog, control)?.text?.trim()

    fun display(doc: ConfigDocument, root: List<String>, catalog: TitleCatalog, control: Control): String =
        control.labelFor(token(doc, root, catalog, control))

    fun write(
        doc: ConfigDocument,
        root: List<String>,
        catalog: TitleCatalog,
        control: Control,
        token: String
    ): Boolean = doc.setValue(root + catalog.pathSuffix, catalog.keyFor(control), token)
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
                "FixedScreenHeight", "Render height",
                "Internal render height in pixels. The game scales to the panel; lower values raise frame rate at the cost of sharpness.",
                DISPLAY, ControlKind.STEPPER, min = 480, max = 2160, suffix = " px"
            )
        )
    )
}
