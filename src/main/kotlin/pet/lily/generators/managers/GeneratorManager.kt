package pet.lily.generators.managers

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
import pet.lily.generators.utils.ItemStackUtils.getPersistentData

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

        // todo: configuration for language and sound
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, 2f, 2f)
        player.sendMessage("you have placed a $generatorType")

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
    fun PlayerInteractEvent.onPlayerInteract() {
        if (action != Action.LEFT_CLICK_BLOCK || hand != EquipmentSlot.HAND) return

        val block = clickedBlock ?: return
        val generator = GeneratorDao.getGeneratorByLocation(block.location) ?: return
        val generatorType = GeneratorRegistry.processedGenerators[generator.type] ?: return

        // check if player owns the generator
        if (generator.playerId != player.uniqueId) return

        // cancel the event and remove the generator
        isCancelled = true
        block.type = Material.AIR

        // todo: configuration for language and sound
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, 2f, 2f)
        player.sendMessage("you have picked up a ${generator.type}")

        // remove the generator from the database
        GeneratorDao.deleteGenerator(generator.id)

        // return the generator to the player's inventory
        player.inventory.addItem(generatorType.itemTemplate)
    }
}