package pet.lily.generators.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import pet.lily.generators.database.tables.Generators

class Generator(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Generator>(Generators) {
        fun findByPosition(x: Int, y: Int, z: Int): Generator? = transaction {
            Generator.find {
                (Generators.x eq x) and
                        (Generators.y eq y) and
                        (Generators.z eq z)
            }.firstOrNull()
        }
    }

    var type by Generators.type
    var x by Generators.x
    var y by Generators.y
    var z by Generators.z
    var player by Player referencedOn Generators.player
}
