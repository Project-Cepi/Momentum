package world.cepi.momentum.cooldown

import net.minestom.server.entity.Player

/**
 * A cooldown that will always allow the ability to be ran.
 */
object NoOpCooldown : Cooldown {
    override fun canRun(player: Player) = true
}