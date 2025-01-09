package pet.lily.generators.database.repositories

import org.bukkit.Location
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import pet.lily.generators.database.DatabaseFactory.dbLock
import pet.lily.generators.database.entities.GeneratorEntity
import pet.lily.generators.database.entities.PlayerEntity
import pet.lily.generators.database.tables.GeneratorTable
import java.util.UUID
import kotlin.concurrent.read
import kotlin.concurrent.write

object PlayerRepository {
    fun createPlayer(playerId: UUID): PlayerEntity = dbLock.write {
        transaction {
            PlayerEntity.new(playerId) {

            }
        }
    }

    fun getPlayer(playerId: UUID): PlayerEntity? = dbLock.read {
        PlayerEntity.findById(playerId)
    }

    fun addGenerator(playerId: UUID, type: String, location: Location): GeneratorEntity? {
        return addGenerator(playerId, type, location.blockX, location.blockY, location.blockZ)
    }

    fun addGenerator(playerId: UUID, type: String, x: Int, y: Int, z: Int): GeneratorEntity? = dbLock.write {
        transaction {
            val player = PlayerEntity.findById(playerId) ?: return@transaction null
            GeneratorEntity.new {
                this.player = player
                this.type = type
                this.x = x
                this.y = y
                this.z = z
            }
        }
    }

    fun getGenerator(location: Location): GeneratorEntity? {
        return getGenerator(location.blockX, location.blockY, location.blockZ)
    }

    fun getGenerator(x: Int, y: Int, z: Int): GeneratorEntity? = dbLock.read {
        transaction {
            GeneratorEntity.find {
                GeneratorTable.x eq x and
                        (GeneratorTable.y eq y) and
                        (GeneratorTable.z eq z)
            }
        }.firstOrNull()
    }

    fun getGeneratorsForPlayer(playerId: UUID): List<GeneratorEntity> = dbLock.read {
        transaction {
            GeneratorEntity.find { GeneratorTable.player eq playerId }.toList()
        }
    }
}