package pet.lily.generators.database.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object GeneratorTable : UUIDTable("generators") {
    val type = varchar("type", 32)
    val x = integer("x")
    val y = integer("y")
    val z = integer("z")
    val player = reference("player_id", PlayerTable, onDelete = ReferenceOption.CASCADE).index()

    init {
        uniqueIndex("unique_location", x, y, z)
    }
}
