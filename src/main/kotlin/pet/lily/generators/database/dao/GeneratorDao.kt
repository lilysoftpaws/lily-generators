package pet.lily.generators.database.dao

import org.bukkit.Bukkit
import org.bukkit.Location
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import pet.lily.generators.database.model.GeneratorModel
import pet.lily.generators.database.tables.GeneratorTable
import java.util.UUID

object GeneratorDao {
    fun createGenerator(type: String, location: Location, playerId: UUID): GeneratorModel = transaction {
        val id = GeneratorTable.insertAndGetId {
            it[this.type] = type
            it[world] = location.world?.name ?: "world"
            it[x] = location.x
            it[y] = location.y
            it[z] = location.z
            it[player] = playerId
        }
        GeneratorModel(id.value, type, location, playerId)
    }

    fun getGeneratorByLocation(location: Location): GeneratorModel? = transaction {
        GeneratorTable.selectAll().where {
            (GeneratorTable.world eq (location.world?.name ?: "world")) and
                    (GeneratorTable.x eq location.x) and
                    (GeneratorTable.y eq location.y) and
                    (GeneratorTable.z eq location.z)
        }.mapNotNull { it.toGeneratorModel() }.singleOrNull()
    }

    fun getGeneratorsByPlayer(playerId: UUID): List<GeneratorModel> = transaction {
        GeneratorTable.selectAll().where { GeneratorTable.player eq playerId }
            .map { it.toGeneratorModel() }
    }

    fun deleteGenerator(generatorId: UUID): Boolean = transaction {
        GeneratorTable.deleteWhere { GeneratorTable.id eq generatorId } > 0
    }

    private fun ResultRow.toGeneratorModel(): GeneratorModel {
        val world = Bukkit.getWorld(this[GeneratorTable.world]) ?: Bukkit.getWorlds().firstOrNull()
        return GeneratorModel(
            id = this[GeneratorTable.id].value,
            type = this[GeneratorTable.type],
            location = Location(world, this[GeneratorTable.x], this[GeneratorTable.y], this[GeneratorTable.z]),
            playerId = this[GeneratorTable.player].value
        )
    }
}
