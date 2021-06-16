package world.cepi.momentum.cooldown

import net.minestom.server.entity.Player

/**
 * A cooldown for abilities.
 */
sealed interface Cooldown {
    companion object {
        /**
         * Gets the default cooldown.
         *
         * @return the default cooldown
         */
        fun default(): Cooldown = NoOpCooldown
    }

    /**
     * Checks if the action can run (i.e. the cooldown has expired).
     *
     * @param player the player to check for
     */
    fun canRun(player: Player): Boolean

    /**
     * A callback indicating that the ability has been used and the cooldown should be reset.
     *
     * @param player the player for whom the action was run for
     */
    fun onRun(player: Player) {
        // default to doing nothing as most cooldown implementations don't care
    }

    /**
     * Runs the provided code for the given player if the cooldown has expired, triggering the [onRun] method if needed.
     *
     * @param player the player
     * @param action the action that will be run
     */
    fun runIfExpired(player: Player, action: (Player) -> Unit) {
        if (this.canRun(player)) {
            action.invoke(player)
            this.onRun(player)
        }
    }
}