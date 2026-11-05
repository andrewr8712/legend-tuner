package dev.legendtuner.app.data

/**
 * Catalogues for the titles whose settings live in a flat registry block. Each row mirrors
 * docs/catalogues/<title>.md; notes mark anything the device file does not prove.
 */
private val tier3 = listOf(
    ControlChoice("low", "Low"),
    ControlChoice("medium", "Medium"),
    ControlChoice("high", "High")
)

private fun intLevels(max: Int, labels: List<String> = listOf("Low", "Medium", "High", "Ultra")) =
    (0..max).map { ControlChoice(it.toString(), labels.getOrElse(it) { "$it" }) }

private fun textToggle() = listOf(ControlChoice("off", "Off"), ControlChoice("on", "On"))
private fun intToggle() = listOf(ControlChoice("0", "Off"), ControlChoice("1", "On"))

object SaintsRowCatalog {
    private const val IMAGE = "Image quality"
    private const val SCENE = "Scene detail"
    private const val PERF = "Performance"
    private const val DISPLAY = "Display"
    private const val CONTROLS = "Controls"
    private const val TIERS = "Tier names are inferred from the stored value; verify in-game."
    private val GAME_OPTIONS = listOf(
        "HKEY_CURRENT_USER", "Software", "Feral Interactive", "Saints Row The Third",
        "Setup", "GameOptions"
    )
    private val MOTION_AIM = listOf(
        "HKEY_CURRENT_USER", "Software", "Feral Interactive", "Saints Row The Third",
        "Setup", "MotionAimSettings"
    )

    private fun scene(key: String, label: String, description: String) =
        Control(key, label, description, SCENE, ControlKind.CHOICE, tier3, note = TIERS)

    val catalog = TitleCatalog(
        groups = listOf(IMAGE, SCENE, PERF, DISPLAY, CONTROLS),
        families = listOf(
            PresetFamily("graphics", "Graphics", pathSuffix = listOf("GraphicsPreset")),
            PresetFamily("battery", "Battery saver", pathSuffix = listOf("BatterySaverPreset")),
            PresetFamily("performance", "Performance", pathSuffix = listOf("PerformancePreset"))
        ),
        controls = listOf(
            Control(
                "GfxShadow", "Shadow detail",
                "Shadow map resolution and filtering for world, characters and vehicles. Higher tiers sharpen contact shadows and extend their draw distance.",
                IMAGE, ControlKind.CHOICE, tier3, note = TIERS
            ),
            Control(
                "GfxLighting", "Lighting detail",
                "Quality of the deferred lighting pass: light count, specular response and ambient contribution.",
                IMAGE, ControlKind.CHOICE, tier3, note = TIERS
            ),
            Control(
                "GfxSSAO", "Ambient occlusion",
                "Screen-space contact shadows in corners, under cars and around characters.",
                IMAGE, ControlKind.CHOICE,
                listOf(ControlChoice("off", "Off"), ControlChoice("low", "Low")),
                note = "Only off and low are observed on Android."
            ),
            Control(
                "GfxSSLR", "Screen space reflections",
                "Screen-space reflections on glass, wet roads and polished surfaces.",
                IMAGE, ControlKind.TOGGLE, textToggle()
            ),
            Control(
                "GfxAA", "Anti-aliasing",
                "Post-process edge smoothing. TAA is temporal anti-aliasing; other values are MSAA modes.",
                IMAGE, ControlKind.CHOICE,
                listOf(ControlChoice("off", "Off"), ControlChoice("taa", "TAA")),
                note = "MSAA tokens exist in the game but are not observed on this device."
            ),
            Control(
                "GfxAnisotropic", "Anisotropic filtering",
                "Texture sharpness on surfaces seen at grazing angles, such as roads and roofs.",
                IMAGE, ControlKind.CHOICE,
                listOf(
                    ControlChoice("off", "Off"),
                    ControlChoice("2x", "2x"),
                    ControlChoice("4x", "4x"),
                    ControlChoice("8x", "8x"),
                    ControlChoice("16x", "16x")
                ),
                note = "Only 8x is observed; the other AF levels follow the key's usual ladder."
            ),
            Control(
                "GfxCubemapReflections", "Cubemap reflections",
                "Reflection probes for cars and glass, including dynamic objects in the reflection.",
                IMAGE, ControlKind.TOGGLE, intToggle()
            ),

            scene(
                "GfxSceneCharacters", "Character detail",
                "Mesh and material detail for pedestrians and gang members."
            ),
            scene(
                "GfxSceneVehicles", "Vehicle detail",
                "Mesh LOD bias and material quality for traffic and parked cars."
            ),
            scene(
                "GfxSceneMeshes", "World detail",
                "Props, street furniture and building detail layers."
            ),
            scene(
                "GfxSceneTrees", "Tree detail",
                "Vegetation level-of-detail and billboard distance; trees stay sharp further away at higher tiers."
            ),
            scene(
                "GfxSceneUndergrowth", "Undergrowth",
                "Grass, weeds and ground clutter density."
            ),
            scene(
                "GfxSceneVFX", "Effects detail",
                "Particle, debris and explosion effect density."
            ),
            Control(
                "GfxVolumetricFog", "Volumetric fog",
                "Light shafts and distance fog scattering; the setting behind the quality of volumetric god rays.",
                SCENE, ControlKind.CHOICE,
                listOf(ControlChoice("off", "Off"), ControlChoice("low", "Low"), ControlChoice("high", "High")),
                note = "Only low is observed on Android."
            ),

            Control(
                "GfxIdleFps", "Idle frame cap",
                "Frame cap while nothing is happening on screen. Lower values save battery in menus and cutscenes.",
                PERF, ControlKind.CHOICE, fpsChoices()
            ),
            Control(
                "GfxResponsiveFps", "Active frame cap",
                "Frame cap while the player is moving or driving. This is the cap that matters during play.",
                PERF, ControlKind.CHOICE, fpsChoices()
            ),
            Control(
                "GfxResolutionScale", "Resolution scale",
                "Internal render scale multiplier. 1.00 renders at the panel resolution; lower values are softer but faster.",
                PERF, ControlKind.CHOICE,
                listOf(
                    ControlChoice("0.500000", "50%"),
                    ControlChoice("0.600000", "60%"),
                    ControlChoice("0.700000", "70%"),
                    ControlChoice("0.750000", "75%"),
                    ControlChoice("0.800000", "80%"),
                    ControlChoice("0.900000", "90%"),
                    ControlChoice("1.000000", "100%")
                )
            ),

            Control(
                "Resolution", "Screen resolution",
                "Buffer size the game writes into ScreenW and ScreenH. It scales to the panel, and may clamp values above the panel height.",
                DISPLAY, ControlKind.RESOLUTION,
                listOf(
                    ControlChoice("1280x720", "1280 × 720"),
                    ControlChoice("1280x800", "1280 × 800"),
                    ControlChoice("1600x900", "1600 × 900"),
                    ControlChoice("1920x1080", "1920 × 1080"),
                    ControlChoice("1920x1200", "1920 × 1200"),
                    ControlChoice("2560x1440", "2560 × 1440"),
                    ControlChoice("2560x1600", "2560 × 1600")
                ),
                note = "The observed file held 1280 × 800. The game may recalculate these on launch.",
                ignoreFamilyPath = true
            ),
            Control(
                "FixedScreenHeight", "Render height",
                "Internal vertical resolution. Lower values raise frame rate at the cost of sharpness.",
                DISPLAY, ControlKind.CHOICE, heightChoices(), ignoreSuffix = true, ignoreFamilyPath = true
            ),

            Control(
                "Enabled", "Motion aim",
                "Enable the game's tilt/motion aiming. The same motion settings are used while aiming down sights.",
                CONTROLS, ControlKind.TOGGLE, intToggle(),
                absolutePath = MOTION_AIM,
                ignoreSuffix = true,
                note = "Stored in Setup/MotionAimSettings.Enabled; the game may reset the neutral point when you unpause."
            ),
            Control(
                "VcpEnableGyroControls", "Gyro controls",
                "Enable gyroscopic steering for vehicles. This is separate from Motion aim, which affects the camera while on foot.",
                CONTROLS, ControlKind.TOGGLE, intToggle(),
                absolutePath = GAME_OPTIONS,
                ignoreSuffix = true,
                note = "Stored in Setup/GameOptions. Vehicle gyro is independent of the on-foot motion aim toggle."
            ),
            Control(
                "SensitivityX", "Motion aim sensitivity",
                "Motion-aim sensitivity. The game stores the same value for the X and Y axes here.",
                CONTROLS, ControlKind.CHOICE,
                listOf(
                    ControlChoice("200.000000", "200"),
                    ControlChoice("300.000000", "300"),
                    ControlChoice("400.000000", "400"),
                    ControlChoice("500.000000", "500"),
                    ControlChoice("600.000000", "600"),
                    ControlChoice("800.000000", "800"),
                    ControlChoice("1000.000000", "1000")
                ),
                absolutePath = MOTION_AIM,
                ignoreSuffix = true,
                linkedWrites = listOf(LinkedWrite(MOTION_AIM, "SensitivityY")),
                note = "Writes both MotionAimSettings.SensitivityX and SensitivityY."
            )
        )
    )
}

