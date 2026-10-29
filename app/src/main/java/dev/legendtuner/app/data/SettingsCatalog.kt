package dev.legendtuner.app.data

enum class SettingGroup(val label: String, val shortLabel: String) {
    FRAME_RATE("Frame rate", "FRAME RATE"),
    VISUALS("Visuals", "VISUALS"),
    EFFECTS("Effects", "EFFECTS"),
    DISPLAY("Display", "DISPLAY")
}

enum class SettingType { TOGGLE, LEVEL, FPS, PERCENT, OPTIONS, INTEGER, DROPDOWN }

enum class SettingScope { PRESET, LIVE, SETUP }

data class SettingDef(
    val key: String,
    val label: String,
    val group: SettingGroup,
    val type: SettingType,
    val scope: SettingScope = SettingScope.PRESET,
    val description: String,
    val options: List<Int> = emptyList(),
    val min: Int = 0,
    val max: Int = 3,
    val valueLabels: Map<Int, String> = emptyMap(),
    val risky: Boolean = false
) {
    fun displayValue(raw: Int?): String {
        if (raw == null) return "—"
        return when (type) {
            SettingType.TOGGLE -> if (raw == 0) "Off" else "On"
            SettingType.FPS -> "$raw fps"
            SettingType.PERCENT -> "$raw%"
            SettingType.DROPDOWN -> valueLabels[raw] ?: "$raw px"
            else -> valueLabels[raw] ?: raw.toString()
        }
    }

    fun optionsFor(maxCap: Int?): List<Int> = when (type) {
        SettingType.TOGGLE -> listOf(0, 1)
        SettingType.LEVEL -> (0..max.coerceAtLeast(0)).toList()
        SettingType.FPS -> options.ifEmpty { listOf(30, 45, 60, 90, 120, 144) }
        SettingType.OPTIONS, SettingType.DROPDOWN -> options
        SettingType.PERCENT, SettingType.INTEGER -> emptyList()
    }

    fun isCapped(raw: Int, cap: Int?): Boolean = type == SettingType.LEVEL && cap != null && raw > cap
}

object SettingsCatalog {

    private val onOff = mapOf(0 to "Off", 1 to "On")
    private val quality4 = mapOf(0 to "Low", 1 to "Medium", 2 to "High", 3 to "Ultra")
    private val shadow4 = mapOf(0 to "Off", 1 to "Low", 2 to "Medium", 3 to "High")
    private val aa4 = mapOf(0 to "Off", 1 to "2×", 2 to "4×", 3 to "8×")
    private val reflections4 = mapOf(0 to "Off", 1 to "Low", 2 to "High", 3 to "Ultra")
    private val dynamicRes = mapOf(0 to "Off", 1 to "On", 3 to "Aggressive")

