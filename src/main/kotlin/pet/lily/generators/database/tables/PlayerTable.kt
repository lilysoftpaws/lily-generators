package pet.lily.generators.database.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object Players : UUIDTable("players") {
    val lastLogin = long("last_login")
}
