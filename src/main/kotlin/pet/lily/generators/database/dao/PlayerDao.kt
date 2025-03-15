package pet.lily.generators.database.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import pet.lily.generators.database.model.PlayerModel
import pet.lily.generators.database.tables.PlayerTable
import pet.lily.generators.plugin
import java.util.UUID

object PlayerDao {
    fun createPlayer(playerId: UUID, locale: String, slots: Int): PlayerModel = transaction {
        PlayerTable.insert {
            it[id] = playerId
            it[PlayerTable.locale] = locale
            it[PlayerTable.slots] = slots
        }
        PlayerModel(playerId, locale, slots)
    }

    fun getPlayerById(playerId: UUID): PlayerModel? = transaction {
        PlayerTable.selectAll().where { PlayerTable.id eq playerId }
            .map { PlayerModel(it[PlayerTable.id].value, it[PlayerTable.locale], it[PlayerTable.slots]) }
            .singleOrNull()
    }

    fun updatePlayerLocale(playerId: UUID, locale: String): Boolean = transaction {
        PlayerTable.update({ PlayerTable.id eq playerId }) {
            it[PlayerTable.locale] = locale
        } > 0
    }

    fun getPlayerSlots(playerId: UUID): Int = transaction {
        PlayerTable.selectAll().where { PlayerTable.id eq playerId }
            .map { it[PlayerTable.slots] }
            .singleOrNull() ?: plugin.configuration.main.defaultGeneratorSlots
    }

    fun setPlayerSlots(playerId: UUID, slots: Int): Boolean = transaction {
        PlayerTable.update({ PlayerTable.id eq playerId }) {
            it[PlayerTable.slots] = slots
        } > 0
    }
}
