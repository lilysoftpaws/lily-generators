package pet.lily.generators.managers

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import pet.lily.generators.Generators
import pet.lily.generators.database.dao.GeneratorDao
import pet.lily.generators.plugin
import pet.lily.generators.registry.GeneratorRegistry
import pet.lily.generators.utils.getPersistentData

@Suppress("unused")
object GeneratorManager : Manager, Listener {
    val generatorTypeKey = NamespacedKey(plugin, "generators.generator-type")

    override fun initialize(plugin: Generators) {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun BlockPlaceEvent.onBlockPlace() {
        val generatorType = itemInHand.getPersistentData(generatorTypeKey, PersistentDataType.STRING)
            ?: return

        // todo: check generator slots

        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, 2f, 2f)

        GeneratorDao.createGenerator(generatorType, blockPlaced.location, player.uniqueId)
    }

    @EventHandler
    fun BlockBreakEvent.onBlockBreak() {
        GeneratorDao.getGeneratorByLocation(block.location)?.let {
            player.permissionValue("generators.break").let { canBreak ->
                if (canBreak.toBoolean() != true) {
                    isCancelled = true
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.LEFT_CLICK_BLOCK || event.hand != EquipmentSlot.HAND) return

        val block = event.clickedBlock ?: return
        val generator = GeneratorDao.getGeneratorByLocation(block.location) ?: return
        val generatorType = GeneratorRegistry.processedGenerators[generator.type] ?: return

        if (generator.playerId != event.player.uniqueId) return

        event.isCancelled = true
        block.type = Material.AIR

        event.player.sendMessage("you have picked up a ${generator.type}")

        GeneratorDao.deleteGenerator(generator.id)

        event.player.inventory.addItem(generatorType.itemTemplate)
    }
}