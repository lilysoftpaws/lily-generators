package pet.lily.generators.database.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import org.bukkit.Bukkit
import org.bukkit.Location

object LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Location") {
        element<String>("world")
        element<Double>("x")
        element<Double>("y")
        element<Double>("z")
        element<Float>("yaw")
        element<Float>("pitch")
    }

    override fun serialize(encoder: Encoder, value: Location) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.world?.name ?: "world")
            encodeDoubleElement(descriptor, 1, value.x)
            encodeDoubleElement(descriptor, 2, value.y)
            encodeDoubleElement(descriptor, 3, value.z)
            encodeFloatElement(descriptor, 4, value.yaw)
            encodeFloatElement(descriptor, 5, value.pitch)
        }
    }

    override fun deserialize(decoder: Decoder): Location {
        return decoder.decodeStructure(descriptor) {
            var world = "world"
            var x = 0.0
            var y = 0.0
            var z = 0.0
            var yaw = 0f
            var pitch = 0f

            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> world = decodeStringElement(descriptor, index)
                    1 -> x = decodeDoubleElement(descriptor, index)
                    2 -> y = decodeDoubleElement(descriptor, index)
                    3 -> z = decodeDoubleElement(descriptor, index)
                    4 -> yaw = decodeFloatElement(descriptor, index)
                    5 -> pitch = decodeFloatElement(descriptor, index)
                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> error("Unexpected index: $index")
                }
            }

            Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
        }
    }
}
