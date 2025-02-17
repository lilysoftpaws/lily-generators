package pet.lily.generators.database.dao

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pet.lily.generators.database.model.PlayerModel
import pet.lily.generators.database.tables.PlayerTable
import java.util.UUID

object PlayerDao {
    fun createPlayer(playerId: UUID): PlayerModel = transaction {
        PlayerTable
            .insertAndGetId {
                it[PlayerTable.id] = playerId
            }
        PlayerModel(playerId)
    }

    fun getPlayerById(playerId: UUID): PlayerModel? = transaction {
        PlayerTable.selectAll()
            .where { PlayerTable.id eq playerId }
            .mapNotNull { row -> PlayerModel(row[PlayerTable.id].value) }
            .singleOrNull()
    }

    fun deletePlayer(playerId: UUID): Boolean = transaction {
        val deletedRows = PlayerTable.deleteWhere { PlayerTable.id eq  playerId }
        deletedRows > 0
    }
}