object CompanyOfHeroesCatalog {
    private const val DISPLAY = "Display"
    private const val GRAPHICS = "Graphics"
    private const val PERFORMANCE = "Performance"
    private const val TIERS = "Tier labels are inferred from the stored integer; verify in-game."
    private val SETUP = listOf("HKEY_CURRENT_USER", "Software", "Feral Interactive", "Company of Heroes", "Setup")
    private val GRAPHICS_CONFIG = listOf("HKEY_CURRENT_USER", "Software", "Original Developer", "Company of Heroes", "Config")
    private val D3D_CONFIG = listOf("HKEY_CURRENT_USER", "Software", "IndirectX", "Direct3D", "Config")

    val catalog = TitleCatalog(
        groups = listOf(DISPLAY, GRAPHICS, PERFORMANCE),
        controls = listOf(
            Control("FullScreen", "Fullscreen", "Run the game fullscreen instead of in a windowed surface.", DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = SETUP, ignoreSuffix = true),
            Control("FixedScreenHeight", "Render height", "Internal vertical resolution; lower values raise frame rate but soften the image.", DISPLAY, ControlKind.CHOICE, heightChoices(), absolutePath = SETUP, ignoreSuffix = true),
            Control("RenderUnderNotch", "Render under notch", "Draw under a display cutout instead of letterboxing around it.", DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = SETUP, ignoreSuffix = true),

            Control(
                "antialiasing", "Anti-aliasing",
                "Anti-aliasing mode. Only the off value is observed in the supplied file; labels are inferred.",
                GRAPHICS, ControlKind.CHOICE,
                listOf(ControlChoice("0", "Off"), ControlChoice("1", "Low"), ControlChoice("2", "High")),
                absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true,
                note = "Only 0 was observed on this device; verify the remaining tokens in-game."
            ),
            Control("shaderquality", "Shader quality", "Complexity of the deferred and unit shaders.", GRAPHICS, ControlKind.CHOICE, intLevels(2), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true, note = TIERS),
            Control("shadows", "Shadow quality", "Shadow map resolution and filtering for units and terrain.", GRAPHICS, ControlKind.CHOICE, intLevels(2), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true, note = TIERS),
            Control("terraindetail", "Terrain detail", "Terrain mesh and texture detail, including ground blending.", GRAPHICS, ControlKind.CHOICE, intLevels(2), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true, note = TIERS),
            Control("texturedetail", "Texture detail", "Texture resolution and filtering for units, buildings and terrain.", GRAPHICS, ControlKind.CHOICE, intLevels(2), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true, note = TIERS),
            Control("treedetail", "Tree detail", "Density and complexity of the tree layer.", GRAPHICS, ControlKind.CHOICE, intLevels(2), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true, note = TIERS),
            Control("effectsdensity", "Effects density", "Particle and explosion density during battles and effects.", GRAPHICS, ControlKind.CHOICE, intLevels(2), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true, note = TIERS),
            Control("effectsfidelity", "Effects fidelity", "Quality of particles, smoke and explosion effects.", GRAPHICS, ControlKind.CHOICE, intLevels(2), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true, note = TIERS),
            Control("shrubsdetail", "Shrub detail", "Density and detail of small vegetation and ground clutter.", GRAPHICS, ControlKind.CHOICE, intLevels(2), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true, note = TIERS),
            Control("modelquality", "Model quality", "Unit and vehicle mesh quality and level-of-detail bias.", GRAPHICS, ControlKind.CHOICE, intLevels(2), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true, note = TIERS),
            Control("physics", "Physics effects", "Ragdolls and physics-driven debris on the battlefield.", GRAPHICS, ControlKind.TOGGLE, intToggle(), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true),
            Control("postprocessing", "Post-processing", "Colour grading, bloom and other full-screen effects.", GRAPHICS, ControlKind.TOGGLE, intToggle(), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true),
            Control("reflections", "Reflections", "Water, puddle and other surface reflections.", GRAPHICS, ControlKind.TOGGLE, intToggle(), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true),
            Control("ribbons", "Ribbons", "Motion ribbons and trail effects on selected visuals.", GRAPHICS, ControlKind.TOGGLE, intToggle(), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true),
            Control("objectscarring", "Object scarring", "Persistent scorch marks and battle damage on the world.", GRAPHICS, ControlKind.TOGGLE, intToggle(), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true),
            Control("raindetail", "Rain detail", "Rain particle density and the resulting splash effects.", GRAPHICS, ControlKind.TOGGLE, intToggle(), absolutePath = GRAPHICS_CONFIG, ignoreSuffix = true),

            Control("EnableFramerateManager", "Dynamic frame-rate manager", "Switch between idle and responsive frame caps automatically.", PERFORMANCE, ControlKind.TOGGLE, intToggle(), absolutePath = D3D_CONFIG, ignoreSuffix = true),
            Control("MaxFramesPerSecond", "Frame cap", "Hard engine frame cap used during normal gameplay.", PERFORMANCE, ControlKind.CHOICE, fpsChoices(), absolutePath = D3D_CONFIG, ignoreSuffix = true),
            Control("FramerateManagerNormalIdleFPS", "Idle frame cap", "Frame cap used in menus and while the game is otherwise idle.", PERFORMANCE, ControlKind.CHOICE, fpsChoices(), absolutePath = SETUP, ignoreSuffix = true),
            Control("FramerateManagerNormalResponsiveFPS", "Responsive frame cap", "Frame cap used while the camera is active or the player is moving.", PERFORMANCE, ControlKind.CHOICE, fpsChoices(), absolutePath = SETUP, ignoreSuffix = true),
            Control("BatterySaverEnabled", "Battery saver", "Enable the battery-saver frame and effect limits.", PERFORMANCE, ControlKind.TOGGLE, intToggle(), absolutePath = SETUP, ignoreSuffix = true)
        )
    )
}

object TombRaiderCatalog {
    private const val DISPLAY = "Display"
    private const val GRAPHICS = "Graphics"
    private const val MOTION = "Motion"
    private const val PERFORMANCE = "Performance"
    private const val TIERS = "Tier labels are inferred from the stored integer; verify in-game."
    private val SETUP = listOf("HKEY_CURRENT_USER", "Software", "Crystal Dynamics", "Tomb Raider", "Setup")
    private val MOTION_AIM = SETUP + "MotionAimSettings"
    private val D3D_CONFIG = listOf("HKEY_CURRENT_USER", "Software", "IndirectX", "Direct3D", "Config")
    private val SCREEN_SIZES = listOf(
        ControlChoice("1280x720", "1280 × 720"),
        ControlChoice("1526x1080", "1526 × 1080"),
        ControlChoice("1920x1080", "1920 × 1080"),
        ControlChoice("2560x1440", "2560 × 1440")
    )
    private val textureQuality = listOf(
        ControlChoice("-1", "Auto"),
        ControlChoice("0", "0"),
        ControlChoice("1", "1"),
        ControlChoice("2", "2"),
        ControlChoice("3", "3")
    )

