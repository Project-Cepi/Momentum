package world.cepi.momentum

import net.minestom.server.extensions.Extension
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.kstom.extension.ExtensionCompanion
import world.cepi.momentum.ability.*
import world.cepi.momentum.command.MovementCommand

class Momentum : Extension() {

    override fun initialize() {
        // Register ability manager
        abilityManager = AbilityManager(eventNode, *MovementAbility::class.sealedSubclasses.map { it.objectInstance!! }.toTypedArray())

        // register commands
        MovementCommand.register()

        logger.info("[Momentum] Extension enabled - ${abilityManager.abilities.size} abilities loaded!")
    }

    override fun terminate() {

        MovementCommand.unregister()

        logger.info("[Momentum] Extension disabled!")
    }

    companion object {
        lateinit var abilityManager: AbilityManager
    }
}