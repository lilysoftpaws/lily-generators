package pet.lily.generators.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.context.CommandContext
import pet.lily.generators.database.dao.PlayerDao
import pet.lily.generators.localization.LocalizationManager
import pet.lily.generators.localization.sendLocalizedMessage

object GeneratorsLocaleCommand : ICommand {
    @Command("generators locale [locale]")
    @Permission("generators.locale")
    fun generatorsLocale(
        sender: Player,
        @Argument("locale", suggestions = "locale-suggestions") locale: String
    ) {
        if (locale.isEmpty()) {
            val playerModel = PlayerDao.getPlayerById(sender.uniqueId) ?: return
            sender.sendLocalizedMessage(
                key = "locale.current",
                placeholders = mapOf("locale" to playerModel.locale)
            )
            return
        }

        val availableLocales = LocalizationManager.availableLocales
        if (!availableLocales.contains(locale)) {
            sender.sendLocalizedMessage(
                key = "locale.invalid",
                placeholders = mapOf("locale" to locale)
            )
            return
        }

        val updateSuccess = PlayerDao.updatePlayerLocale(sender.uniqueId, locale)
        sender.sendLocalizedMessage(
            key = if (updateSuccess) "locale.updated" else "locale.error",
            placeholders = mapOf("locale" to locale)
        )
    }

    @Suggestions("locale-suggestions")
    fun localeSuggestions(context: CommandContext<CommandSender>, input: String): List<String> {
        return LocalizationManager.availableLocales
            .filter { it.startsWith(input, ignoreCase = true) }
    }
}