    val catalog = TitleCatalog(
        groups = listOf(DISPLAY, GRAPHICS, MOTION, PERFORMANCE),
        pathSuffix = listOf("GraphicsSettings"),
        families = listOf(
            PresetFamily("battery", "Battery saver", pathSuffix = listOf("BatterySaverPreset")),
            PresetFamily("graphics", "Graphics", pathSuffix = listOf("GraphicsPreset")),
            PresetFamily("performance", "Performance", pathSuffix = listOf("PerformancePreset")),
            PresetFamily("performance-plus", "Performance+", pathSuffix = listOf("PerformancePlusPreset"))
        ),
        familySelector = FamilySelector(
            absolutePath = SETUP + "GraphicsSettings",
            key = "GraphicsPreset",
            // The block order is the observed preset index order; verify the selector in-game.
            tokens = mapOf("battery" to "0", "graphics" to "1", "performance" to "2", "performance-plus" to "3")
        ),
        controls = listOf(
            Control(
                "Resolution", "Screen resolution",
                "Backbuffer size written as ScreenW and ScreenH. The observed device file held 1526 × 1080.",
                DISPLAY, ControlKind.RESOLUTION, SCREEN_SIZES,
                absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "FixedScreenHeight", "Render height",
                "Internal vertical resolution. Lower values raise frame rate at the cost of sharpness.",
                DISPLAY, ControlKind.CHOICE, heightChoices(),
                absolutePath = SETUP, ignoreSuffix = true
            ),
            Control("FullScreen", "Fullscreen", "Run the game fullscreen instead of in a windowed surface.", DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = SETUP, ignoreSuffix = true),
            Control("RenderUnderNotch", "Render under notch", "Draw under a display cutout instead of letterboxing around it.", DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = SETUP, ignoreSuffix = true),

            Control("ShadowMode", "Shadow mode", "Shadow filtering mode used for world and character shadows.", GRAPHICS, ControlKind.CHOICE, intLevels(2), note = TIERS),
            Control("ShadowResolution", "Shadow resolution", "Shadow map resolution and the resulting softness of contact shadows.", GRAPHICS, ControlKind.CHOICE, intLevels(3), note = TIERS),
            Control("SSAOMode", "Ambient occlusion", "Screen-space contact shadows in corners and under characters.", GRAPHICS, ControlKind.CHOICE, intLevels(3), note = TIERS),
            Control("DOFQuality", "Depth of field", "Depth-of-field blur quality in cutscenes and while aiming.", GRAPHICS, ControlKind.CHOICE, intLevels(3), note = TIERS),
            Control("LODScale", "Level of detail", "Distance at which models switch to lower detail.", GRAPHICS, ControlKind.CHOICE, intLevels(3), note = TIERS),
            Control("TextureQuality", "Texture quality", "Texture resolution. Auto lets the game choose by video memory.", GRAPHICS, ControlKind.CHOICE, textureQuality),
            Control(
                "BestTextureFilter", "Texture filtering",
                "Anisotropic filtering level for textures seen at grazing angles such as roads.",
                GRAPHICS, ControlKind.CHOICE,
                listOf(ControlChoice("0", "0×"), ControlChoice("2", "2×"), ControlChoice("4", "4×"), ControlChoice("6", "6×"))
            ),
            Control("ReflectionQuality", "Reflections", "Reflection quality on wet and polished surfaces.", GRAPHICS, ControlKind.CHOICE, intLevels(2), note = TIERS),
            Control("HairQuality", "Hair quality (TressFX)", "Hair strand rendering; expensive on mobile GPUs.", GRAPHICS, ControlKind.CHOICE, listOf(ControlChoice("0", "Off"), ControlChoice("1", "On"))),
            Control("AntiAliasingMode", "Anti-aliasing", "Post-process or MSAA edge smoothing, depending on the selected mode.", GRAPHICS, ControlKind.CHOICE, intLevels(3), note = TIERS),
            Control("EnableTessellation", "Tessellation", "Extra geometry on terrain, rocks and selected surfaces.", GRAPHICS, ControlKind.TOGGLE, intToggle()),
            Control("EnablePostProcess", "Post-processing", "Post-process effects such as colour grading and lens effects.", GRAPHICS, ControlKind.TOGGLE, intToggle()),
            Control("EnableMotionBlur", "Motion blur", "Camera motion blur while turning or moving quickly.", GRAPHICS, ControlKind.TOGGLE, intToggle()),
            Control("EnableScreenEffects", "Screen effects", "Lens dirt, vignette and similar full-screen effects.", GRAPHICS, ControlKind.TOGGLE, intToggle()),
            Control("EnableHighPrecisionRT", "High-precision render targets", "Reduces banding on gradients at a rendering cost.", GRAPHICS, ControlKind.TOGGLE, intToggle()),
            Control(
                "OptimisationResolutionMultiplier", "Preset render scale",
                "Extra resolution multiplier applied inside the selected graphics preset.",
                GRAPHICS, ControlKind.CHOICE,
                listOf(
                    ControlChoice("50", "50%"), ControlChoice("60", "60%"), ControlChoice("70", "70%"),
                    ControlChoice("80", "80%"), ControlChoice("90", "90%"), ControlChoice("100", "100%")
                )
            ),

            Control(
                "Enabled", "Motion aim",
                "Enable tilt/motion aiming. The game uses MotionAimSettings for the active aiming controls.",
                MOTION, ControlKind.TOGGLE, intToggle(),
                absolutePath = MOTION_AIM, ignoreSuffix = true,
                linkedWrites = listOf(LinkedWrite(SETUP, "EnableMotionAim")),
                note = "Writes both MotionAimSettings.Enabled and Setup.EnableMotionAim."
            ),
            Control(
                "SensitivityX", "Motion aim sensitivity",
                "Motion-aim sensitivity. The editor writes the same value to both X and Y axes.",
                MOTION, ControlKind.CHOICE,
                listOf(
                    ControlChoice("200.000000", "200"), ControlChoice("300.000000", "300"),
                    ControlChoice("400.000000", "400"), ControlChoice("500.000000", "500"),
                    ControlChoice("600.000000", "600"), ControlChoice("800.000000", "800"),
                    ControlChoice("1000.000000", "1000")
                ),
                absolutePath = MOTION_AIM, ignoreSuffix = true,
                linkedWrites = listOf(LinkedWrite(MOTION_AIM, "SensitivityY"))
            ),

            Control(
                "MaxFramesPerSecond", "Frame cap",
                "Engine-level frame cap. The game also stores a cap inside each graphics preset.",
                PERFORMANCE, ControlKind.CHOICE, fpsChoices(),
                absolutePath = D3D_CONFIG, ignoreSuffix = true
            ),
            Control(
                "VulkanUseSeparateSubmitThread", "Separate Vulkan submit thread",
                "Submit rendering work from a dedicated thread to improve frame pacing.",
                PERFORMANCE, ControlKind.TOGGLE, intToggle(), absolutePath = D3D_CONFIG, ignoreSuffix = true
            )
        )
    )
}

object HitmanAbsolutionCatalog {
    private const val DISPLAY = "Display"
    private const val GRAPHICS = "Graphics"
    private const val MOTION = "Motion"
    private const val PERFORMANCE = "Performance"
    private const val TIERS = "Tier labels are inferred from the stored integer; verify in-game."
    private val ROOT = listOf("HKEY_CURRENT_USER", "Software", "IO Interactive", "Hitman Absolution")
    private val SETUP = ROOT + "Setup"
    private val MOTION_AIM = SETUP + "MotionAimSettings"
    private val D3D_CONFIG = listOf("HKEY_CURRENT_USER", "Software", "IndirectX", "Direct3D", "Config")
    private val SCREEN_SIZES = listOf(
        ControlChoice("1280x720", "1280 × 720"),
        ControlChoice("1526x1080", "1526 × 1080"),
        ControlChoice("1920x1080", "1920 × 1080"),
        ControlChoice("2560x1440", "2560 × 1440")
    )
    private val scaling = listOf(
        ControlChoice("0.500000", "50%"),
        ControlChoice("0.600000", "60%"),
        ControlChoice("0.700000", "70%"),
        ControlChoice("0.800000", "80%"),
        ControlChoice("0.900000", "90%"),
        ControlChoice("1.000000", "100%")
    )

