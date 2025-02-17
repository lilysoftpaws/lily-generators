package pet.lily.generators.database.dao

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pet.lily.generators.database.model.GeneratorModel
import pet.lily.generators.database.model.Location
import pet.lily.generators.database.tables.GeneratorTable
import java.util.UUID

object GeneratorDao {
    fun createGenerator(type: String, location: Location, playerId: UUID): GeneratorModel = transaction {
        val id = GeneratorTable
            .insertAndGetId {
                it[GeneratorTable.type] = type
                it[GeneratorTable.x] = location.x
                it[GeneratorTable.y] = location.y
                it[GeneratorTable.z] = location.z
                it[GeneratorTable.player] = playerId
            }
        GeneratorModel(id.value, type, location, playerId)
    }

    fun getGeneratorByLocation(location: Location): GeneratorModel? = transaction {
        GeneratorTable
            .selectAll()
            .where {
                GeneratorTable.x eq location.x and
                        (GeneratorTable.y eq location.y) and
                        (GeneratorTable.z eq location.z)
            }
            .mapNotNull {
                GeneratorModel(
                    it[GeneratorTable.id].value,
                    it[GeneratorTable.type],
                    Location(it[GeneratorTable.x], it[GeneratorTable.y], it[GeneratorTable.z]),
                    it[GeneratorTable.player].value
                )
            }
            .singleOrNull()
    }

    fun getGeneratorsByPlayer(playerId: UUID): List<GeneratorModel> = transaction {
        GeneratorTable
            .selectAll()
            .where { GeneratorTable.player eq playerId }
            .map {
                GeneratorModel(
                    it[GeneratorTable.id].value,
                    it[GeneratorTable.type],
                    Location(it[GeneratorTable.x], it[GeneratorTable.y], it[GeneratorTable.z]),
                    it[GeneratorTable.player].value
                )
            }
    }

    fun getAllGenerators(): List<GeneratorModel> = transaction {
        GeneratorTable
            .selectAll()
            .map {
                GeneratorModel(
                    it[GeneratorTable.id].value,
                    it[GeneratorTable.type],
                    Location(it[GeneratorTable.x], it[GeneratorTable.y], it[GeneratorTable.z]),
                    it[GeneratorTable.player].value
                )
            }
    }

    fun deleteGenerator(generatorId: UUID): Boolean = transaction {
        val deletedRows = GeneratorTable.deleteWhere { GeneratorTable.id eq generatorId }
        deletedRows > 0
    }
}