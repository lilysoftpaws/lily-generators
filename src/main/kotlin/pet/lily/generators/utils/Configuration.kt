package pet.lily.generators.utils

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.SerialName
import kotlinx.serialization.decodeFromString
import java.io.File
import java.io.FileOutputStream
import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    val main: MainConfiguration = MainConfiguration(),
    val database: DatabaseConfiguration = DatabaseConfiguration()
)

@Serializable
data class MainConfiguration(
    @SerialName("generator-tick-rate") val generatorTickRate: Int = 20
)

@Serializable
data class DatabaseConfiguration(
    @SerialName("database-path") val databasePath: String = "database.sqlite"
)

fun loadConfiguration(configurationPath: String): Configuration {
    val file = File(configurationPath)

    if (!file.exists()) {
        Configuration::class.java.getResourceAsStream("/default-config.toml")?.use { defaultStream ->
            FileOutputStream(file).use { outputStream ->
                defaultStream.copyTo(outputStream)
            }
        } ?: throw IllegalStateException("default-config.toml missing from JAR")
    }

    return file.inputStream().use { inputStream ->
        inputStream.readAllBytes().decodeToString().let { content ->
            Toml.decodeFromString<Configuration>(content)
        }
    }
}
