package dev.legendtuner.app.data

enum class Preset(
    val keyName: String,
    val index: Int,
    val label: String,
    val shortLabel: String,
    val description: String
) {
    BATTERY_SAVER(
        keyName = "BatterySaver",
        index = 0,
        label = "Battery Saver",
        shortLabel = "Battery",
        description = "Maximum battery life"
    ),
    GRAPHICS(
        keyName = "Graphics",
        index = 1,
        label = "Graphics",
        shortLabel = "Graphics",
        description = "Balanced quality"
    ),
    PERFORMANCE(
        keyName = "Performance",
        index = 2,
        label = "Performance",
        shortLabel = "Performance",
        description = "Lowest effects"
    );

    companion object {
        fun fromIndex(index: Int): Preset = entries.firstOrNull { it.index == index } ?: GRAPHICS
        fun fromKeyName(name: String): Preset? = entries.firstOrNull { it.keyName == name }
    }
}
