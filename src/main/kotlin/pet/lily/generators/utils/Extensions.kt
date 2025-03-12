package pet.lily.generators.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

private val miniMessage = MiniMessage.builder().build()

fun String.mm(): Component = miniMessage.deserialize(this)
fun Component.mm() = miniMessage.serialize(this)
