package pet.lily.generators.manager

import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import pet.lily.generators.Generators
import pet.lily.generators.database.repositories.PlayerRepository
import pet.lily.generators.plugin

@Suppress("unused")
object GeneratorManager : Manager, Listener {
    val generatorTypeKey = NamespacedKey(plugin, "generators.generator_type")

    override fun initialize(plugin: Generators) {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun BlockPlaceEvent.onBlockPlace() {
        PlayerRepository.addGenerator(
            player.uniqueId,
            blockPlaced.type.name,
            blockPlaced.location
        )
    }
}