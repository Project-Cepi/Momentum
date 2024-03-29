package world.cepi.momentum.ability

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerStartFlyingEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.time.TimeUnit
import world.cepi.kstom.event.listenOnly
import world.cepi.kstom.util.toBlockPosition

object RockPillar : MovementAbility() {

    override val description: String = """
        By double tapping the space bar (the default way to toggle flying in vanilla), a
        pillar of rock will appear beneath the player, propelling them upwards. Shortly
        afterwards, the pillar will disappear.
    """.trimIndent()

    override fun initialise() {
        node.listenOnly(::run)
    }

    override fun apply(player: Player) {
        player.isAllowFlying = true
    }

    override fun remove(player: Player) {
        player.isAllowFlying = player.isCreative || player.gameMode == GameMode.SPECTATOR
    }

    fun run(event: PlayerStartFlyingEvent) = with(event) {

        if (!cooldown.canRun(player)) return

        // cancel the flight first
        player.isFlying = false

        // now get the block to start the pillar at
        val blockPosition = getBlockAboveGround(player)

        if (blockPosition != null) {
            // start off by throwing the player up in the air
            player.velocity = Vec(0.0, 16.0, 0.0)

            // now build the pillar!
            val pillarBlocks = HashSet<Vec>()

            MinecraftServer.getSchedulerManager().buildTask {
                // double check the instance still exists
                if (player.instance == null) {
                    return@buildTask
                }

                // loop and place each block in the given location
                for (i in 1..5) {
                    if (player.instance!!.getBlock(blockPosition).isAir) {
                        pillarBlocks.add(blockPosition)
                        player.instance!!.setBlock(blockPosition, Block.STONE)
                        blockPosition.add(0.0, 1.0, 0.0)
                    } else {
                        // break if we've hit any non air block so we don't destroy any existing structures
                        break
                    }
                }

                // schedule a task to remove the pillar later
                MinecraftServer.getSchedulerManager().buildTask {
                    // double check the instance still exists
                    if (player.instance != null) {
                        // destroy each placed block
                        pillarBlocks.forEach {
                            player.instance!!.setBlock(it, Block.AIR)
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
    private fun getBlockAboveGround(player: Player, limit: Int = 5): Vec? {
        // just return null if they're not in an instance
        if (player.instance == null) {
            return null
        }

        var iteration = 0
        val lastBlockPosition = player.position.asVec().toBlockPosition().asVec()

        while (iteration < limit) {
            if (!player.instance!!.getBlock(lastBlockPosition.sub(.0, 1.0, 0.0)).isAir) {
                return lastBlockPosition.add(.0, 1.0, .0)
            }

            iteration++
        }

        return null
    }
}