package pet.lily.generators.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

object MiniMessageUtils {

    private val miniMessage = MiniMessage.builder().build()

    /**
     * Parses a string into a MiniMessage [Component].
     *
     * @param tagResolvers Optional vararg of [TagResolver] for dynamic placeholders.
     * @return A [Component] parsed from the string.
     */
    fun String.asComponent(vararg tagResolvers: TagResolver = emptyArray()): Component {
        val miniMessage = MiniMessage.miniMessage()
        return miniMessage.deserialize(this, *tagResolvers)
    }

    /**
     * Serializes a [Component] into a MiniMessage string.
     */
    fun Component.asString(): String = miniMessage.serialize(this)
}
