package world.cepi.momentum.ability

import net.minestom.server.entity.Player
import net.minestom.server.event.EventCallback
import net.minestom.server.event.player.PlayerStartFlyingEvent
import net.minestom.server.event.player.PlayerStartSneakingEvent
import world.cepi.momentum.MovementAbility

object JumpSmash : MovementAbility(), EventCallback<PlayerStartSneakingEvent> {
    override fun apply(player: Player) {
        player.addEventCallback(PlayerStartSneakingEvent::class.java, ::run)
    }

    override fun remove(player: Player) {
        player.removeEventCallback(PlayerStartSneakingEvent::class.java, this)
    }

    override fun run(event: PlayerStartSneakingEvent) {
        if (event.player.isFlying || !event.player.isOnGround) {
            event.player.isFlying = false
            val vector = event.player.position.direction.multiply(5)
            vector.y = -10.0
            event.player.velocity = vector
        }
    }
}