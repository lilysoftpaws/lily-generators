package pet.lily.generators.database.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object PlayerTable : UUIDTable("players") {
    val locale = varchar("locale", 16)
    val slots = integer("slots")
}