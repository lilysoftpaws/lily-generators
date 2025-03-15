package pet.lily.generators

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileOutputStream

@Serializable
data class Configuration(
    val main: MainConfiguration = MainConfiguration(),
    val database: DatabaseConfiguration = DatabaseConfiguration(),
    val generators: GeneratorsConfiguration = GeneratorsConfiguration()
)

@Serializable
data class MainConfiguration(
    @SerialName("generator-tick-rate") val generatorTickRate: Int = 20,
    @SerialName("default-language-locale") val defaultLanguageLocale: String = "en",
    @SerialName("default-generator-slots") val defaultGeneratorSlots: Int = 5
)

@Serializable
data class DatabaseConfiguration(
    @SerialName("database-path") val databasePath: String = "database.sqlite"
)

@Serializable
data class GeneratorsConfiguration(
    val types: Map<String, GeneratorType> = emptyMap(),
    val drops: Map<String, GeneratorDrop> = emptyMap()
)

@Serializable
data class GeneratorType(
    val material: String,
    @SerialName("display-name") val displayName: String,
    val lore: List<String>,
    val price: Double,
    val drop: String
)

@Serializable
data class GeneratorDrop(
    val material: String,
    @SerialName("display-name") val displayName: String,
    val lore: List<String>,
    val value: Double
)

fun loadConfiguration(configurationPath: String): Configuration {
    val file = File(configurationPath)

    if (!file.exists()) {
        Configuration::class.java.getResourceAsStream("/default-config.yaml")?.use { defaultStream ->
            FileOutputStream(file).use { outputStream ->
                defaultStream.copyTo(outputStream)
            }
        } ?: throw IllegalStateException("default-config.yaml missing from JAR")
    }

    val content = file.inputStream().use { inputStream ->
        inputStream.readAllBytes().decodeToString()
    }

    return Yaml.default.decodeFromString(Configuration.serializer(), content)
}