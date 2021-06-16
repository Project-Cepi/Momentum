package world.cepi.momentum.ability

import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import world.cepi.momentum.cooldown.Cooldown
import java.util.*

/**
 * Abstract framework for movement abilities.
 */
sealed class MovementAbility {

    /**
     * Gets the name of this movement ability.
     */
    open val name: String
        get() = this.javaClass.simpleName

    val players = HashSet<UUID>()

    val node = EventNode.type("${this.javaClass.simpleName}-node", EventFilter.PLAYER) { _, obj ->
        players.contains(obj.uuid)
    }

    /**
     * Gets the description of this movement ability.
     */
    open val description: String
        get() = ""

    /**
     * The cooldown for this movement ability.
     */
    open val cooldown: Cooldown = Cooldown.default()

    /**
     * Called when the movement ability is initialised.
     */
    open fun initialise() { }

    /**
     * Called when a player sets their movement ability to this movement ability.
     *
     * @param player the player
     */
    open fun apply(player: Player) { }

    /**
     * Called when a player no longer uses this movement ability.
     *
     * @param player the player
     */
    open fun remove(player: Player) { }
}