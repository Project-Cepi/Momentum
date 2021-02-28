package world.cepi.momentum

import net.minestom.server.entity.Player
import java.util.*

class AbilityManager(
    /** a collection of all currently loaded abilities. */
    vararg val abilities: MovementAbility
) {
    private val playerAbilities = HashMap<UUID, MovementAbility>()

    init {
        abilities.iterator().forEachRemaining {
            it.initialise()
        }
    }

    /**
     * Gets an ability by it's name.
     * @param name the name of the ability
     * @return the ability, or `null` if it does not exist
     */
    operator fun get(name: String): MovementAbility? = abilities.firstOrNull { it.name == name }

    /**
     * Gets the current ability a player has active, if any.
     * @param player the player
     * @return their current ability, or `null` if they do not have one active
     */
    operator fun get(player: Player): MovementAbility? = playerAbilities[player.uuid]

    /**
     * Sets the ability for a player.
     * @param player the player
     * @param ability the ability
     */
    operator fun set(player: Player, ability: MovementAbility?) {
        // first remove the existing ability, if any
        playerAbilities.remove(player.uuid)

        // If the ability is null then there is no need to do anything else
        if (ability != null) {
            // now reapply the new ability and save it
            ability.apply(player)
            playerAbilities[player.uuid] = ability
        }
    }
}

/**
 * The current ability this player has active, if any.
 * @return their current ability, or `null` if they do not have one active
 */
var Player.ability: MovementAbility?
    get() = Momentum.abilityManager[this]
    set(ability) {
        Momentum.abilityManager[this] = ability
    }