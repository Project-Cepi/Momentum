package world.cepi.momentum.ability

import net.minestom.server.entity.Player
import net.minestom.server.event.EventCallback
import net.minestom.server.event.player.PlayerStartFlyingEvent
import world.cepi.kstom.addEventCallback
import world.cepi.momentum.MovementAbility

/**
 * By double tapping the space bar (the default way to toggle flying in vanilla), the
 * player will get a brief boost to their momentum, as if they performed a second jump
 * in the air.
 */
class DoubleJump : MovementAbility(), EventCallback<PlayerStartFlyingEvent> {

    override val description: String = """
        By double tapping the space bar (the default way to toggle flying in vanilla), the
        player will get a brief boost to their momentum, as if they performed a second jump
        in the air.
    """.trimIndent()

    override fun apply(player: Player) {
        player.isAllowFlying = true
        player.addEventCallback(::run)
    }

    override fun remove(player: Player) {
        player.isAllowFlying = false
        player.removeEventCallback(PlayerStartFlyingEvent::class.java, this)
    }

    override fun run(event: PlayerStartFlyingEvent) {
        // cancel the flying first
        event.player.isFlying = false
        event.player.refreshFlying(false)

        // apply a jump to the player
        val vector = event.player.position.direction.multiply(5)
        vector.y = 10.0

        event.player.velocity = vector
    }
}