    val settings: List<SettingDef> = listOf(
        // ---------------------------------------------------------- frame rate
        SettingDef(
            key = "FramerateLimit",
            label = "FPS cap",
            group = SettingGroup.FRAME_RATE,
            type = SettingType.FPS,
            description = "Maximum frame rate the game targets. The panel refresh rate is detected automatically; values above it have no visible benefit.",
            options = listOf(30, 45, 60, 90, 120, 144)
        ),
        SettingDef(
            key = "FrameSizeMaximumPercentage",
            label = "Max resolution scale",
            group = SettingGroup.FRAME_RATE,
            type = SettingType.PERCENT,
            description = "Upper bound for dynamic resolution. 100% renders at the screen's native resolution.",
            min = 25,
            max = 100
        ),
        SettingDef(
            key = "FrameSizeMinimumPercentage",
            label = "Min resolution scale",
            group = SettingGroup.FRAME_RATE,
            type = SettingType.PERCENT,
            description = "Lower bound for dynamic resolution. Lower values protect frame rate at the cost of softness.",
            min = 25,
            max = 100
        ),
        SettingDef(
            key = "DynamicResolution",
            label = "Dynamic resolution",
            group = SettingGroup.FRAME_RATE,
            type = SettingType.OPTIONS,
            description = "Lets the game lower render resolution under load. Aggressive ramps faster but is more visible.",
            options = listOf(0, 1, 3),
            valueLabels = dynamicRes
        ),
        SettingDef(
            key = "OptimisationResolutionMultiplier",
            label = "Optimisation multiplier",
            group = SettingGroup.FRAME_RATE,
            type = SettingType.PERCENT,
            description = "Internal resolution multiplier used by the game's optimisation path.",
            min = 50,
            max = 100
        ),

        // ------------------------------------------------------------ visuals
        SettingDef(
            key = "AntiAliasing",
            label = "Anti-aliasing",
            group = SettingGroup.VISUALS,
            type = SettingType.LEVEL,
            description = "Smooths jagged edges. On a high-density 12.7-inch panel the difference is subtle; it is also relatively expensive.",
            valueLabels = aa4
        ),
        SettingDef(
            key = "TextureQuality",
            label = "Texture quality",
            group = SettingGroup.VISUALS,
            type = SettingType.LEVEL,
            description = "Resolution of track, car and environment textures. Higher values cost memory more than frame rate.",
            valueLabels = quality4
        ),
        SettingDef(
            key = "Shadows",
            label = "Shadows",
            group = SettingGroup.VISUALS,
            type = SettingType.LEVEL,
            description = "Shadow map resolution and draw distance. One of the biggest frame-rate costs at 2944 × 1840.",
            valueLabels = shadow4
        ),
        SettingDef(
            key = "AmbientOcclusion",
            label = "Ambient occlusion",
            group = SettingGroup.VISUALS,
            type = SettingType.TOGGLE,
            description = "Darkens creases and contact points. Adds depth but costs GPU time.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "LODQuality",
            label = "Level of detail",
            group = SettingGroup.VISUALS,
            type = SettingType.LEVEL,
            description = "How much detail is kept at distance. Higher values reduce pop-in.",
            valueLabels = quality4
        ),
        SettingDef(
            key = "GroundCover",
            label = "Ground cover",
            group = SettingGroup.VISUALS,
            type = SettingType.LEVEL,
            description = "Density and detail of grass, gravel and trackside vegetation.",
            valueLabels = quality4
        ),
        SettingDef(
            key = "LightingQuality",
            label = "Lighting quality",
            group = SettingGroup.VISUALS,
            type = SettingType.LEVEL,
            description = "Quality of the lighting pass. Higher values improve track atmosphere.",
            valueLabels = quality4
        ),
        SettingDef(
            key = "VehicleReflections",
            label = "Vehicle reflections",
            group = SettingGroup.VISUALS,
            type = SettingType.LEVEL,
            description = "Reflection detail on car paint and glass.",
            valueLabels = reflections4
        ),

        // ------------------------------------------------------------- effects
        SettingDef(
            key = "MotionBlur",
            label = "Motion blur",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            scope = SettingScope.LIVE,
            description = "Camera and object motion blur. Turning it off can make the image feel sharper.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "Particles",
            label = "Particles",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            description = "Sparks, dust, smoke and debris effects.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "ScreenSpaceReflections",
            label = "Screen-space reflections",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            description = "Reflections on wet roads and glossy surfaces. Costly at high resolution.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "VolumetricLights",
            label = "Volumetric lights",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            description = "Light shafts and atmospheric haze.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "WeatherEffects",
            label = "Weather effects",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            description = "Rain, spray and weather-specific particles.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "SmokeShadows",
            label = "Smoke shadows",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            description = "Whether tyre smoke receives and casts shadows.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "Skidmarks",
            label = "Skid marks",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            description = "Tyre marks left on the track surface.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "Mirrors",
            label = "Mirrors",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            description = "Real-time mirror rendering.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "PostProcess",
            label = "Post processing",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            description = "Bloom, tone mapping, lens effects and the post chain.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "Crowds",
            label = "Crowds",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            description = "Trackside crowd density and animation.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "FireworksAndConfetti",
            label = "Fireworks & confetti",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            scope = SettingScope.LIVE,
            description = "Celebration effects at race finish.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "ReduceVisibleRain",
            label = "Reduce visible rain",
            group = SettingGroup.EFFECTS,
            type = SettingType.TOGGLE,
            scope = SettingScope.LIVE,
            description = "Draws less rain on screen. Useful when weather races are GPU-bound.",
            valueLabels = onOff
        ),

        // ------------------------------------------------------------- display
        SettingDef(
            key = "HDR",
            label = "HDR",
            group = SettingGroup.DISPLAY,
            type = SettingType.TOGGLE,
            scope = SettingScope.LIVE,
            description = "High dynamic range output. Only effective on a compatible display.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "VerticalSync",
            label = "VSync",
            group = SettingGroup.DISPLAY,
            type = SettingType.TOGGLE,
            scope = SettingScope.LIVE,
            description = "Synchronises presentation with the display. Usually leave off when using an FPS cap.",
            valueLabels = onOff
        ),
        SettingDef(
            key = "FixedScreenHeight",
            label = "Fixed render height",
            group = SettingGroup.DISPLAY,
            type = SettingType.DROPDOWN,
            scope = SettingScope.SETUP,
            description = "Forces the internal render height. Pick a common resolution height; use Raw XML if you need an exact non-standard value.",
            options = listOf(480, 540, 600, 720, 768, 800, 838, 900, 960, 1080, 1200, 1440, 1600, 1800, 1840, 2160),
            valueLabels = mapOf(
                480 to "480 px",
                540 to "540 px",
                600 to "600 px",
                720 to "720 px",
                768 to "768 px",
                800 to "800 px",
                838 to "838 px",
                900 to "900 px",
                960 to "960 px",
                1080 to "1080 px",
                1200 to "1200 px",
                1440 to "1440 px",
                1600 to "1600 px",
                1800 to "1800 px",
                1840 to "1840 px",
                2160 to "2160 px"
            ),
            min = 480,
            max = 2160,
            risky = true
        )
    )

