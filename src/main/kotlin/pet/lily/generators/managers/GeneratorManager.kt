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
import pet.lily.generators.localization.sendLocalizedMessage
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
        // check if held item is a generator
        val generatorType = itemInHand.getPersistentData(generatorTypeKey, PersistentDataType.STRING) ?: return
        val generatorData = GeneratorRegistry.processedGenerators[generatorType] ?: return

        // todo: check generator slots

        // play sound and send message
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, 2f, 2f)

        player.sendLocalizedMessage(
            key = "generators.place.success",
            placeholders = mapOf("display-name" to generatorData.displayName)
        )

        // create generator in database
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
        val generatorData = GeneratorDao.getGeneratorByLocation(block.location) ?: return
        val generatorType = GeneratorRegistry.processedGenerators[generatorData.type] ?: return

        // check if player owns the generator
        if (generatorData.playerId != player.uniqueId) return

        // cancel the event and remove the generator
        isCancelled = true
        block.type = Material.AIR

        // play sound and send message
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, 2f, 2f)
        player.sendLocalizedMessage(
            key = "generators.pickup.success",
            placeholders = mapOf("display-name" to generatorType.displayName)
        )

        // remove the generator from the database
        GeneratorDao.deleteGenerator(generatorData.id)

        // return the generator to the player's inventory
        player.inventory.addItem(generatorType.itemTemplate)
    }
}