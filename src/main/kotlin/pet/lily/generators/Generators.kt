package pet.lily.generators

import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.coroutines.annotations.installCoroutineSupport
import org.incendo.cloud.paper.LegacyPaperCommandManager
import pet.lily.generators.database.DatabaseFactory
import pet.lily.generators.managers.Manager
import pet.lily.generators.registry.GeneratorRegistry
import pet.lily.generators.utils.*
import java.io.File

class Generators : JavaPlugin() {

    lateinit var configuration: Configuration

    override fun onEnable() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        // load configuration
        configuration = loadConfiguration(File(dataFolder, "config.yaml").path)
        logger.info { "loaded ${configuration.generators.types.size} generators and ${configuration.generators.drops.size} drops" }

        // initialize database
        DatabaseFactory.initialize(File(dataFolder, configuration.database.databasePath).path)

        // initialize managers
        getImplementations<Manager>("pet.lily.generators.managers").forEach {
            it.initialize(plugin)
            plugin.logger.fine { "initialized manager $it" }
        }

        // initialize commands and suggestions
        val commandManager = LegacyPaperCommandManager(this, ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity())
        val annotationParser = AnnotationParser(commandManager, CommandSender::class.java)
        annotationParser.installCoroutineSupport()

        val instances = listOf(
            getAnnotatedMethods<Command>("pet.lily.generators.commands").map { it.first },
        ).flatten().distinct()

        instances.forEach { instance ->
            annotationParser.parse(instance)
            plugin.logger.fine { "registered command from ${instance::class.simpleName}" }
        }
    }
}

val plugin: Generators
get() = JavaPlugin.getPlugin(Generators::class.java)

val configuration: Configuration
get() = plugin.configuration
