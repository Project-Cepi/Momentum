package world.cepi.momentum.cooldown

import net.minestom.server.entity.Player

/**
 * A cooldown that combines multiple other cooldown instances.
 *
 * @param cooldowns the cooldown instances to combine
 */
class CombinedCooldown(private vararg val cooldowns: Cooldown): Cooldown {
    override fun canRun(player: Player) = this.cooldowns.all { it.canRun(player) }
}