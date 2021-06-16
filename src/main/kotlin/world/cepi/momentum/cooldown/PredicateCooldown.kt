package world.cepi.momentum.cooldown

import net.minestom.server.entity.Player

/**
 * A cooldown that provides a simple wrapper around a check action.
 */
class PredicateCooldown(private val predicate: (Player) -> Boolean): Cooldown {
    override fun canRun(player : Player) = predicate.invoke(player)
}