    val catalog = TitleCatalog(
        groups = listOf(DISPLAY, GRAPHICS, MOTION, PERFORMANCE),
        families = listOf(
            PresetFamily("battery", "Battery saver", keySuffix = "_battery"),
            PresetFamily("framerate", "Framerate", keySuffix = "_framerate"),
            PresetFamily("graphics", "Graphics", keySuffix = "_graphics")
        ),
        controls = listOf(
            Control(
                "Resolution", "Screen resolution",
                "Backbuffer size written as ResolutionWidth and ResolutionHeight. The observed file held 1526 × 1080.",
                DISPLAY, ControlKind.RESOLUTION, SCREEN_SIZES,
                absolutePath = ROOT, widthKey = "ResolutionWidth", heightKey = "ResolutionHeight", ignoreSuffix = true
            ),
            Control("FixedScreenHeight", "Render height", "Internal vertical resolution; lower values raise frame rate but soften the image.", DISPLAY, ControlKind.CHOICE, heightChoices(), absolutePath = SETUP, ignoreSuffix = true),
            Control("Fullscreen", "Fullscreen", "Run the game fullscreen instead of in a window.", DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = ROOT, ignoreSuffix = true),
            Control("ExclusiveFullscreen", "Exclusive fullscreen", "Take exclusive control of the display instead of sharing it with the system.", DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = ROOT, ignoreSuffix = true),
            Control("VSync", "Vertical sync", "Limit presentation to the display refresh rate to avoid tearing.", DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = ROOT, ignoreSuffix = true),

            Control("Bloom", "Bloom", "Bloom glow around bright lights and reflective objects.", GRAPHICS, ControlKind.TOGGLE, intToggle()),
            Control("Bokeh", "Depth of field / bokeh", "Background blur in cutscenes and aiming.", GRAPHICS, ControlKind.CHOICE, intLevels(2), note = TIERS),
            Control("FXAA", "FXAA", "Fast post-process anti-aliasing that softens jagged edges.", GRAPHICS, ControlKind.TOGGLE, intToggle()),
            Control("MSAA", "MSAA", "Multisample anti-aliasing; expensive on mobile GPUs.", GRAPHICS, ControlKind.CHOICE, listOf(ControlChoice("0", "Off"), ControlChoice("2", "2×"), ControlChoice("4", "4×"))),
            Control("LevelOfDetail", "Level of detail", "Model and prop level-of-detail distance for distant objects.", GRAPHICS, ControlKind.CHOICE, intLevels(3), note = TIERS),
            Control("ShadowQuality", "Shadow quality", "Shadow map resolution and filtering for characters and the world.", GRAPHICS, ControlKind.CHOICE, intLevels(4), note = TIERS),
            Control("SSAO", "Ambient occlusion", "Screen-space contact shadows in corners and under characters.", GRAPHICS, ControlKind.CHOICE, intLevels(3), note = TIERS),
            Control("LPV", "Light propagation volumes", "Global illumination approximation; expensive.", GRAPHICS, ControlKind.TOGGLE, intToggle()),
            Control("MirrorQuality", "Mirrors and reflections", "Mirror and reflection resolution on glass and polished surfaces.", GRAPHICS, ControlKind.CHOICE, intLevels(3), note = TIERS),
            Control("Tessellation", "Tessellation", "Extra geometry detail on selected surfaces and props.", GRAPHICS, ControlKind.TOGGLE, intToggle()),
            Control("TextureFilter", "Texture filtering", "Anisotropic filtering level for textures seen at grazing angles such as roads.", GRAPHICS, ControlKind.CHOICE, intLevels(3), note = TIERS),
            Control("TextureResolution", "Texture resolution", "Texture memory budget and the resulting texture resolution.", GRAPHICS, ControlKind.CHOICE, listOf(ControlChoice("0", "Low"), ControlChoice("1", "Medium"), ControlChoice("2", "High"))),
            Control("ResolutionScalingMultiplier", "Resolution scale", "Internal render scale multiplier applied before the final upscale.", GRAPHICS, ControlKind.CHOICE, scaling),

            Control(
                "Enabled", "Motion aim",
                "Enable tilt/motion aiming and the additional motion controls stored under Setup.",
                MOTION, ControlKind.TOGGLE, intToggle(),
                absolutePath = MOTION_AIM, ignoreSuffix = true,
                linkedWrites = listOf(LinkedWrite(SETUP, "AdditionalMotionControls")),
                note = "Writes both MotionAimSettings.Enabled and Setup.AdditionalMotionControls."
            ),
            Control(
                "SensitivityX", "Motion aim sensitivity",
                "Motion-aim sensitivity. The editor writes the same value to both X and Y axes.",
                MOTION, ControlKind.CHOICE,
                listOf(
                    ControlChoice("200.000000", "200"), ControlChoice("300.000000", "300"),
                    ControlChoice("400.000000", "400"), ControlChoice("500.000000", "500"),
                    ControlChoice("600.000000", "600"), ControlChoice("800.000000", "800"),
                    ControlChoice("1000.000000", "1000")
                ),
                absolutePath = MOTION_AIM, ignoreSuffix = true,
                linkedWrites = listOf(LinkedWrite(MOTION_AIM, "SensitivityY"))
            ),

            Control("FrameRateLimit", "Frame cap", "Main frame-rate limit used by the engine during normal play.", PERFORMANCE, ControlKind.CHOICE, fpsChoices(), absolutePath = SETUP, ignoreSuffix = true),
            Control("IdleFPS", "Idle frame cap", "Frame cap used in menus and while the game is otherwise idle.", PERFORMANCE, ControlKind.CHOICE, fpsChoices(), absolutePath = ROOT, ignoreSuffix = true),
            Control("ResponsiveFPS", "Responsive frame cap", "Frame cap used while the camera is active or the player is moving.", PERFORMANCE, ControlKind.CHOICE, fpsChoices(), absolutePath = ROOT, ignoreSuffix = true),
            Control("EnableFramerateManager", "Dynamic frame-rate manager", "Switch between the idle and responsive frame caps automatically.", PERFORMANCE, ControlKind.TOGGLE, intToggle(), absolutePath = D3D_CONFIG, ignoreSuffix = true),
            Control("VulkanUseSeparateSubmitThread", "Separate Vulkan submit thread", "Submit rendering work from a dedicated thread to improve frame pacing.", PERFORMANCE, ControlKind.TOGGLE, intToggle(), absolutePath = D3D_CONFIG, ignoreSuffix = true)
        )
    )
}

object RailroadsCatalog {
    private const val DISPLAY = "Display"
    private const val QUALITY = "Graphics"
    private const val PERFORMANCE = "Performance"
    private const val ENGINE = "Engine"
    private const val INFERRED = "Tier labels are inferred from the stored integer; verify in-game."
    private val D3D_CONFIG = listOf("HKEY_CURRENT_USER", "Software", "IndirectX", "Direct3D", "Config")
    private val SCREEN_SIZES = listOf(
        ControlChoice("1280x720", "1280 × 720"),
        ControlChoice("1440x900", "1440 × 900"),
        ControlChoice("1920x1080", "1920 × 1080"),
        ControlChoice("2560x1440", "2560 × 1440")
    )
    private val aaChoices = listOf(
        ControlChoice("0", "Off"),
        ControlChoice("1", "2×"),
        ControlChoice("2", "4×")
    )

    val catalog = TitleCatalog(
        groups = listOf(DISPLAY, QUALITY, PERFORMANCE, ENGINE),
        controls = listOf(
            Control(
                "Resolution", "Screen resolution",
                "Backbuffer size written as ScreenW and ScreenH. The observed device file held 1440 × 900.",
                DISPLAY, ControlKind.RESOLUTION, SCREEN_SIZES,
                note = "The game also has a separate FixedScreenHeight internal render height."
            ),
            Control(
                "FixedScreenHeight", "Render height",
                "Internal vertical resolution. Lower values raise frame rate at the cost of sharpness.",
                DISPLAY, ControlKind.CHOICE, heightChoices()
            ),
            Control("FullScreen", "Fullscreen", "Run the game fullscreen instead of in a windowed surface.", DISPLAY, ControlKind.TOGGLE, intToggle()),
            Control(
                "RenderUnderNotch", "Render under notch",
                "Let the game draw under a display cutout instead of letterboxing around it.",
                DISPLAY, ControlKind.TOGGLE, intToggle()
            ),

            Control("ShaderQuality", "Shader quality", "Complexity of the world and vehicle shaders.", QUALITY, ControlKind.CHOICE, intLevels(2), note = INFERRED),
            Control("ShadowQuality", "Shadow quality", "Resolution and filtering of shadow maps.", QUALITY, ControlKind.CHOICE, intLevels(2), note = INFERRED),
            Control("TextureQuality", "Texture quality", "Texture resolution and filtering for terrain, trains and buildings.", QUALITY, ControlKind.CHOICE, intLevels(2), note = INFERRED),
            Control("TreeDensity", "Tree density", "Density of the tree layer around the map.", QUALITY, ControlKind.CHOICE, intLevels(2), note = INFERRED),
            Control(
                "FSAALevel", "Anti-aliasing",
                "Full-screen anti-aliasing level. Off, 2× and 4× are the inferred labels for the three stored values.",
                QUALITY, ControlKind.CHOICE, aaChoices, absolutePath = D3D_CONFIG,
                note = "Labels inferred from the stored level; verify in-game."
            ),

            Control(
                "EnableFramerateManager", "Dynamic frame-rate manager",
                "Let the game switch between idle and responsive frame caps.",
                PERFORMANCE, ControlKind.TOGGLE, intToggle(), absolutePath = D3D_CONFIG
            ),
            Control(
                "MaxFramesPerSecond", "Frame cap",
                "Hard engine frame cap. This is the value the game exposes as the main FPS limit.",
                PERFORMANCE, ControlKind.CHOICE, fpsChoices(), absolutePath = D3D_CONFIG
            ),
            Control(
                "FramerateManagerNormalIdleFPS", "Idle frame cap",
                "Frame cap used while the game is idle or in menus.",
                PERFORMANCE, ControlKind.CHOICE, fpsChoices(), absolutePath = D3D_CONFIG
            ),
            Control(
                "FramerateManagerNormalResponsiveFPS", "Responsive frame cap",
                "Frame cap used while the camera is moving or the game is active.",
                PERFORMANCE, ControlKind.CHOICE, fpsChoices(), absolutePath = D3D_CONFIG
            ),
            Control("UseBatterySaver", "Battery saver", "Enable the battery-saver preset, including its lower frame cap and reduced tree density.", PERFORMANCE, ControlKind.TOGGLE, intToggle()),
            Control("BatterySaverModeMaxFPS", "Battery saver frame cap", "Frame cap used while battery saver is enabled.", PERFORMANCE, ControlKind.CHOICE, fpsChoices()),

            Control(
                "VulkanUseSeparateSubmitThread", "Separate Vulkan submit thread",
                "Submit rendering work from a dedicated thread. Can help frame pacing on some GPUs.",
                ENGINE, ControlKind.TOGGLE, intToggle(), absolutePath = D3D_CONFIG
            )
        )
    )
}

