package pet.lily.generators.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

object MiniMessageUtils {

    private val miniMessage = MiniMessage.builder().build()

    /**
     * Parses a string into a MiniMessage [Component].
     */
    fun String.asComponent(): Component = miniMessage.deserialize(this)

    /**
     * Serializes a [Component] into a MiniMessage string.
     */
    fun Component.asString(): String = miniMessage.serialize(this)
}
