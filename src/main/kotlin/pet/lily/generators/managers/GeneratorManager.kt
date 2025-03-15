package pet.lily.generators.managers

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import pet.lily.generators.Generators
import pet.lily.generators.database.dao.GeneratorDao
import pet.lily.generators.database.dao.PlayerDao
import pet.lily.generators.localization.sendLocalizedMessage
import pet.lily.generators.plugin
import pet.lily.generators.registry.ItemRegistry
import pet.lily.generators.utils.NumberUtils.formatCurrency
import pet.lily.generators.utils.ItemStackUtils.getPersistentData

object GeneratorManager : IManager, Listener {
    val generatorTypeKey = NamespacedKey(plugin, "generators.generator-type")

    override fun initialize(plugin: Generators) {
        plugin.server.pluginManager.registerEvents(this, plugin)
        startDropSpawningTask()
    }

    @EventHandler
    fun BlockPlaceEvent.onBlockPlace() {
        val generatorType = itemInHand.getPersistentData(generatorTypeKey, PersistentDataType.STRING) ?: return
        val generatorData = ItemRegistry.processedGenerators[generatorType] ?: return

        // check generator slots
        val playerSlots = PlayerDao.getPlayerSlots(player.uniqueId)
        val playerGenerators = GeneratorDao.getGeneratorsByPlayer(player.uniqueId).size

        if (playerGenerators >= playerSlots) {
            player.sendLocalizedMessage(
                key = "generators.place.limit-reached",
                placeholders = mapOf("slots" to playerSlots)
            )
            isCancelled = true
            return
        }

        // register the generator in the database
        GeneratorDao.createGenerator(generatorType, blockPlaced.location, player.uniqueId)

        // play sound and send success message
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, 2f, 2f)
        player.sendLocalizedMessage(
            key = "generators.place.success",
            placeholders = mapOf(
                "display-name" to generatorData.displayName,
                "max-slots" to playerSlots,
                "slots" to playerGenerators + 1
            )
        )
    }

    @EventHandler
    fun BlockBreakEvent.onBlockBreak() {
        GeneratorDao.getGeneratorByLocation(block.location)?.let {
            player.permissionValue("generators.break").let { canBreak ->
                if (canBreak.toBoolean() != true) {
                    isCancelled = true
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun PlayerInteractEvent.onLeftClick() {
        if (action != Action.LEFT_CLICK_BLOCK || hand != EquipmentSlot.HAND) return

        val block = clickedBlock ?: return
        val generatorData = GeneratorDao.getGeneratorByLocation(block.location) ?: return
        val generatorType = ItemRegistry.processedGenerators[generatorData.type] ?: return

        // ensure the player owns the generator
        if (generatorData.playerId != player.uniqueId) return

        // check generator slots
        val playerSlots = PlayerDao.getPlayerSlots(player.uniqueId)
        val playerGenerators = GeneratorDao.getGeneratorsByPlayer(player.uniqueId).size

        // remove the generator and update the database
        isCancelled = true
        block.type = Material.AIR
        GeneratorDao.deleteGenerator(generatorData.id)

        // return the generator to the player's inventory
        player.inventory.addItem(generatorType.itemTemplate)

        // play sound and send success message
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, 2f, 2f)
        player.sendLocalizedMessage(
            key = "generators.pickup.success",
            placeholders = mapOf(
                "display-name" to generatorType.displayName,
                "max-slots" to playerSlots,
                "slots" to playerGenerators - 1
            )
        )
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun PlayerInteractEvent.onShiftRightClick() {
        if (action != Action.RIGHT_CLICK_BLOCK || !player.isSneaking || hand != EquipmentSlot.HAND) return

        val block = clickedBlock ?: return
        val generatorData = GeneratorDao.getGeneratorByLocation(block.location) ?: return
        val currentGenerator = ItemRegistry.processedGenerators[generatorData.type] ?: return

        // ensure the player owns the generator
        if (generatorData.playerId != player.uniqueId) return

        // check if there's a next generator to upgrade to
        val nextGenerator = ItemRegistry.processedGenerators.values
            .sortedBy { it.price }
            .firstOrNull { it.price > currentGenerator.price } ?: return

        val upgradeCost = nextGenerator.price - currentGenerator.price

        // check if the player has enough money
        val playerBalance = plugin.economy.getBalance(player)
        if (playerBalance < upgradeCost) {
            player.sendLocalizedMessage(
                key = "generators.upgrade.insufficient-funds",
                placeholders = mapOf("cost" to (upgradeCost - playerBalance).formatCurrency())
            )
            return
        }

        // deduct the cost and upgrade the generator
        plugin.economy.withdrawPlayer(player, upgradeCost)
        GeneratorDao.deleteGenerator(generatorData.id)
        GeneratorDao.createGenerator(nextGenerator.id, block.location, player.uniqueId)

        block.type = nextGenerator.material
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        player.sendLocalizedMessage(
            key = "generators.upgrade.success",
            placeholders = mapOf("display-name" to nextGenerator.displayName)
        )
    }

    private fun startDropSpawningTask() {
        val tickRate = plugin.configuration.main.generatorTickRate.toLong()

        object : BukkitRunnable() {
            override fun run() {
                val loadedChunks = mutableSetOf<Chunk>()

                Bukkit.getOnlinePlayers().forEach { player ->
                    val generators = GeneratorDao.getGeneratorsByPlayer(player.uniqueId)

                    generators.forEach { generator ->
                        val location = generator.location
                        val chunk = location.chunk

                        if (!loadedChunks.contains(chunk)) {
                            if (!chunk.isLoaded) return
                            loadedChunks.add(chunk)
                        }

                        val generatorType = ItemRegistry.processedGenerators[generator.type] ?: return
                        val drop = generatorType.drop

                        location.world?.dropItemNaturally(
                            location.add(0.5, 1.0, 0.5),
                            drop.itemTemplate
                        )
                    }
                }
            }
        }.runTaskTimer(plugin, tickRate, tickRate)
    }
}