object NapoleonCatalog {
    private const val QUALITY = "Battle quality"
    private const val FRAME = "Frame rate"
    private const val DISPLAY = "Display"
    private val DIRECT3D_CONFIG = listOf("HKEY_CURRENT_USER", "Software", "IndirectX", "Direct3D", "Config")
    private val SETUP = listOf(
        "HKEY_CURRENT_USER", "Software", "The Creative Assembly Limited",
        "Napoleon Total War - Mobile", "Setup"
    )

    private fun q(key: String, label: String, description: String) =
        Control(key, label, description, QUALITY, ControlKind.CHOICE, intLevels(3))

    val catalog = TitleCatalog(
        groups = listOf(QUALITY, FRAME, DISPLAY),
        pathSuffix = listOf("GraphicsSettings"),
        controls = listOf(
            q(
                "ShadowsQuality", "Shadows",
                "Shadow map resolution and filtering for units, buildings and terrain. The biggest battle-frame cost after unit count."
            ),
            q(
                "TextureQuality", "Texture quality",
                "Texture resolution for units, buildings and terrain. Higher tiers need more VRAM and bandwidth."
            ),
            q(
                "TextureFiltering", "Texture filtering",
                "Anisotropic filtering level for ground and unit textures seen at an angle."
            ),
            q(
                "UnitDetail", "Unit detail",
                "Soldier mesh detail, LOD distances and animation quality. Scales with the number of men on the field."
            ),
            q(
                "BuildingDetail", "Building detail",
                "Settlement and fortification mesh detail on the battle map."
            ),
            q(
                "ParticleEffects", "Particle effects",
                "Smoke, musket fire, cannon impacts and dust. The game warns large battles suffer most."
            ),
            q(
                "GrassQuality", "Grass quality",
                "Ground cover density and draw distance; grass thins out noticeably at the lowest tier."
            ),
            q(
                "TreesQuality", "Tree quality",
                "Tree level-of-detail and billboard distance; forests stay dense further from the camera at high tiers."
            ),
            q(
                "WaterQuality", "Water quality",
                "Sea and river shading, reflections and wave detail."
            ),
            q(
                "SkyQuality", "Sky quality",
                "Cloud and atmosphere quality, mostly visible in battle lighting."
            ),
            q(
                "TerrainQuality", "Terrain quality",
                "Ground mesh resolution and blending layers."
            ),
            q(
                "ShipQuality", "Ship detail",
                "Naval unit mesh and sail detail; only visible in sea battles, so it is often left low."
            ),
            Control(
                "AntiAliasing", "Anti-aliasing",
                "MSAA on geometric edges. Expensive on a tablet GPU when large formations fill the screen.",
                QUALITY, ControlKind.CHOICE, intLevels(3),
                note = "Whether this is a toggle or a level on mobile is unverified."
            ),
            Control(
                "SoftShadows", "Soft shadows",
                "Blurs shadow edges with a wider filter for a softer look, at a small extra cost.",
                QUALITY, ControlKind.TOGGLE, intToggle()
            ),
            Control(
                "HDR", "HDR",
                "High-dynamic-range lighting and tone mapping.",
                QUALITY, ControlKind.TOGGLE, intToggle()
            ),
            Control(
                "gfx_dead_unit_render_time", "Dead unit render time",
                "Seconds corpses stay on the field. Not a quality setting, but a battle-load control.",
                DISPLAY, ControlKind.STEPPER, min = 0, max = 10, suffix = " s"
            ),
            Control(
                "EnableFramerateManager", "Dynamic frame-rate manager",
                "The engine's idle/responsive FPS manager. Turning it off may remove its soft caps; the game may still be limited by VSync or the display mode.",
                FRAME, ControlKind.TOGGLE, intToggle(),
                absolutePath = DIRECT3D_CONFIG,
                ignoreSuffix = true,
                note = "Engine key in IndirectX/Direct3D/Config."
            ),
            Control(
                "FramerateManagerBattleResponsiveFPS", "Battle frame cap",
                "Maximum responsive frame rate during battles. Raise this to 60 or 120 to unlock the 30 FPS battle cap.",
                FRAME, ControlKind.CHOICE, fpsChoices(),
                absolutePath = DIRECT3D_CONFIG,
                ignoreSuffix = true,
                note = "Engine key in IndirectX/Direct3D/Config."
            ),
            Control(
                "FramerateManagerCampaignResponsiveFPS", "Campaign frame cap",
                "Maximum responsive frame rate on the campaign map. The campaign is usually lighter than a full battle.",
                FRAME, ControlKind.CHOICE, fpsChoices(),
                absolutePath = DIRECT3D_CONFIG,
                ignoreSuffix = true,
                note = "Engine key in IndirectX/Direct3D/Config."
            ),
            Control(
                "FixedScreenHeight", "Render height",
                "Internal vertical resolution. Lower values raise frame rate at the cost of sharpness.",
                DISPLAY, ControlKind.CHOICE, heightChoices(),
                absolutePath = SETUP,
                ignoreSuffix = true
            ),
            Control(
                "GameResolutionMultiplier", "Resolution scale",
                "Internal render scale relative to the panel. 100% is native; lower values trade sharpness for frame rate.",
                DISPLAY, ControlKind.CHOICE,
                listOf(
                    ControlChoice("0.500000", "50%"),
                    ControlChoice("0.750000", "75%"),
                    ControlChoice("1.000000", "100%")
                ),
                absolutePath = SETUP,
                ignoreSuffix = true
            )
        )
    )
}

object HitmanCatalog {
    private const val FRAME = "Frame rate"
    private const val SHADOWS = "Shadows"
    private const val POST = "Post-processing"
    private const val DETAIL = "Detail"
    private const val DISPLAY = "Display"
    private const val LADDER =
        "This key stores a ladder (three different values), so it is shown read-only rather than overwritten with one number."

    private fun step(key: String, label: String, description: String, max: Int, group: String) =
        Control(key, label, description, group, ControlKind.STEPPER, min = 0, max = max, triple = true)

    private fun toggle(key: String, label: String, description: String, invert: Boolean = false, group: String = POST) =
        Control(
            key, label, description, group, ControlKind.TOGGLE,
            listOf(ControlChoice("0", "Off"), ControlChoice("1", "On")),
            triple = true, invert = invert
        )

    private fun readOnly(key: String, label: String, description: String, group: String = POST) =
        Control(key, label, description, group, ControlKind.READONLY, triple = true)

