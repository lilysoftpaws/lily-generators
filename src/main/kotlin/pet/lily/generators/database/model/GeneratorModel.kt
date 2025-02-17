package pet.lily.generators.database.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class GeneratorModel(
    @Contextual val id: UUID,
    val type: String,
    val location: Location,
    @Contextual val playerId: UUID
)

@Serializable
data class Location(val x: Int, val y: Int, val z: Int)
