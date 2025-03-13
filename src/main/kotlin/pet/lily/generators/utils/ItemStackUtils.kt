package pet.lily.generators.utils

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object ItemStackUtils {

    /**
     * Retrieves persistent data from an [ItemStack].
     *
     * @param key The [NamespacedKey] for the data.
     * @param type The [PersistentDataType] of the data.
     * @return The value of the persistent data, or null if not found.
     */
    fun <T : Any> ItemStack.getPersistentData(key: NamespacedKey, type: PersistentDataType<*, T>): T? {
        return itemMeta?.persistentDataContainer?.get(key, type)
    }

    /**
     * Sets persistent data on an [ItemStack].
     *
     * @param key The [NamespacedKey] for the data.
     * @param type The [PersistentDataType] of the data.
     * @param value The value to set.
     */
    fun <T : Any> ItemStack.setPersistentData(key: NamespacedKey, type: PersistentDataType<*, T>, value: T) {
        itemMeta?.persistentDataContainer?.set(key, type, value)
    }
}
