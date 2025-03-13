package pet.lily.generators.database.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import pet.lily.generators.database.model.PlayerModel
import pet.lily.generators.database.tables.PlayerTable
import java.util.UUID

object PlayerDao {
    fun createPlayer(playerId: UUID): PlayerModel = transaction {
        PlayerTable.insert {
            it[id] = playerId
        }
        PlayerModel(playerId)
    }

    fun getPlayerById(playerId: UUID): PlayerModel? = transaction {
        PlayerTable.selectAll().where { PlayerTable.id eq playerId }
            .map { PlayerModel(it[PlayerTable.id].value) }
            .singleOrNull()
    }

    fun deletePlayer(playerId: UUID): Boolean = transaction {
        PlayerTable.deleteWhere { PlayerTable.id eq playerId } > 0
    }
}
