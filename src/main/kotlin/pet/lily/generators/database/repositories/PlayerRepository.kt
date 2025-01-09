package pet.lily.generators.database.repositories

import org.bukkit.Location
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import pet.lily.generators.database.DatabaseFactory.dbLock
import pet.lily.generators.database.entities.Generator
import pet.lily.generators.database.entities.Player
import pet.lily.generators.database.tables.Generators
import java.util.UUID
import kotlin.concurrent.read
import kotlin.concurrent.write

object PlayerRepository {
    fun createPlayer(playerId: UUID): Player = dbLock.write {
        transaction {
            Player.new(playerId) {

            }
        }
    }

    fun getPlayer(playerId: UUID): Player? = dbLock.read {
        Player.findById(playerId)
    }

    fun addGenerator(playerId: UUID, type: String, location: Location): Generator? {
        return addGenerator(playerId, type, location.blockX, location.blockY, location.blockZ)
    }

    fun addGenerator(playerId: UUID, type: String, x: Int, y: Int, z: Int): Generator? = dbLock.write {
        transaction {
            val player = Player.findById(playerId) ?: return@transaction null
            Generator.new {
                this.player = player
                this.type = type
                this.x = x
                this.y = y
                this.z = z
            }
        }
    }

    fun getGenerator(location: Location): Generator? {
        return getGenerator(location.blockX, location.blockY, location.blockZ)
    }

    fun getGenerator(x: Int, y: Int, z: Int): Generator? = dbLock.read {
        transaction {
            Generator.find {
                Generators.x eq x and
                        (Generators.y eq y) and
                        (Generators.z eq z)
            }
        }.firstOrNull()
    }

    fun getGeneratorsForPlayer(playerId: UUID): List<Generator> = dbLock.read {
        transaction {
            Generator.find { Generators.player eq playerId }.toList()
        }
    }
}