    val catalog = TitleCatalog(
        groups = listOf(FRAME, SHADOWS, POST, DETAIL, DISPLAY),
        families = listOf(
            PresetFamily("live", "Live values"),
            PresetFamily("battery", "Battery saver", keySuffix = "_battery"),
            PresetFamily("graphics", "Graphics", keySuffix = "_graphics"),
            PresetFamily("perf", "Performance", keySuffix = "_perf"),
            PresetFamily("plus", "Performance+", keySuffix = "_plus")
        ),
        controls = listOf(
            Control(
                "IdleFPS", "Idle frame cap",
                "Frame cap when the player is still or the scene is static. Lower values save battery in quiet moments.",
                FRAME, ControlKind.CHOICE, fpsChoices(), triple = true
            ),
            Control(
                "ResponsiveFPS", "Active frame cap",
                "Frame cap while input is active. This is the cap that matters during play and in firefights.",
                FRAME, ControlKind.CHOICE, fpsChoices(), triple = true
            ),

            Control(
                "DirectionalShadowResolution", "Sun shadow resolution",
                "Shadow map size for the directional light. The stored triple is a ladder of three presets, not one value.",
                SHADOWS, ControlKind.CHOICE,
                listOf(
                    ControlChoice("512", "512"),
                    ControlChoice("1024", "1024"),
                    ControlChoice("1536", "1536"),
                    ControlChoice("2048", "2048")
                ),
                note = LADDER, triple = true
            ),
            step(
                "DropShadowQuality", "Drop shadow quality",
                "Quality of the cheap character and prop drop shadows cast onto the floor.",
                3, SHADOWS
            ),
            toggle(
                "EnableDropShadows", "Drop shadows",
                "Enables the cheap character shadow pass. Turning it off removes the flat shadow under characters.",
                group = SHADOWS
            ),
            toggle(
                "EnableSelfShadows", "Self shadows",
                "Lets characters cast shadows on themselves. Subtle, but it costs an extra shadow pass.",
                group = SHADOWS
            ),

            step(
                "Antialias", "Anti-aliasing",
                "MSAA sample level for geometric edges. Zero is off, higher values cost bandwidth.",
                8, POST
            ),
            step(
                "FXAAPreset", "FXAA preset",
                "FXAA quality preset index. Higher presets trade a little sharpness for fewer jagged edges.",
                3, POST
            ),
            toggle(
                "DisableBloom", "Bloom",
                "Adds a soft glow around bright highlights. Stored inverted: 1 means bloom is disabled.",
                invert = true
            ),
            toggle(
                "DisableDepthOfField", "Depth of field",
                "Blurs out-of-focus depth ranges around the focal plane. Stored inverted: 1 disables it.",
                invert = true
            ),
            toggle(
                "DisableFinishFilter", "Finish filter",
                "Applies the colour-grade finish pass over the frame. Stored inverted: 1 disables it.",
                invert = true
            ),
            readOnly(
                "FXAAEdgeQualityThreshold", "FXAA edge threshold",
                "Edge detection threshold for FXAA. Lower values find more edges and smooth more of the image."
            ),
            readOnly(
                "FXAAEdgeThresholdMin", "FXAA minimum threshold",
                "Minimum contrast before FXAA acts. Raising it leaves low-contrast detail untouched."
            ),
            readOnly(
                "FXAASubpixQuality", "FXAA subpixel quality",
                "Subpixel aliasing removal strength. Higher values smooth thin geometry and text more."
            ),

            step(
                "LevelOfDetail", "Level of detail",
                "Distance at which props and characters drop to lower-detail meshes. Higher keeps detail further out.",
                3, DETAIL
            ),
            step(
                "TextureResolution", "Texture resolution",
                "Texture streaming budget and mip selection. Higher values need more memory and bandwidth.",
                3, DETAIL
            ),
            step(
                "Anisotropy", "Anisotropic filtering",
                "Anisotropic filtering level for floors and walls seen at an angle.",
                16, DETAIL
            ),

            Control(
                "FixedScreenHeight", "Render height",
                "Internal vertical resolution. Higher values are sharper; lower values raise frame rate on this tablet.",
                DISPLAY, ControlKind.CHOICE, heightChoices(), ignoreSuffix = true
            )
        )
    )
}

object LaraGolCatalog {
    private const val IMAGE = "Image quality"
    private const val EFFECTS = "Effects"
    private const val DISPLAY = "Display"

    val catalog = TitleCatalog(
        groups = listOf(IMAGE, EFFECTS, DISPLAY),
        families = listOf(
            PresetFamily("graphics", "Graphics", pathSuffix = listOf("GraphicsPreset")),
            PresetFamily("battery", "Battery saver", pathSuffix = listOf("BatterySaverPreset")),
            PresetFamily("performance", "Performance", pathSuffix = listOf("PerformancePreset")),
            PresetFamily("plus", "Performance+", pathSuffix = listOf("PerformancePlusPreset"))
        ),
        controls = listOf(
            Control(
                "TextureDetail", "Texture detail",
                "Texture resolution for characters, ruins and ground.",
                IMAGE, ControlKind.CHOICE, intLevels(2, listOf("Low", "Medium", "High"))
            ),
            Control(
                "BestTextureFilter", "Texture filtering",
                "Filter mode: Point, Bilinear, Trilinear or anisotropic 1x to 16x. The stored number indexes that list.",
                IMAGE, ControlKind.STEPPER, min = 0, max = 16,
                note = "The value 10 is observed; the index-to-mode mapping is unverified."
            ),
            Control(
                "AntiAliasing", "Antialiasing mode",
                "Edge smoothing mode. This engine treats it as a post-process choice rather than MSAA samples.",
                IMAGE, ControlKind.CHOICE,
                listOf(ControlChoice("0", "Off"), ControlChoice("1", "On")),
                note = "Only 0 is observed; higher modes may exist."
            ),

            Control(
                "EnableShadows", "Enable shadows",
                "Dynamic shadows from characters, braziers and geometry.",
                EFFECTS, ControlKind.TOGGLE, intToggle()
            ),
            Control(
                "EnableReflection", "Enable reflections",
                "Reflections in water and polished stone.",
                EFFECTS, ControlKind.TOGGLE, intToggle()
            ),
            Control(
                "EnableFullscreenEffects", "Fullscreen effects",
                "Colour grade, bloom and other full-screen post passes.",
                EFFECTS, ControlKind.TOGGLE, intToggle()
            ),

            Control(
                "EnableVSync", "VSync",
                "Locks presentation to the display refresh. Removes tearing, adds latency.",
                DISPLAY, ControlKind.TOGGLE, intToggle()
            ),
            Control(
                "MaxFramesPerSecond", "Frame rate cap",
                "Engine-level cap. The dynamic resolution target is a separate key.",
                DISPLAY, ControlKind.CHOICE,
                listOf(ControlChoice("30", "30 fps"), ControlChoice("60", "60 fps"), ControlChoice("120", "120 fps"))
            ),
            Control(
                "DRSFPSTarget", "Dynamic resolution target",
                "Frame rate the dynamic resolution scaler aims for when it lowers render resolution.",
                DISPLAY, ControlKind.STEPPER, min = 30, max = 120, suffix = " fps"
            ),
            Control(
                "FixedScreenHeight", "Render height",
                "Internal vertical resolution. The panel is 2K; lower values raise frame rate at the cost of sharpness.",
                DISPLAY, ControlKind.CHOICE, heightChoices()
            )
        )
    )
}

object LaraTooCatalog {
    private const val IMAGE = "Image quality"
    private const val DISPLAY = "Display"
    private val GRAPHICS = listOf(
        "HKEY_CURRENT_USER", "Software", "Crystal Dynamics",
        "Lara Croft and the Temple of Osiris", "Graphics"
    )

    private fun q(key: String, label: String, description: String) =
        Control(key, label, description, IMAGE, ControlKind.CHOICE, intLevels(3))

    val catalog = TitleCatalog(
        groups = listOf(IMAGE, DISPLAY),
        families = listOf(
            PresetFamily("graphics", "Graphics", pathSuffix = listOf("GraphicsPreset")),
            PresetFamily("battery", "Battery saver", pathSuffix = listOf("BatterySaverPreset")),
            PresetFamily("performance", "Performance", pathSuffix = listOf("PerformancePreset"))
        ),
        controls = listOf(
            q(
                "ShadowQuality", "Shadows",
                "Shadow map resolution and filtering for characters, tombs and braziers."
            ),
            q(
                "ReflectionQuality", "Reflections",
                "Planar and screen-space reflections on water and polished floors."
            ),
            q(
                "AmbientOcclusionQuality", "Ambient occlusion",
                "Contact shadows where geometry meets; softens the look of stone joints and rubble."
            ),
            q(
                "DOFQuality", "Depth of field",
                "Blurs distant or foreground depth ranges for a cinematic look. Some players find it uncomfortable."
            ),
            Control(
                "SSAAQuality", "Anti-aliasing",
                "Supersampling level. The menu calls this ANTI ALIASING; SSAAQuality is the only AA-shaped key.",
                IMAGE, ControlKind.CHOICE, intLevels(3),
                note = "Mapping to the menu label is provisional; verify in-game."
            ),
            Control(
                "TextureQuality", "Texture detail",
                "Texture resolution for environment and characters.",
                IMAGE, ControlKind.CHOICE, intLevels(3)
            ),
            Control(
                "TextureFiltering", "Texture filtering",
                "Point, Bilinear, Trilinear or anisotropic 1x to 16x. The stored number indexes the game's filter list.",
                IMAGE, ControlKind.STEPPER, min = 0, max = 16,
                note = "Only 4 is observed; the index order is unverified."
            ),

            Control(
                "Resolution", "Screen resolution",
                "Fullscreen width and height the game writes into FullscreenWidth/FullscreenHeight. The engine may recalculate the pair on launch.",
                DISPLAY, ControlKind.RESOLUTION, resolutionChoices(),
                absolutePath = GRAPHICS,
                widthKey = "FullscreenWidth",
                heightKey = "FullscreenHeight",
                ignoreSuffix = true,
                ignoreFamilyPath = true
            ),
            Control(
                "VSync", "VSync",
                "Locks presentation to the display refresh; removes tearing, adds latency.",
                DISPLAY, ControlKind.TOGGLE, intToggle()
            ),
            Control(
                "MaxFramesPerSecond", "Frame rate limiter",
                "Highest possible FPS. A lower limit is more stable, but values must divide the panel refresh rate.",
                DISPLAY, ControlKind.CHOICE,
                listOf(ControlChoice("30", "30 fps"), ControlChoice("60", "60 fps"), ControlChoice("120", "120 fps"))
            ),
            Control(
                "EnableAdvancedGraphicsOption", "Advanced options",
                "Shows the additional options that may impact performance. Mirrors the game's own toggle.",
                DISPLAY, ControlKind.TOGGLE, intToggle()
            ),
            Control(
                "FixedScreenHeight", "Render height",
                "Internal vertical resolution. Lower values raise frame rate at the cost of sharpness.",
                DISPLAY, ControlKind.CHOICE, heightChoices()
            )
        )
    )
}

