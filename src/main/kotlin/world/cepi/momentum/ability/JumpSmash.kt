package world.cepi.momentum.ability

import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.EventCallback
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerStartFlyingEvent
import net.minestom.server.event.player.PlayerStartSneakingEvent
import world.cepi.kstom.event.listenOnly
import world.cepi.momentum.cooldown.PredicateCooldown

object JumpSmash : MovementAbility(), EventCallback<PlayerStartFlyingEvent> {

    override val cooldown = PredicateCooldown(Player::isOnGround)

    override fun initialise() {
        node.listenOnly(::run)
        node.listenOnly<PlayerMoveEvent> {
            if (player.isOnGround) player.isAllowFlying = true
        }
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
        player.isAllowFlying = false

        val vector = player.position.direction().mul(5.0).withY(-10.0)
        player.velocity = vector

    }
}