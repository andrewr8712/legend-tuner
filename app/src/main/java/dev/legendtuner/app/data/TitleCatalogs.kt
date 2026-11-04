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
    private const val TIERS = "Tier names are inferred from the stored value; verify in-game."

    private fun scene(key: String, label: String, description: String) =
        Control(key, label, description, SCENE, ControlKind.CHOICE, tier3, note = TIERS)

    val catalog = TitleCatalog(
        groups = listOf(IMAGE, SCENE, PERF),
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
                PERF, ControlKind.STEPPER, min = 30, max = 60, suffix = " fps"
            ),
            Control(
                "GfxResponsiveFps", "Active frame cap",
                "Frame cap while the player is moving or driving. This is the cap that matters during play.",
                PERF, ControlKind.STEPPER, min = 30, max = 60, suffix = " fps"
            ),
            Control(
                "GfxResolutionScale", "Resolution scale",
                "Internal render scale multiplier. 1.0 renders at the panel resolution; lower values are softer but faster.",
                PERF, ControlKind.STEPPER, min = 1, max = 1,
                note = "Stored as a decimal (1.000000); edit it in Raw XML until the decimal path is verified."
            )
        )
    )
}

object NapoleonCatalog {
    private const val QUALITY = "Battle quality"
    private const val DISPLAY = "Display"

    private fun q(key: String, label: String, description: String) =
        Control(key, label, description, QUALITY, ControlKind.CHOICE, intLevels(3))

    val catalog = TitleCatalog(
        groups = listOf(QUALITY, DISPLAY),
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
            step(
                "IdleFPS", "Idle frame cap",
                "Frame cap when the player is still or the scene is static. Lower values save battery in quiet moments.",
                120, FRAME
            ),
            step(
                "ResponsiveFPS", "Active frame cap",
                "Frame cap while input is active. This is the cap that matters during play and in firefights.",
                120, FRAME
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
                DISPLAY, ControlKind.STEPPER, min = 480, max = 2160, suffix = " px"
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
                DISPLAY, ControlKind.STEPPER, min = 480, max = 2160, suffix = " px"
            )
        )
    )
}

object LaraTooCatalog {
    private const val IMAGE = "Image quality"
    private const val DISPLAY = "Display"

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
                DISPLAY, ControlKind.STEPPER, min = 480, max = 2160, suffix = " px"
            )
        )
    )
}
