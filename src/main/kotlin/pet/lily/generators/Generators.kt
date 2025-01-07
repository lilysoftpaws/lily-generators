package pet.lily.generators

import org.bukkit.plugin.java.JavaPlugin
import pet.lily.generators.manager.Manager
import pet.lily.generators.utils.getImplementations

class Generators : JavaPlugin() {
    override fun onEnable() {
        getImplementations<Manager>("pet.lily.manager").forEach {
            it.initialize(plugin)
        }
    }

    override fun onDisable() {

    }
}

val plugin: Generators
get() = JavaPlugin.getPlugin(Generators::class.java)