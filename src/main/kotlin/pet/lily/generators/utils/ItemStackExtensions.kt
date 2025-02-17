package pet.lily.generators.utils

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun <T : Any> ItemStack.getPersistentData(key: NamespacedKey, type: PersistentDataType<*, T>): T? {
    return itemMeta?.persistentDataContainer?.get(key, type)
}
fun <T : Any> ItemStack.setPersistentData(key: NamespacedKey, type: PersistentDataType<*, T>, value: T) {
    itemMeta?.persistentDataContainer?.set(key, type, value)
}