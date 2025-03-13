package pet.lily.generators.database.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object GeneratorTable : UUIDTable("generators") {
    val type = varchar("type", 32)
    val world = varchar("world", 64)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val player = reference("player_id", PlayerTable, onDelete = ReferenceOption.CASCADE).index()

    init {
        uniqueIndex("unique_location", world, x, y, z)
    }
}
