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
import pet.lily.generators.registry.ItemRegistry

object GeneratorsCommand {
    @Command("generators give <type> [target] [quantity]")
    @Permission("generators.give")
    fun give(
        sender: Player,
        @Argument("type", suggestions = "generator-type-suggestions") type: String,
        @Argument("target") target: Player = sender,
        @Argument("quantity") quantity: Int = 1
    ) {
        val generatorData= ItemRegistry.processedGenerators[type]
            ?: run {
                sender.sendLocalizedMessage(
                    key = "generators.give.error",
                    placeholders = mapOf("type" to type)
                )
                return
            }

        val item = generatorData.itemTemplate.clone().apply {
            amount = quantity.coerceIn(1, 64)
        }

        target.inventory.addItem(item)
        sender.sendLocalizedMessage(
            key = "generators.give.success",
            placeholders = mapOf(
                "quantity" to item.amount.toString(),
                "display-name" to generatorData.displayName,
                "target" to target.name
            )
        )
    }

    @Command("generators locale [locale]")
    @Permission("generators.locale")
    fun locale(
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
        return LocalizationManager.availableLocales.filter { it.startsWith(input, ignoreCase = true) }
    }

    @Suggestions("generator-type-suggestions")
    fun generatorTypeSuggestions(context: CommandContext<CommandSender>, input: String): List<String> {
        return ItemRegistry.processedGenerators.keys
            .filter { it.startsWith(input, true) }
    }
}
