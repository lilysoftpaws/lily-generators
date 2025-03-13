package pet.lily.generators.database.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import pet.lily.generators.database.model.PlayerModel
import pet.lily.generators.database.tables.PlayerTable
import java.util.UUID

object PlayerDao {
    fun createPlayer(playerId: UUID, locale: String): PlayerModel = transaction {
        PlayerTable.insert {
            it[id] = playerId
            it[PlayerTable.locale] = locale
        }
        PlayerModel(playerId, locale)
    }

    fun getPlayerById(playerId: UUID): PlayerModel? = transaction {
        PlayerTable.selectAll().where { PlayerTable.id eq playerId }
            .map { PlayerModel(it[PlayerTable.id].value, it[PlayerTable.locale]) }
            .singleOrNull()
    }

    fun updatePlayerLocale(playerId: UUID, locale: String): Boolean = transaction {
        PlayerTable.update({ PlayerTable.id eq playerId }) {
            it[PlayerTable.locale] = locale
        } > 0
    }
}
