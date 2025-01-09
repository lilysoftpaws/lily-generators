package pet.lily.generators

import org.bukkit.plugin.java.JavaPlugin
import pet.lily.generators.database.DatabaseFactory
import pet.lily.generators.manager.Manager
import pet.lily.generators.utils.getImplementations
import java.io.File

class Generators : JavaPlugin() {
    override fun onEnable() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        val databaseFile = File(dataFolder, "database.sqlite")
        DatabaseFactory.initialize(databaseFile.path)

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