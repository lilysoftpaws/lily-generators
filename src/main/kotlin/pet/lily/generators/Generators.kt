package pet.lily.generators

import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.coroutines.annotations.installCoroutineSupport
import org.incendo.cloud.paper.LegacyPaperCommandManager
import pet.lily.generators.database.DatabaseFactory
import pet.lily.generators.localization.LocalizationManager
import pet.lily.generators.managers.Manager
import pet.lily.generators.utils.ReflectionUtils
import java.io.File
import kotlin.math.log

class Generators : JavaPlugin() {

    lateinit var configuration: Configuration

    override fun onEnable() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        // load configuration
        configuration = loadConfiguration(File(dataFolder, "config.yaml").path)
        logger.info { "Loaded ${configuration.generators.types.size} generators and ${configuration.generators.drops.size} drops" }
        logger.info { "Available locales: ${LocalizationManager.availableLocales.joinToString(", ")}" }

        // initialize database
        DatabaseFactory.initialize(File(dataFolder, configuration.database.databasePath).path)

        // initialize managers
        ReflectionUtils.getImplementations<Manager>("pet.lily.generators.managers").forEach {
            it.initialize(plugin)
            plugin.logger.fine { "Initialized manager $it" }
        }

        // initialize commands and suggestions
        val commandManager = LegacyPaperCommandManager(this, ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity())
        val annotationParser = AnnotationParser(commandManager, CommandSender::class.java)
        annotationParser.installCoroutineSupport()

        val instances = listOf(
            ReflectionUtils.getAnnotatedMethods<Command>("pet.lily.generators.commands").map { it.first },
        ).flatten().distinct()

        instances.forEach { instance ->
            annotationParser.parse(instance)
            plugin.logger.fine { "Registered command from ${instance::class.simpleName}" }
        }
    }
}

val plugin: Generators
get() = JavaPlugin.getPlugin(Generators::class.java)

val configuration: Configuration
get() = plugin.configuration
