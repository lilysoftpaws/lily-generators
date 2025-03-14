package pet.lily.generators.localization

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import pet.lily.generators.database.dao.PlayerDao
import pet.lily.generators.utils.MiniMessageUtils.asComponent

/**
 * Sends a localized message to the player.
 *
 * @param key The translation key.
 * @param prefixed Whether to include the prefix in the message.
 * @param placeholders A map of placeholders to replace in the message. Supports both [String] and [Component].
 */
fun Player.sendLocalizedMessage(
    key: String,
    prefixed: Boolean = true,
    placeholders: Map<String, Any> = emptyMap()
) {
    val playerModel = PlayerDao.getPlayerById(uniqueId) ?: return
    val locale = playerModel.locale

    val resolvedPlaceholders = placeholders.mapValues { (_, value) ->
        when (value) {
            is Component -> value
            else -> value.toString().asComponent()
        }
    }

    val message = LocalizationManager.getMessage(key, locale, resolvedPlaceholders)
    val prefix = if (prefixed) LocalizationManager.getMessage("prefix", locale) else Component.empty()

    sendMessage(prefix.append(message))
}
