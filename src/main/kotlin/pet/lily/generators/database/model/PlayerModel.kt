package pet.lily.generators.database.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PlayerModel(
    @Contextual val id: UUID
)