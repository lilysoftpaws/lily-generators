package pet.lily.generators.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.context.CommandContext
import pet.lily.generators.localization.sendLocalizedMessage
import pet.lily.generators.registry.ItemRegistry

object GeneratorGiveCommand : ICommand {
    @Command("generators give <type> [target] [quantity]")
    @Permission("generators.give")
    fun generatorsGive(
        sender: Player,
        @Argument("type", suggestions = "generator-type-suggestions") type: String,
        @Argument("target") target: Player = sender,
        @Argument("quantity") quantity: Int = 1
    ) {
        val generatorData = ItemRegistry.processedGenerators[type]
            ?: run {
                sender.sendLocalizedMessage(
                    key = "generators.give.error",
                    placeholders = mapOf("type" to type)
                )
                return
            }

        val limitedQuantity = quantity.coerceIn(1, 64)
        val item = generatorData.itemTemplate.clone().apply {
            amount = limitedQuantity
        }

        target.inventory.addItem(item)
        sender.sendLocalizedMessage(
            key = "generators.give.success",
            placeholders = mapOf(
                "quantity" to limitedQuantity,
                "display-name" to generatorData.displayName,
                "target" to target.name
            )
        )
    }

    @Suggestions("generator-type-suggestions")
    fun generatorTypeSuggestions(context: CommandContext<CommandSender>, input: String): List<String> {
        return ItemRegistry.processedGenerators.keys
            .filter { it.startsWith(input, true) }
    }
}