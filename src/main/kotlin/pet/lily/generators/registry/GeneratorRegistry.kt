package pet.lily.generators.registry

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import pet.lily.generators.managers.GeneratorManager
import pet.lily.generators.plugin
import pet.lily.generators.utils.MiniMessageUtils.asComponent

data class ProcessedDrop(
    val id: String,
    val material: Material,
    val displayName: Component?,
    val lore: List<Component>?,
    val value: Double,
    val itemTemplate: ItemStack
)

data class ProcessedGenerator(
    val id: String,
    val material: Material,
    val displayName: Component?,
    val lore: List<Component>?,
    val price: Double,
    val drop: ProcessedDrop,
    val itemTemplate: ItemStack
)

object GeneratorRegistry {
    private fun buildItemTemplate(material: Material, displayName: Component?, lore: List<Component>?): ItemStack =
        ItemStack(material).apply {
            itemMeta = itemMeta.apply {
                itemName(displayName)
                lore(lore)
            }
        }

    val processedDrops: Map<String, ProcessedDrop> = plugin.configuration.generators.drops.mapNotNull { (id, dropConfig) ->
        val material = Material.matchMaterial(dropConfig.material)
        if (material == null) {
            plugin.logger.warning { "invalid material for drop '$id': ${dropConfig.material}" }
            return@mapNotNull null
        }

        val displayName = dropConfig.displayName.asComponent()
        val lore = dropConfig.lore.map { it.asComponent() }
        val template = buildItemTemplate(material, displayName, lore)

        id to ProcessedDrop(
            id = id,
            material = material,
            displayName = displayName,
            lore = lore,
            value = dropConfig.value,
            itemTemplate = template
        )
    }.toMap()

    val processedGenerators: Map<String, ProcessedGenerator> = plugin.configuration.generators.types.mapNotNull { (id, typeConfig) ->
        val material = Material.matchMaterial(typeConfig.material)
        if (material == null) {
            plugin.logger.warning { "invalid material for generator '$id': ${typeConfig.material}" }
            return@mapNotNull null
        }

        val displayName = typeConfig.displayName.asComponent()
        val lore = typeConfig.lore.map { it.asComponent() }
        val drop = processedDrops[typeConfig.drop]

        if (drop == null) {
            plugin.logger.warning { "generator '$id' references unknown drop '${typeConfig.drop}'" }
            return@mapNotNull null
        }

        val template = buildItemTemplate(material, displayName, lore)
        template.editMeta {
            it.persistentDataContainer.set(GeneratorManager.generatorTypeKey, PersistentDataType.STRING, id)
        }

        id to ProcessedGenerator(
            id = id,
            material = material,
            displayName = displayName,
            lore = lore,
            price = drop.value,
            drop = drop,
            itemTemplate = template
        )
    }.toMap()
}
