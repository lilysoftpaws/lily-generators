package pet.lily.generators.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.context.CommandContext
import pet.lily.generators.registry.GeneratorRegistry

object GeneratorsCommand {

    @Command("generators give <type> [target] [quantity]")
    @Permission("generators.give")
    fun generatorsGiveCommand(
        sender: Player,
        @Argument("type", suggestions = "generator-type-suggestions") type: String,
        @Argument("target") target: Player = sender,
        @Argument("quantity") quantity: Int = 1
    ) {
        val generator = GeneratorRegistry.processedGenerators[type]
            ?: run {
                sender.sendMessage("[generators] generator type '$type' not found")
                return
            }

        val item = generator.itemTemplate.clone().apply {
            amount = quantity.coerceIn(1, 64)
        }

        target.inventory.addItem(item)
        sender.sendMessage("[generators] gave ${item.amount}x $type to ${target.name}")
    }

    @Suggestions("generator-type-suggestions")
    fun generatorTypeSuggestions(context: CommandContext<CommandSender>, input: String): List<String> {
        return GeneratorRegistry.processedGenerators.keys
            .filter { it.startsWith(input, true) }
    }
}

