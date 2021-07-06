package world.cepi.momentum.ability

import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerStartSneakingEvent
import world.cepi.kstom.event.listenOnly
import world.cepi.momentum.cooldown.PredicateCooldown

object JumpSmash : MovementAbility() {

    override val cooldown = PredicateCooldown(Player::isOnGround)

    override fun initialise() {
        node.listenOnly(::run)
    }

    private fun run(event: PlayerStartSneakingEvent) = with(event) {

        if (!cooldown.canRun(player)) return

        player.isFlying = false
        val vector = player.position.direction.multiply(5)
        vector.y = -10.0
        player.velocity = vector

    }
}