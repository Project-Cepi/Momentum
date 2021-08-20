package world.cepi.momentum.ability

import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerStartSneakingEvent
import net.minestom.server.event.player.PlayerStopSneakingEvent
import world.cepi.kstom.event.listenOnly
import world.cepi.momentum.cooldown.PredicateCooldown
import java.util.*

object SuperJump : MovementAbility() {
    private val sneakingTime: Object2LongMap<UUID> = Object2LongOpenHashMap()

    override val description: String = """
        Holding shift whilst on the ground will "charge" the super jump. When you release
        shift you will be launched into the air a certain amount dependent on how long you
        were shifting for. Moving whilst charging the super jump will cancel the jump.
    """.trimIndent()

    override val cooldown = PredicateCooldown(Player::isOnGround)

    override fun initialise() {
        node.listenOnly(::startSneakingEvent)
        node.listenOnly(::stopSneakingEvent)
    }

    private fun performSuperJump(player: Player) {
        sneakingTime.removeLong(player.uuid).let { power ->
            this.cooldown.runIfExpired(player) { player ->
                val vector = getMultiplier(System.currentTimeMillis() - power)
                player.velocity = vector
            }
        }
    }

    /**
     * A function that, given a wait time in seconds, will return the vector for a
     * jump. This essentially uses the number of seconds waited as the new vector with a
     * set limit to prevent jumping too high.
     *
     * @param milliseconds the amount of seconds the jump has been charging
     * @return the vector for the jump
     */
    private fun getMultiplier(milliseconds: Long): Vec {
        return Vec(0.0, (milliseconds / 1000.0).coerceAtMost(10.0), 0.0)
    }

    private fun startSneakingEvent(event: PlayerStartSneakingEvent) = with(event) {
        sneakingTime[player.uuid] = System.currentTimeMillis()
    }

    private fun stopSneakingEvent(event: PlayerStopSneakingEvent) = with(event) {
        performSuperJump(player)
    }

    override fun remove(player: Player) {
        sneakingTime.removeLong(player.uuid)
    }
}