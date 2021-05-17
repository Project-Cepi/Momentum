package world.cepi.momentum.ability

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.EventCallback
import net.minestom.server.event.player.PlayerStartFlyingEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.Vector
import net.minestom.server.utils.time.TimeUnit
import world.cepi.kstom.addEventCallback
import world.cepi.momentum.MovementAbility
import java.util.*

object RockPillar : MovementAbility(), EventCallback<PlayerStartFlyingEvent> {

    override val description: String = """
        By double tapping the space bar (the default way to toggle flying in vanilla), a
        pillar of rock will appear beneath the player, propelling them upwards. Shortly
        afterwards, the pillar will disappear.
    """.trimIndent()

    override fun apply(player: Player) {
        player.isAllowFlying = true
        player.addEventCallback(PlayerStartFlyingEvent::class.java, this)
    }

    override fun remove(player: Player) {
        player.isAllowFlying = false
        player.removeEventCallback(PlayerStartFlyingEvent::class.java, this)
    }

    override fun run(event: PlayerStartFlyingEvent) {
        // cancel the flight first
        event.player.isFlying = false

        // now get the block to start the pillar at
        val blockPosition = getBlockAboveGround(event.player)

        if (blockPosition != null) {
            // start off by throwing the player up in the air
            event.player.velocity = Vector(0.0, 12.0, 0.0)

            // now build the pillar!
            val pillarBlocks = HashSet<BlockPosition>()

            MinecraftServer.getSchedulerManager().buildTask {
                // double check the instance still exists
                if (event.player.instance == null) {
                    return@buildTask
                }

                // loop and place each block in the given location
                for (i in 1..5) {
                    if (event.player.instance!!.getBlock(blockPosition).isAir) {
                        pillarBlocks.add(blockPosition.clone())
                        event.player.instance!!.setBlock(blockPosition, Block.STONE)
                        blockPosition.add(0, 1, 0)
                    } else {
                        // break if we've hit any non air block so we don't destroy any existing structures
                        break
                    }
                }

                // schedule a task to remove the pillar later
                MinecraftServer.getSchedulerManager().buildTask {
                    // double check the instance still exists
                    if (event.player.instance != null) {
                        // destroy each placed block
                        pillarBlocks.forEach {
                            event.player.instance!!.setBlock(it, Block.AIR)
                        }
                    }
                }.delay(5, TimeUnit.SECOND).schedule()
            }.delay(500, TimeUnit.MILLISECOND).schedule()
        }
    }

    /**
     * Gets the block below the player that is just above the ground, with a given limit.
     * @param player the player
     * @return the block position of the block just above the ground, if any
     */
    private fun getBlockAboveGround(player: Player): BlockPosition? {
        // just return null if they're not in an instance
        if (player.instance == null) {
            return null
        }

        var iteration = 0
        val lastBlockPosition = player.position.toBlockPosition()

        while (iteration < 5) {
            if (!player.instance!!.getBlock(lastBlockPosition.subtract(0, 1, 0)).isAir) {
                return lastBlockPosition.add(0, 1, 0)
            }

            iteration++
        }

        return null
    }
}