object Tropico3Catalog {
    private const val DISPLAY = "Display"
    private const val QUALITY = "Graphics"
    private const val PERFORMANCE = "Performance"
    private val SETUP = listOf(
        "HKEY_CURRENT_USER", "Software", "Haemimont Games", "Tropico 3: Gold Edition", "Setup"
    )
    private val GFX = SETUP + "GFX"
    private val D3D_CONFIG = listOf("HKEY_CURRENT_USER", "Software", "IndirectX", "Direct3D", "Config")

    private val textTiers = listOf(
        ControlChoice("Off", "Off"),
        ControlChoice("Low", "Low"),
        ControlChoice("Medium", "Medium"),
        ControlChoice("High", "High")
    )

    private fun gfx(
        key: String,
        label: String,
        description: String,
        choices: List<ControlChoice> = textTiers
    ) = Control(
        key, label, description, QUALITY, ControlKind.CHOICE, choices,
        absolutePath = GFX, ignoreSuffix = true
    )

    val catalog = TitleCatalog(
        groups = listOf(DISPLAY, QUALITY, PERFORMANCE),
        controls = listOf(
            Control(
                "Resolution", "Screen resolution",
                "Fullscreen width and height stored in ScreenW and ScreenH. The game may recalculate the pair after a display change.",
                DISPLAY, ControlKind.RESOLUTION, resolutionChoices(),
                absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "FullScreen", "Fullscreen",
                "Runs the game fullscreen instead of in a windowed surface.",
                DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "RenderUnderNotch", "Render under notch",
                "Extends the render surface below the display cutout on edge-to-edge devices.",
                DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "FixedScreenHeight", "Render height",
                "Internal vertical resolution. Lower values raise frame rate at the cost of sharpness.",
                DISPLAY, ControlKind.CHOICE, heightChoices(), absolutePath = SETUP, ignoreSuffix = true
            ),
            gfx(
                "SceneRez", "Scene resolution",
                "Resolution of the world render target before the UI is composited. Lower values soften the whole scene."
            ),
            gfx(
                "ShadowQuality", "Shadows",
                "Shadow-map resolution and filtering for buildings, terrain and units."
            ),
            gfx(
                "TextureLevel", "Texture quality",
                "Texture resolution for terrain, buildings and characters. Higher tiers need more VRAM."
            ),
            gfx(
                "WaterQuality", "Water quality",
                "Sea and river shading, reflections and wave detail."
            ),
            gfx(
                "TerrainQuality", "Terrain quality",
                "Ground mesh detail and blending between terrain layers."
            ),
            gfx(
                "GrassQuality", "Grass quality",
                "Ground-cover density and draw distance around the camera."
            ),
            gfx(
                "ViewDistance", "View distance",
                "How far detailed scenery and buildings remain visible before LOD switches."
            ),
            gfx(
                "Anisotropy", "Anisotropic filtering",
                "Texture sharpness on roads, roofs and terrain seen at grazing angles.",
                listOf(
                    ControlChoice("Off", "Off"),
                    ControlChoice("2x", "2×"),
                    ControlChoice("4x", "4×"),
                    ControlChoice("8x", "8×"),
                    ControlChoice("16x", "16×")
                )
            ),
            gfx(
                "PostProcessing", "Post-processing",
                "Bloom, colour grading and other full-screen effects applied after the scene is rendered."
            ),
            Control(
                "MaxFramesPerSecond", "Engine frame cap",
                "Hard engine cap used during normal gameplay. Lower values can make frame time more consistent.",
                PERFORMANCE, ControlKind.CHOICE, fpsChoices(),
                absolutePath = D3D_CONFIG, ignoreSuffix = true,
                note = "Engine key in IndirectX/Direct3D/Config."
            ),
            Control(
                "BatterySaverFPS", "Battery saver frame cap",
                "Frame cap the game uses when battery saver is active.",
                PERFORMANCE, ControlKind.CHOICE, fpsChoices(),
                absolutePath = SETUP, ignoreSuffix = true
            )
        )
    )
}

object EmpireTotalWarCatalog {
    private const val QUALITY = "Battle quality"
    private const val FRAME = "Frame rate"
    private const val DISPLAY = "Display"
    private val SETUP = listOf(
        "HKEY_CURRENT_USER", "Software", "The Creative Assembly Limited", "Total War EMPIRE", "Setup"
    )
    private val GRAPHICS = SETUP + "GraphicsSettings"
    private val MAIN_MENU = SETUP + "MainMenuSettings"
    private val D3D_CONFIG = listOf("HKEY_CURRENT_USER", "Software", "IndirectX", "Direct3D", "Config")

    private fun q(key: String, label: String, description: String) =
        Control(key, label, description, QUALITY, ControlKind.CHOICE, intLevels(3))

    private fun toggle(key: String, label: String, description: String) =
        Control(key, label, description, QUALITY, ControlKind.TOGGLE, intToggle())

    val catalog = TitleCatalog(
        groups = listOf(QUALITY, FRAME, DISPLAY),
        pathSuffix = listOf("GraphicsSettings"),
        controls = listOf(
            q(
                "ShadowsQuality", "Shadows",
                "Shadow-map resolution and filtering for soldiers, buildings and terrain."
            ),
            q(
                "TextureQuality", "Texture quality",
                "Texture resolution for units, buildings and terrain. Higher tiers need more VRAM."
            ),
            q(
                "TextureFiltering", "Texture filtering",
                "Anisotropic filtering level for ground and unit textures seen at an angle."
            ),
            q(
                "UnitDetail", "Unit detail",
                "Soldier mesh detail, LOD distances and animation quality. Scales with the number of men on the field."
            ),
            q(
                "BuildingDetail", "Building detail",
                "Settlement and fortification mesh detail on the battle map."
            ),
            q(
                "ParticleEffects", "Particle effects",
                "Smoke, musket fire, cannon impacts and dust. Large battles suffer most."
            ),
            q(
                "GrassQuality", "Grass quality",
                "Ground cover density and draw distance; grass thins out noticeably at the lowest tier."
            ),
            q(
                "TreesQuality", "Tree quality",
                "Tree level-of-detail and billboard distance; forests stay dense further from the camera at high tiers."
            ),
            q(
                "WaterQuality", "Water quality",
                "Sea and river shading, reflections and wave detail."
            ),
            q(
                "SkyQuality", "Sky quality",
                "Cloud and atmosphere quality, mostly visible in battle lighting."
            ),
            q(
                "TerrainQuality", "Terrain quality",
                "Ground mesh resolution and blending layers."
            ),
            q(
                "ShipQuality", "Ship detail",
                "Naval unit mesh and sail detail; only visible in sea battles."
            ),
            Control(
                "AntiAliasing", "Anti-aliasing",
                "MSAA on geometric edges. Expensive on a tablet GPU when large formations fill the screen.",
                QUALITY, ControlKind.CHOICE, intLevels(3),
                note = "Whether this is a toggle or a level on mobile is unverified."
            ),
            toggle(
                "SoftShadows", "Soft shadows",
                "Blurs shadow edges with a wider filter for a softer look, at a small extra cost."
            ),
            toggle("HDR", "HDR", "High-dynamic-range lighting and tone mapping."),
            toggle("SSAO", "Ambient occlusion", "Screen-space contact shadows in corners and under units."),
            toggle("DepthOfField", "Depth of field", "Blurs distant or foreground ranges for a cinematic look."),
            toggle("Distortion", "Distortion", "Heat haze and other screen-space distortion effects."),
            toggle("VolumetricEffects", "Volumetric effects", "Light shafts and atmospheric scattering in battles."),
            Control(
                "FullScreen", "Fullscreen",
                "Runs the game fullscreen instead of in a windowed surface.",
                DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "RenderUnderNotch", "Render under notch",
                "Extends the render surface below the display cutout on edge-to-edge devices.",
                DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "FixedScreenHeight", "Render height",
                "Internal vertical resolution. Lower values raise frame rate at the cost of sharpness.",
                DISPLAY, ControlKind.CHOICE, heightChoices(), absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "GameResolutionMultiplier", "Resolution scale",
                "Internal render scale relative to the panel. 100% is native; lower values trade sharpness for frame rate.",
                DISPLAY, ControlKind.CHOICE,
                listOf(
                    ControlChoice("0.500000", "50%"),
                    ControlChoice("0.750000", "75%"),
                    ControlChoice("1.000000", "100%")
                ),
                absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "GraphicsQuality", "Graphics preset",
                "Stored quality preset index. The game writes this when its own graphics menu changes the preset.",
                DISPLAY, ControlKind.CHOICE,
                intLevels(4, listOf("Very Low", "Low", "Medium", "High", "Ultra")),
                absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "EnableFramerateManager", "Dynamic frame-rate manager",
                "The engine's idle/responsive FPS manager. Turning it off may remove its soft caps.",
                FRAME, ControlKind.TOGGLE, intToggle(),
                absolutePath = D3D_CONFIG, ignoreSuffix = true,
                note = "Engine key in IndirectX/Direct3D/Config."
            ),
            Control(
                "FramerateManagerBattleResponsiveFPS", "Battle frame cap",
                "Maximum responsive frame rate during battles. Raise this to 60 or 120 to unlock the 30 FPS battle cap.",
                FRAME, ControlKind.CHOICE, fpsChoices(),
                absolutePath = D3D_CONFIG, ignoreSuffix = true,
                note = "Engine key in IndirectX/Direct3D/Config."
            ),
            Control(
                "FramerateManagerCampaignResponsiveFPS", "Campaign frame cap",
                "Maximum responsive frame rate on the campaign map. The campaign is usually lighter than a full battle.",
                FRAME, ControlKind.CHOICE, fpsChoices(),
                absolutePath = D3D_CONFIG, ignoreSuffix = true,
                note = "Engine key in IndirectX/Direct3D/Config."
            ),
            Control(
                "BatterySaver", "Battery saver",
                "Enables the game's battery-saver mode, which uses the engine's lower idle and responsive caps.",
                FRAME, ControlKind.TOGGLE, intToggle(),
                absolutePath = MAIN_MENU, ignoreSuffix = true
            )
        )
    )
}

