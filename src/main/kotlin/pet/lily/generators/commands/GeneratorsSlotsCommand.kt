package pet.lily.generators.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import pet.lily.generators.database.dao.PlayerDao
import pet.lily.generators.localization.sendLocalizedMessage
import java.util.UUID

object GeneratorsSlotsCommand : ICommand {
    @Command("generators slots set <player> <slots>")
    @Permission("generators.slots.set")
    fun setSlots(
        sender: CommandSender,
        @Argument("player") target: Player,
        @Argument("slots") slots: Int
    ) {
        if (slots < 0) {
            sender.sendLocalizedMessage(
                key = "generators.slots.invalid",
                placeholders = mapOf("slots" to slots)
            )
            return
        }

        val success = PlayerDao.setPlayerSlots(target.uniqueId, slots)
        if (success) {
            sender.sendLocalizedMessage(
                key = "generators.slots.set.success",
                placeholders = mapOf("player" to target.name, "slots" to slots)
            )
            target.sendLocalizedMessage(
                key = "generators.slots.updated",
                placeholders = mapOf("slots" to slots)
            )
        } else {
            sender.sendLocalizedMessage(
                key = "generators.slots.set.error",
                placeholders = mapOf("player" to target.name)
            )
        }
    }
}
