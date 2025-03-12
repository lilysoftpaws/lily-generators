package pet.lily.generators.managers

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import pet.lily.generators.Generators
import pet.lily.generators.database.dao.PlayerDao

@Suppress("unused")
object PlayerManager : Manager, Listener {
    override fun initialize(plugin: Generators) {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun PlayerJoinEvent.onPlayerJoin() {
        if (PlayerDao.getPlayerById(player.uniqueId) == null) {
            // todo: store locale for i18n
            PlayerDao.createPlayer(player.uniqueId)
        }
    }
}