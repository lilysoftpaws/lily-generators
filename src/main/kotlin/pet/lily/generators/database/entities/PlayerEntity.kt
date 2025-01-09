package pet.lily.generators.database.entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pet.lily.generators.database.tables.Generators
import pet.lily.generators.database.tables.Players
import java.util.UUID


class Player(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Player>(Players) {

    }

    val generators by Generator referrersOn Generators.player
}