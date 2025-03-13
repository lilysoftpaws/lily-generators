package pet.lily.generators.localization

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import pet.lily.generators.plugin
import pet.lily.generators.utils.MiniMessageUtils.asComponent
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object LocalizationManager {
    private const val BASE_NAME = "locale/messages"
    private val DEFAULT_LOCALES = listOf("en", "fr")
    const val DEFAULT_LOCALE = "en"

    private val bundles: MutableMap<Locale, ResourceBundle> = ConcurrentHashMap()

    val availableLocales: List<String> by lazy {
        copyDefaultLocales()
        loadAvailableLocales()
    }

    /**
     * Retrieves a localized message for the given key and locale.
     *
     * @param key The translation key.
     * @param locale The locale to use for the message.
     * @param placeholders A map of placeholders to replace in the message.
     * @return A MiniMessage [Component] with the localized message.
     */
    fun getMessage(
        key: String,
        locale: String = DEFAULT_LOCALE,
        placeholders: Map<String, Component> = emptyMap()
    ): Component {
        val resolvedLocale = Locale.forLanguageTag(locale)
        val bundle = bundles.getOrPut(resolvedLocale) {
            loadResourceBundle(resolvedLocale) ?: loadResourceBundle(Locale.forLanguageTag(DEFAULT_LOCALE))
            ?: throw MissingResourceException(
                "Default locale '$DEFAULT_LOCALE' not found.",
                BASE_NAME,
                key
            )
        }

        val rawMessage = bundle.getString(key) ?: throw MissingResourceException(
            "Message key '$key' not found for locale '$locale'.",
            BASE_NAME,
            key
        )

        return rawMessage.asComponent(createTagResolver(placeholders))
    }

    private fun loadResourceBundle(locale: Locale): ResourceBundle? {
        return try {
            ResourceBundle.getBundle(BASE_NAME, locale)
        } catch (e: MissingResourceException) {
            plugin.logger.warning { "Locale '${locale.language}' not found. Falling back to default locale." }
            null
        }
    }

    private fun createTagResolver(placeholders: Map<String, Component>): TagResolver {
        return TagResolver.resolver(
            placeholders.map { (key, value) ->
                TagResolver.resolver(key, Tag.inserting(value))
            }
        )
    }

    private fun copyDefaultLocales() {
        val resourceFolder = File(plugin.dataFolder, BASE_NAME.substringBeforeLast("/")).apply { mkdirs() }

        DEFAULT_LOCALES.forEach { locale ->
            val resourceName = "/${BASE_NAME}_$locale.properties"
            val targetFile = File(resourceFolder, "messages_$locale.properties")

            if (!targetFile.exists()) {
                LocalizationManager::class.java.getResourceAsStream(resourceName)?.use { inputStream ->
                    FileOutputStream(targetFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    plugin.logger.info { "Copied missing locale file: $resourceName to ${targetFile.absolutePath}" }
                } ?: plugin.logger.warning { "Locale resource '$resourceName' not found in JAR." }
            }
        }
    }

    private fun loadAvailableLocales(): List<String> {
        val resourceFolder = File(plugin.dataFolder, BASE_NAME.substringBeforeLast("/"))
        if (!resourceFolder.isDirectory) {
            plugin.logger.warning { "Resource folder '$resourceFolder' does not exist or is not a directory." }
            return emptyList()
        }

        return resourceFolder.listFiles { file ->
            file.isFile && file.name.startsWith("messages_") && file.name.endsWith(".properties")
        }?.map { file ->
            file.name.substringAfter("messages_").substringBefore(".properties")
        }.orEmpty()
    }
}
