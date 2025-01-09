package pet.lily.generators.manager

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import pet.lily.generators.Generators
import pet.lily.generators.database.repositories.PlayerRepository

@Suppress("unused")
object PlayerManager : Manager, Listener {
    override fun initialize(plugin: Generators) {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun PlayerJoinEvent.onPlayerJoin() {
        if (PlayerRepository.getPlayer(player.uniqueId) == null) {
            PlayerRepository.createPlayer(player.uniqueId)
        }
    }
}