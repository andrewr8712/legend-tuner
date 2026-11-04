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
    val guided: Boolean = false
) {
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
        guided = true
    )

    /** Titles with a verified package and registry root. Order is the picker's default order. */
    val ALL: List<FeralTitle> = listOf(
        GRID_LEGENDS,
        FeralTitle(
            "grid-autosport", "GRID Autosport", "com.feralinteractive.gridas",
            listOf("HKEY_CURRENT_USER", "Software", "Feral Interactive", "GRID Autosport", "Setup")
        ),
        FeralTitle(
            "alien-isolation", "Alien: Isolation", "com.feralinteractive.alienisolation_android",
            listOf("HKEY_CURRENT_USER", "Software", "Feral Interactive", "Alien Isolation", "Setup")
        ),
        FeralTitle(
            "saints-row-3", "Saints Row: The Third", "com.feralinteractive.saintsrow3_android",
            listOf("HKEY_CURRENT_USER", "Software", "Feral Interactive", "Saints Row The Third", "Setup")
        ),
        FeralTitle(
            "napoleon", "NAPOLEON", "com.feralinteractive.napoleon_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "The Creative Assembly Limited",
                "Napoleon Total War - Mobile", "Setup"
            )
        ),
        FeralTitle(
            "hitman-blood-money", "Hitman: Blood Money", "com.feralinteractive.hitmanbloodmoney_android",
            listOf("HKEY_CURRENT_USER", "Software", "Feral Interactive", "Hitman Blood Money", "Setup")
        ),
        FeralTitle(
            "lara-croft-gol", "Lara Croft: Guardian of Light", "com.feralinteractive.laracroftgol_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "Feral Interactive",
                "Lara Croft and the Guardian of Light", "Setup"
            )
        ),
        FeralTitle(
            "lara-croft-too", "Lara Croft: Temple of Osiris", "com.feralinteractive.laracrofttoo_android",
            listOf(
                "HKEY_CURRENT_USER", "Software", "Feral Interactive",
                "Lara Croft and the Temple of Osiris", "Setup"
            )
        )
    )

    fun byPackage(packageName: String): FeralTitle? = ALL.firstOrNull { it.packageName == packageName }

    fun installed(context: Context): List<FeralTitle> = ALL.filter {
        runCatching { context.packageManager.getPackageInfo(it.packageName, 0) }.isSuccess
    }
}
