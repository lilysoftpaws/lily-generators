package pet.lily.generators.managers

import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import pet.lily.generators.Generators
import pet.lily.generators.localization.sendLocalizedMessage
import pet.lily.generators.plugin
import pet.lily.generators.registry.ItemRegistry
import pet.lily.generators.utils.NumberUtils.formatCurrency
import pet.lily.generators.utils.ItemStackUtils.getPersistentData

object DropManager : IManager, Listener {
    val dropTypeKey = NamespacedKey(plugin, "generators.drop-type")

    override fun initialize(plugin: Generators) {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun PlayerInteractEvent.onPlayerInteract() {
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return
        if (hand != EquipmentSlot.HAND) return

        val player = player
        val itemInHand = player.inventory.itemInMainHand

        // check if the item in hand is a drop
        val dropType = itemInHand.getPersistentData(dropTypeKey, PersistentDataType.STRING) ?: return
        val dropData = ItemRegistry.processedDrops[dropType] ?: return

        val inventory = player.inventory
        var totalAmount = 0
        var totalValue = 0.0

        // iterate through the inventory and sell all items of the specified drop type
        for (slot in 0 until inventory.size) {
            val item = inventory.getItem(slot) ?: continue
            val itemDropType = item.getPersistentData(dropTypeKey, PersistentDataType.STRING)
            if (itemDropType == dropType) {
                totalAmount += item.amount
                totalValue += item.amount * dropData.value
                inventory.setItem(slot, null)
            }
        }

        // add the value to the player's balance
        plugin.economy.depositPlayer(player, totalValue)

        // notify the player
        player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
        player.sendLocalizedMessage(
            key = "drops.sell.success",
            placeholders = mapOf(
                "quantity" to totalAmount,
                "display-name" to dropData.displayName,
                "value" to totalValue.formatCurrency()
            )
        )
    }
}