    private val byKey = settings.associateBy { it.key }

    fun find(key: String): SettingDef? = byKey[key]

    fun forGroup(group: SettingGroup): List<SettingDef> = settings.filter { it.group == group }

    fun liveSnapshot(doc: ConfigDocument, preset: Preset): Map<String, Int?> =
        settings.associate { it.key to value(doc, preset, it) }

    fun value(doc: ConfigDocument, preset: Preset, def: SettingDef): Int? = when (def.scope) {
        SettingScope.PRESET -> doc.presetValue(preset, def.key)?.firstIntOrNull
        SettingScope.LIVE -> doc.int(ConfigDocument.GRAPHICS_SETTINGS_PATH, def.key)
        SettingScope.SETUP -> doc.int(ConfigDocument.SETUP_PATH, def.key)
    }

    fun set(doc: ConfigDocument, preset: Preset, def: SettingDef, value: Int): Boolean = when (def.scope) {
        SettingScope.PRESET -> {
            val nodeType = doc.presetValue(preset, def.key)?.type
            if (nodeType == "integer") doc.setPresetInteger(preset, def.key, value)
            else doc.setPresetFirstToken(preset, def.key, value)
        }
        SettingScope.LIVE -> doc.setLiveInteger(def.key, value)
        SettingScope.SETUP -> doc.setSetupInteger(def.key, value)
    }

    fun capFor(doc: ConfigDocument, def: SettingDef): Int? =
        if (def.scope == SettingScope.PRESET && def.type == SettingType.LEVEL) doc.maximumFor(def.key) else null

    fun performanceCost(def: SettingDef, value: Int?, cap: Int?): Int {
        if (value == null) return 0
        if (def.type == SettingType.TOGGLE) return if (value == 0) 0 else 2
        if (def.type == SettingType.PERCENT) return 1
        val max = (cap ?: 3).coerceAtLeast(1)
        return ((value.toFloat() / max) * 5f).toInt().coerceIn(0, 5)
    }
}