object RomeTotalWarCatalog {
    private const val DISPLAY = "Display"
    private const val FRAME = "Frame rate"
    private const val QUALITY = "Battle quality"
    private const val CAMPAIGN = "Campaign quality"
    private val SETUP = listOf(
        "HKEY_CURRENT_USER", "Software", "The Creative Assembly Limited", "Rome Total War", "Setup"
    )
    private val D3D_CONFIG = listOf("HKEY_CURRENT_USER", "Software", "IndirectX", "Direct3D", "Config")
    private val sixTiers = listOf("Very Low", "Low", "Medium", "High", "Very High", "Ultra")

    private fun toggle(key: String, label: String, description: String, group: String = QUALITY) =
        Control(key, label, description, group, ControlKind.TOGGLE, intToggle())

    private fun level(
        key: String,
        label: String,
        description: String,
        max: Int,
        labels: List<String> = listOf("Low", "Medium", "High", "Ultra"),
        group: String = QUALITY
    ) = Control(key, label, description, group, ControlKind.CHOICE, intLevels(max, labels))

    val catalog = TitleCatalog(
        groups = listOf(DISPLAY, FRAME, QUALITY, CAMPAIGN),
        controls = listOf(
            Control(
                "Resolution", "Screen resolution",
                "Fullscreen width and height stored in ScreenW and ScreenH. The engine may recalculate the pair on launch.",
                DISPLAY, ControlKind.RESOLUTION, resolutionChoices(),
                absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "FullScreen", "Fullscreen",
                "Runs the game fullscreen instead of in a windowed surface.",
                DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "RenderUnderNotch", "Render under notch",
                "Extends the render surface below the display cutout on edge-to-edge devices.",
                DISPLAY, ControlKind.TOGGLE, intToggle(), absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "FixedScreenHeight", "Render height",
                "Internal vertical resolution. Lower values raise frame rate at the cost of sharpness.",
                DISPLAY, ControlKind.CHOICE, heightChoices(), absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "MaxFramesPerSecond", "Engine frame cap",
                "Main engine frame cap used outside battles and the campaign map.",
                FRAME, ControlKind.CHOICE, fpsChoices(), absolutePath = SETUP, ignoreSuffix = true,
                note = "Engine key in the live Setup block."
            ),
            Control(
                "BattleMaxFramesPerSecond", "Battle frame cap",
                "Frame cap applied during real-time battles. Raise this to 60 or 120 to unlock smoother battles.",
                FRAME, ControlKind.CHOICE, fpsChoices(), absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "CampaignMaxFramesPerSecond", "Campaign frame cap",
                "Frame cap applied on the campaign map, which is usually lighter than a full battle.",
                FRAME, ControlKind.CHOICE, fpsChoices(), absolutePath = SETUP, ignoreSuffix = true
            ),
            Control(
                "MainMenuFPS", "Menu frame cap",
                "Frame cap used in menus and the front end. A low cap saves power without affecting battles.",
                FRAME, ControlKind.CHOICE, fpsChoices(), absolutePath = SETUP, ignoreSuffix = true
            ),
            level(
                "feral_aa_quality", "Anti-aliasing",
                "Edge smoothing quality. The stored integer indexes the game's AA mode; the mapping is provisional.",
                3,
                group = QUALITY
            ),
            toggle(
                "feral_battle_map_shadows", "Battle shadows",
                "Enables shadow maps on the battle map for units, buildings and terrain."
            ),
            toggle(
                "feral_campaign_map_shadows", "Campaign shadows",
                "Enables shadow maps on the campaign map, where they cost battery life for little gameplay benefit."
            ),
            toggle(
                "feral_detailed_unit_shadows", "Detailed unit shadows",
                "Renders per-unit shadow detail instead of a cheaper blob or lower-resolution approximation."
            ),
            level(
                "feral_building_quality", "Building detail",
                "Mesh and texture detail for settlements, forts and campaign buildings.",
                5,
                sixTiers
            ),
            level(
                "feral_effect_quality", "Effect quality",
                "Particle effects, smoke, dust and impact effects during battles.",
                4
            ),
            level(
                "feral_terrain_quality", "Terrain quality",
                "Battle-map ground mesh detail and texture blending.",
                3
            ),
            level(
                "feral_vegetation_quality", "Vegetation quality",
                "Tree and shrub density around the battlefield and campaign map.",
                3
            ),
            level(
                "feral_unit_quality", "Unit quality",
                "Soldier mesh and texture detail. Higher tiers scale with the number of men on screen.",
                3
            ),
            Control(
                "feral_unit_size", "Unit size",
                "Number of soldiers in a standard unit. Larger units change battle balance as well as load.",
                QUALITY, ControlKind.CHOICE,
                listOf(ControlChoice("80", "80 men"), ControlChoice("160", "160 men")),
                note = "Only 80 and 160 are observed in the preset blocks."
            ),
            toggle(
                "feral_unit_size_massive_enabled", "Massive units",
                "Allows the game's massive-unit-size option, which can multiply the number of soldiers on screen."
            ),
            toggle(
                "feral_use_unit_shaders", "Unit shaders",
                "Enables the per-unit shader path. Disabling it can help weak GPUs but changes the visual style."
            ),
            Control(
                "feral_grass_quality", "Grass quality",
                "Grass density and draw distance, stored as a percentage of the maximum.",
                CAMPAIGN, ControlKind.CHOICE,
                listOf(
                    ControlChoice("0", "Off"),
                    ControlChoice("25", "25%"),
                    ControlChoice("50", "50%"),
                    ControlChoice("75", "75%"),
                    ControlChoice("100", "100%")
                )
            ),
            Control(
                "feral_campaign_terrain_pixel_density", "Campaign terrain density",
                "Texel density for campaign terrain. Higher values sharpen the map at the cost of memory and fill rate.",
                CAMPAIGN, ControlKind.CHOICE,
                listOf(ControlChoice("15", "Standard"), ControlChoice("23", "High")),
                note = "15 and 23 are the values observed in the preset blocks."
            ),
            toggle(
                "feral_smoke_dust", "Smoke and dust",
                "Battle smoke, dust clouds and similar atmospheric particles.",
                CAMPAIGN
            ),
            toggle(
                "feral_show_ai_movements", "Show AI movements",
                "Shows AI army movement paths on the campaign map. Purely informational, not a performance control.",
                CAMPAIGN
            )
        )
    )
}
