package pet.lily.generators

import org.bukkit.plugin.java.JavaPlugin
import pet.lily.generators.database.DatabaseFactory
import pet.lily.generators.manager.Manager
import pet.lily.generators.utils.getImplementations
import pet.lily.generators.utils.loadConfiguration
import java.io.File

class Generators : JavaPlugin() {
    override fun onEnable() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        val configuration = loadConfiguration(File(dataFolder, "config.toml").path)
        DatabaseFactory.initialize(File(dataFolder, configuration.database.databasePath).path)

        getImplementations<Manager>("pet.lily.generators.manager").forEach {
            plugin.logger.fine { "initializing manager $it" }
            it.initialize(plugin)
        }
    }

    override fun onDisable() {

    }
}

val plugin: Generators
get() = JavaPlugin.getPlugin(Generators::class.java)