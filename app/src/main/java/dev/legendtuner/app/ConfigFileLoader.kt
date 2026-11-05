package dev.legendtuner.app

import dev.legendtuner.app.access.FileAccess
import dev.legendtuner.app.data.ConfigDocument
import dev.legendtuner.app.data.FeralTitle

data class ConfigLoadFailure(
    val message: String,
    val notFound: Boolean = false
)

sealed interface ConfigLoadResult {
    data class Success(val config: LoadedConfig) : ConfigLoadResult
    data class Failure(val failure: ConfigLoadFailure) : ConfigLoadResult
}

/**
 * Reads and validates one Feral registry file. Keeping this boundary separate from the
 * ViewModel makes the read path independently testable and keeps UI state out of file I/O.
 */
class ConfigFileLoader {
    suspend fun load(access: FileAccess, title: FeralTitle?): ConfigLoadResult {
        val label = title?.label ?: "Manual file"
        val file = try {
            access.locate()
        } catch (e: Exception) {
            return ConfigLoadResult.Failure(
                ConfigLoadFailure("Could not inspect the $label preferences file: ${e.message ?: "read failed"}")
            )
        }
        if (file == null) {
            return ConfigLoadResult.Failure(
                ConfigLoadFailure(
                    "The $label preferences file was not found. Launch the game once, or use manual mode.",
                    notFound = true
                )
            )
        }

        val bytes = try {
            access.read(file.path)
        } catch (e: Exception) {
            return ConfigLoadResult.Failure(
                ConfigLoadFailure("Could not read the $label preferences file: ${e.message ?: "read failed"}")
            )
        }

        val document = try {
            ConfigDocument.parse(bytes)
        } catch (e: Exception) {
            return ConfigLoadResult.Failure(
                ConfigLoadFailure("Could not parse the $label preferences file: ${e.message ?: "invalid XML"}")
            )
        }

        try {
            if (title?.legacyGuided == true) document.validate() else document.validateRegistry()
        } catch (e: Exception) {
            return ConfigLoadResult.Failure(
                ConfigLoadFailure("The $label preferences file is not valid: ${e.message ?: "validation failed"}")
            )
        }

        return ConfigLoadResult.Success(LoadedConfig(document, access, file, bytes))
    }
}
