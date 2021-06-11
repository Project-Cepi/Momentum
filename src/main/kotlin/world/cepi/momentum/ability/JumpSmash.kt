package world.cepi.momentum.ability

import net.minestom.server.event.player.PlayerStartSneakingEvent
import world.cepi.kstom.event.listenOnly

object JumpSmash : MovementAbility() {

    override fun initialise() {
        node.listenOnly(::run)
    }

    fun run(event: PlayerStartSneakingEvent) {
        if (event.player.isFlying || !event.player.isOnGround) {
            event.player.isFlying = false
            val vector = event.player.position.direction.multiply(5)
            vector.y = -10.0
            event.player.velocity = vector
        }
    }
}