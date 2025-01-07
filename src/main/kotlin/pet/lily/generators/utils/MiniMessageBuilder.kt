package pet.lily.generators.utils

import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * Returns a custom MiniMessage builder for global parsing.
 *
 * @return A new instance of MiniMessage.Builder.
 */
fun getMiniMessageBuilder(): MiniMessage.Builder {
    return MiniMessage.builder()
}