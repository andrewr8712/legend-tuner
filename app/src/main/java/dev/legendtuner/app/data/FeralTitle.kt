package dev.legendtuner.app.data

import android.content.Context

/**
 * One supported Feral title. [registryRoot] is the absolute path to the game's settings subtree;
 * it is null until a real preferences sample has been inspected. [guided] means a catalogue exists
 * and the friendly editor is available; titles without one still open in the registry browser.
 */
data class FeralTitle(
    val id: String,
    val label: String,
    val packageName: String,
    val registryRoot: List<String>? = null,
    /** Where the guided controls live when it differs from [registryRoot] (both Lara titles). */
    val controlsRoot: List<String>? = null,
    val titleCatalog: TitleCatalog? = null,
    val legacyGuided: Boolean = false
) {
    val guided: Boolean get() = titleCatalog != null || legacyGuided
    val valueRoot: List<String>? get() = controlsRoot ?: registryRoot
    val monogram: String
        get() {
            val words = label.replace(Regex("[^A-Za-z0-9 ]"), " ")
                .split(' ')
                .filter { it.isNotBlank() }
            return when {
                words.isEmpty() -> "??"
                words.size == 1 -> words[0].take(2).uppercase()
                else -> (words[0].first().toString() + words[1].first().toString()).uppercase()
            }
        }
}

object FeralTitles {
    val GRID_LEGENDS = FeralTitle(
        id = "grid-legends",
        label = "GRID Legends",
        packageName = "com.feralinteractive.gridlegends_android",
        registryRoot = listOf("HKEY_CURRENT_USER", "Software", "Feral Interactive", "GRIDLegends"),
        legacyGuided = true
    )

