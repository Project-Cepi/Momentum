package world.cepi.momentum.ability

import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.EventCallback
import net.minestom.server.event.player.PlayerStartFlyingEvent
import world.cepi.kstom.event.listenOnly
import world.cepi.momentum.cooldown.Cooldown
import world.cepi.momentum.cooldown.PredicateCooldown

/**
 * By double tapping the space bar (the default way to toggle flying in vanilla), the
 * player will get a brief boost to their momentum, as if they performed a second jump
 * in the air.
 */
object DoubleJump : MovementAbility(), EventCallback<PlayerStartFlyingEvent> {

    override val description: String = """
        By double tapping the space bar (the default way to toggle flying in vanilla), the
        player will get a brief boost to their momentum, as if they performed a second jump
        in the air.
    """.trimIndent()

    override val cooldown: Cooldown = PredicateCooldown(Player::isOnGround)

    override fun initialise() {
        node.listenOnly(::run)
    }

    override fun apply(player: Player) {
        player.isAllowFlying = true
    }

    override fun remove(player: Player) {
        player.isAllowFlying = player.isCreative || player.gameMode == GameMode.SPECTATOR
    }

    override fun run(event: PlayerStartFlyingEvent) = with(event) {

        // cancel the flying first
        player.isFlying = false
        player.refreshFlying(false)

        // apply a jump to the player
        val vector = event.player.position.direction().mul(12.0).withY(10.0)

        player.velocity = vector

    }
}