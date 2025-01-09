package pet.lily.generators.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object GeneratorTable : IntIdTable("generators") {
    val type = varchar("type", 30)
    val x = integer("x")
    val y = integer("y")
    val z = integer("z")
    val player = reference("player_id", PlayerTable)

    init {
        index(true, player, type, x, y, z)
    }
}