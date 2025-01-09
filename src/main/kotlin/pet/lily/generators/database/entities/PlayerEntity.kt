package pet.lily.generators.database.entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pet.lily.generators.database.tables.GeneratorTable
import pet.lily.generators.database.tables.PlayerTable
import java.util.UUID


class PlayerEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlayerEntity>(PlayerTable) {

    }

    val generators by GeneratorEntity referrersOn GeneratorTable.player
}