package world.cepi.momentum.ability

import net.kyori.adventure.sound.Sound
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerStartFlyingEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.sound.SoundEvent
import net.minestom.server.tag.Tag
import net.minestom.server.utils.time.TimeUnit
import world.cepi.energy.energy
import world.cepi.kstom.event.listenOnly
import world.cepi.kstom.util.toBlockPosition
import world.cepi.kstom.util.toExactBlockPosition
import world.cepi.kstom.util.viewersAndSelfAsAudience
import world.cepi.particle.Particle
import world.cepi.particle.ParticleType
import world.cepi.particle.data.Color
import world.cepi.particle.data.OffsetAndSpeed
import world.cepi.particle.extra.BlockState
import world.cepi.particle.extra.Dust
import world.cepi.particle.renderer.Renderer
import world.cepi.particle.renderer.render

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

        if (!cooldown.canRun(player)) return@with

        // cancel the flight first
        player.isFlying = false

        // now get the block to start the pillar at
        val blockPosition = getBlockAboveGround(player) ?: return@with

        // generate the position blocks
        val pillarBlocks = (1..5).map { blockPosition.add(0.0, it.toDouble() - 1, 0.0) }

        val rectangle = Renderer.fixedRectangle(blockPosition, pillarBlocks.last().add(1.0, 1.0, 1.0))

        val blockUnder = player.instance!!.getBlock(pillarBlocks.first().sub(0.0, 1.0, 0.0))

        if (blockUnder.isAir || player.energy < 14) {
            rectangle.render(Particle.particle(
                ParticleType.DUST,
                1,
                OffsetAndSpeed(),
                Dust(1f, 0f, 0f, 1f)
            ), player.viewersAndSelfAsAudience)

            player.viewersAndSelfAsAudience.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_DIDGERIDOO, Sound.Source.MASTER, 1f, 0.5f))

            return
        }

        // start off by throwing the player up in the air
        player.velocity = Vec(0.0, 16.0, 0.0)

        player.viewersAndSelfAsAudience.playSound(Sound.sound(SoundEvent.ENTITY_IRON_GOLEM_ATTACK, Sound.Source.MASTER, 2f, 0.5f))

        rectangle.render(Particle.particle(
            ParticleType.BLOCK,
            1,
            OffsetAndSpeed(),
            BlockState(Block.STONE)
        ), player.viewersAndSelfAsAudience)

        MinecraftServer.getSchedulerManager().buildTask {
            // double check the instance still exists
            if (player.instance == null) {
                return@buildTask
            }

            // loop and place each block in the given location
            for (blockPos in pillarBlocks) {
                if (player.instance!!.getBlock(blockPos).isAir) {
                    player.instance!!.setBlock(blockPos, Block.STONE)
                } else {
                    // break if we've hit any non air block so we don't destroy any existing structures
                    break
                }
            }

            player.energy -= 14

            player.viewersAndSelfAsAudience.playSound(Sound.sound(SoundEvent.ENTITY_IRON_GOLEM_DAMAGE, Sound.Source.MASTER, 1f, 0.5f))

            // schedule a task to remove the pillar later
            MinecraftServer.getSchedulerManager().buildTask {
                // double check the instance still exists
                if (player.instance != null) {
                    // destroy each placed block
                    pillarBlocks.forEach {
                        player.instance!!.setBlock(it, Block.AIR)
                    }

                    player.viewersAndSelfAsAudience.playSound(Sound.sound(SoundEvent.ENTITY_IRON_GOLEM_REPAIR, Sound.Source.MASTER, 1f, 0.5f))

                    rectangle.render(Particle.particle(
                        ParticleType.BLOCK,
                        1,
                        OffsetAndSpeed(0f, 0f, 0f, 1f),
                        BlockState(Block.STONE)
                    ), player.viewersAndSelfAsAudience)
                }
            }.delay(4000, TimeUnit.MILLISECOND).schedule()
        }.delay(500, TimeUnit.MILLISECOND).schedule()


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

        repeat(limit) {
            if (player.instance!!.getBlock(player.position.sub(0.0, it.toDouble(), 0.0)).isSolid) {
                return player.position.sub(0.0, it - 1.0, 0.0).asVec().toExactBlockPosition().asVec()
            }
        }

        return null
    }
}