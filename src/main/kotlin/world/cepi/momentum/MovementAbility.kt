package world.cepi.momentum

import net.minestom.server.entity.Player

/**
 * Abstract framework for movement abilities.
 */
abstract class MovementAbility {

    /**
     * Gets the name of this movement ability.
     */
    open val name: String
        get() = this.javaClass.simpleName

    /**
     * Gets the description of this movement ability.
     */
    open val description: String
        get() = ""

    /**
     * Called when the movement ability is initialised.
     */
    open fun initialise() { }

    /**
     * Called when a player sets their movement ability to this movement ability.
     * @param player the player
     */
    abstract fun apply(player: Player)

    /**
     * Called when a player no longer uses this movement ability.
     * @param player the player
     */
    abstract fun remove(player: Player)
}