package pet.lily.generators

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.coroutines.annotations.installCoroutineSupport
import org.incendo.cloud.paper.LegacyPaperCommandManager
import pet.lily.generators.commands.ICommand
import pet.lily.generators.database.DatabaseFactory
import pet.lily.generators.localization.LocalizationManager
import pet.lily.generators.managers.IManager
import pet.lily.generators.utils.ReflectionUtils
import java.io.File

class Generators : JavaPlugin() {

    lateinit var configuration: Configuration
    lateinit var economy: Economy

    override fun onEnable() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        if (!setupEconomy()) {
            logger.warning { "Vault not found, disabling plugin" }
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        // load configuration
        configuration = loadConfiguration(File(dataFolder, "config.yaml").path)
        logger.info { "Loaded ${configuration.generators.types.size} generators and ${configuration.generators.drops.size} drops" }
        logger.info { "Available locales: ${LocalizationManager.availableLocales.joinToString(", ")}" }

        // initialize database
        DatabaseFactory.initialize(File(dataFolder, configuration.database.databasePath).path)

        // initialize managers
        ReflectionUtils.getImplementations<IManager>("pet.lily.generators.managers").forEach {
            it.initialize(plugin)
            plugin.logger.fine { "Initialized manager $it" }
        }

        // initialize commands and suggestions
        val commandManager = LegacyPaperCommandManager(this, ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity())
        val annotationParser = AnnotationParser(commandManager, CommandSender::class.java)
        annotationParser.installCoroutineSupport()

        ReflectionUtils.getImplementations<ICommand>("pet.lily.generators.commands").forEach {
            annotationParser.parse(it)
            plugin.logger.fine { "Registered commands and suggestions in ${it::class.simpleName}" }
        }
    }

    private fun setupEconomy(): Boolean {
        val response = server.servicesManager.getRegistration(Economy::class.java) ?: return false
        economy = response.provider
        return true
    }
}

val plugin: Generators
get() = JavaPlugin.getPlugin(Generators::class.java)
