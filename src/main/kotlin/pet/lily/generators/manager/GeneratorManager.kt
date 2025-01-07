package pet.lily.generators.manager

import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import pet.lily.generators.Generators
import pet.lily.generators.plugin

object GeneratorDataKeys {
    val GENERATOR_TYPE_KEY = NamespacedKey(plugin, "generators.generator_type")
}

object GeneratorManager : Manager, Listener {
    override fun initialize(plugin: Generators) {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun BlockPlaceEvent.onPlace() {

    }
}