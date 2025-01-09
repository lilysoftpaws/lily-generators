package pet.lily.generators.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import pet.lily.generators.database.tables.GeneratorTable

class GeneratorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GeneratorEntity>(GeneratorTable) {
        fun findByPosition(x: Int, y: Int, z: Int): GeneratorEntity? = transaction {
            GeneratorEntity.find {
                (GeneratorTable.x eq x) and
                        (GeneratorTable.y eq y) and
                        (GeneratorTable.z eq z)
            }.firstOrNull()
        }
    }

    var type by GeneratorTable.type
    var x by GeneratorTable.x
    var y by GeneratorTable.y
    var z by GeneratorTable.z
    var player by PlayerEntity referencedOn GeneratorTable.player
}
