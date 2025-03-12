package pet.lily.generators.database.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.Location
import pet.lily.generators.database.serialization.LocationSerializer
import java.util.UUID

@Serializable
data class GeneratorModel(
    @Contextual val id: UUID,
    val type: String,
    @Serializable(with = LocationSerializer::class) val location: Location,
    @Contextual val playerId: UUID
)
