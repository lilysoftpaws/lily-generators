package pet.lily.generators.manager

import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType
import pet.lily.generators.Generators
import pet.lily.generators.database.dao.GeneratorDao
import pet.lily.generators.database.model.Location
import pet.lily.generators.plugin
import pet.lily.generators.utils.getPersistentData

@Suppress("unused")
object GeneratorManager : Manager, Listener {
    val generatorTypeKey = NamespacedKey(plugin, "generators.generator_type")

    override fun initialize(plugin: Generators) {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun BlockPlaceEvent.onBlockPlace() {
        // todo: check generator slots
        val generatorType = itemInHand.getPersistentData(generatorTypeKey, PersistentDataType.STRING)
            ?: return

        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, 2f, 2f)

        GeneratorDao.createGenerator(
            generatorType,
            Location(blockPlaced.location.x.toInt(), blockPlaced.location.y.toInt(), blockPlaced.location.z.toInt()),
            player.uniqueId
        )
    }
}