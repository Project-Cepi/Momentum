package world.cepi.momentum.ability

import net.minestom.server.event.player.PlayerStartSneakingEvent
import world.cepi.kstom.event.listenOnly
import world.cepi.momentum.cooldown.PredicateCooldown

object JumpSmash : MovementAbility() {

    override val cooldown = PredicateCooldown() {player -> !player.isOnGround }

    override fun initialise() {
        node.listenOnly(::run)
    }

    private fun run(event: PlayerStartSneakingEvent) {
        this.cooldown.runIfExpired(event.player) { player ->
            player.isFlying = false
            val vector = player.position.direction.multiply(5)
            vector.y = -10.0
            player.velocity = vector
        }
    }
}