    /** Titles with a verified package and registry root. Order is the picker's default order. */
    val ALL: List<FeralTitle> = listOf(
        GRID_LEGENDS,
        FeralTitle(
            "grid-autosport", "GRID Autosport", "com.feralinteractive.gridas",
            listOf("HKEY_CURRENT_USER", "Software", "Feral Interactive", "GRID Autosport", "Setup"),
            titleCatalog = GridAutosportCatalog.catalog
        ),
        FeralTitle(
            "alien-isolation", "Alien: Isolation", "com.feralinteractive.alienisolation_android",
            listOf("HKEY_CURRENT_USER", "Software", "Feral Interactive", "Alien Isolation", "Setup"),
            titleCatalog = AlienIsolationCatalog.catalog
        ),
        FeralTitle(
            "saints-row-3", "Saints Row: The Third", "com.feralinteractive.saintsrow3_android",
            listOf("HKEY_CURRENT_USER", "Software", "Feral Interactive", "Saints Row The Third", "Setup"),
            titleCatalog = SaintsRowCatalog.catalog
        ),
        FeralTitle(
            "napoleon", "NAPOLEON", "com.feralinteractive.napoleon_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "The Creative Assembly Limited",
                "Napoleon Total War - Mobile", "Setup"
            ),
            titleCatalog = NapoleonCatalog.catalog
        ),
        FeralTitle(
            "empire-total-war", "Total War: EMPIRE", "com.feralinteractive.empire_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "The Creative Assembly Limited",
                "Total War EMPIRE", "Setup"
            ),
            titleCatalog = EmpireTotalWarCatalog.catalog
        ),
        FeralTitle(
            "rome-total-war", "ROME: Total War", "com.feralinteractive.rometw",
            listOf(
                "HKEY_CURRENT_USER", "Software", "The Creative Assembly Limited",
                "Rome Total War", "Setup"
            ),
            titleCatalog = RomeTotalWarCatalog.catalog
        ),
        FeralTitle(
            "medieval-2-total-war", "Medieval II: Total War", "com.feralinteractive.medieval2_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "Original Developer",
                "Medieval II Total War", "Setup"
            ),
            titleCatalog = Medieval2TotalWarCatalog.catalog
        ),
        FeralTitle(
            "rome-alexander", "ROME: Alexander", "com.feralinteractive.rometwalex",
            listOf(
                "HKEY_CURRENT_USER", "Software", "The Creative Assembly Limited",
                "Rome Total War - Alexander", "Setup"
            ),
            titleCatalog = RomeTotalWarCatalog.catalog
        ),
        FeralTitle(
            "rome-barbarian-invasion", "ROME: Barbarian Invasion", "com.feralinteractive.rometwbi",
            listOf(
                "HKEY_CURRENT_USER", "Software", "The Creative Assembly Limited",
                "Rome Total War - Barbarian Invasion", "Setup"
            ),
            titleCatalog = RomeTotalWarCatalog.catalog
        ),
        FeralTitle(
            "xcom-2", "XCOM 2 Collection", "com.feralinteractive.xcom2_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "Feral Interactive", "XCOM 2", "Setup"
            ),
            titleCatalog = Xcom2Catalog.catalog
        ),
        FeralTitle(
            "tropico-3", "Tropico 3", "com.feralinteractive.tropico3",
            listOf(
                "HKEY_CURRENT_USER", "Software", "Haemimont Games",
                "Tropico 3: Gold Edition", "Setup"
            ),
            titleCatalog = Tropico3Catalog.catalog
        ),
        FeralTitle(
            "hitman-blood-money", "Hitman: Blood Money", "com.feralinteractive.hitmanbloodmoney_android",
            listOf("HKEY_CURRENT_USER", "Software", "Feral Interactive", "Hitman Blood Money", "Setup"),
            titleCatalog = HitmanCatalog.catalog
        ),
        FeralTitle(
            "lara-croft-gol", "Lara Croft: Guardian of Light", "com.feralinteractive.laracroftgol_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "Feral Interactive",
                "Lara Croft and the Guardian of Light", "Setup"
            ),
            controlsRoot = listOf(
                "HKEY_CURRENT_USER", "Software", "Crystal Dynamics", "Lara Croft: GoL", "Graphics"
            ),
            titleCatalog = LaraGolCatalog.catalog
        ),
        FeralTitle(
            "lara-croft-too", "Lara Croft: Temple of Osiris", "com.feralinteractive.laracrofttoo_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "Feral Interactive",
                "Lara Croft and the Temple of Osiris", "Setup"
            ),
            controlsRoot = listOf(
                "HKEY_CURRENT_USER", "Software", "Crystal Dynamics",
                "Lara Croft and the Temple of Osiris", "Graphics"
            ),
            titleCatalog = LaraTooCatalog.catalog
        ),
        FeralTitle(
            "company-of-heroes", "Company of Heroes", "com.feralinteractive.companyofheroes_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "Feral Interactive", "Company of Heroes", "Setup"
            ),
            titleCatalog = CompanyOfHeroesCatalog.catalog
        ),
        FeralTitle(
            "tomb-raider", "Tomb Raider", "com.feralinteractive.tombraider_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "Crystal Dynamics", "Tomb Raider", "Setup"
            ),
            titleCatalog = TombRaiderCatalog.catalog
        ),
        FeralTitle(
            "hitman-absolution", "Hitman: Absolution", "com.feralinteractive.hitmanabsolution_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "IO Interactive", "Hitman Absolution"
            ),
            titleCatalog = HitmanAbsolutionCatalog.catalog
        ),
        FeralTitle(
            "railroads", "Sid Meier's Railroads!", "com.feralinteractive.railroads_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "Original Developer",
                "Sid Meier's Railroads!", "Setup"
            ),
            titleCatalog = RailroadsCatalog.catalog
        )
    )

    fun byPackage(packageName: String): FeralTitle? = ALL.firstOrNull { it.packageName == packageName }

    fun installed(context: Context): List<FeralTitle> = ALL.filter {
        runCatching { context.packageManager.getPackageInfo(it.packageName, 0) }.isSuccess
    }
}
