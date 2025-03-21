package pet.lily.generators.managers

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import pet.lily.generators.Generators
import pet.lily.generators.database.dao.PlayerDao
import pet.lily.generators.localization.LocalizationManager
import pet.lily.generators.plugin

object PlayerManager : IManager, Listener {
    override fun initialize(plugin: Generators) {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun PlayerJoinEvent.onPlayerJoin() {
        if (PlayerDao.getPlayerById(player.uniqueId) == null) {
            val availableLocales = LocalizationManager.availableLocales
            val playerLocale = player.locale().toString()

            PlayerDao.createPlayer(
                player.uniqueId,
                if (availableLocales.contains(playerLocale)) playerLocale else plugin.configuration.main.defaultLanguageLocale,
                plugin.configuration.main.defaultGeneratorSlots
            )
        }
    }
}