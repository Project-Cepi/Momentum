package world.cepi.momentum

import net.minestom.server.extensions.Extension
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.kstom.util.log
import world.cepi.kstom.util.node
import world.cepi.momentum.ability.*
import world.cepi.momentum.command.MovementCommand

class Momentum : Extension() {

    override fun initialize(): LoadStatus {
        // Register ability manager
        abilityManager = AbilityManager(node, *MovementAbility::class.sealedSubclasses.map { it.objectInstance!! }.toTypedArray())

        // register commands
        MovementCommand.register()

        log.info("[Momentum] Extension enabled - ${abilityManager.abilities.size} abilities loaded!")

        return LoadStatus.SUCCESS
    }

    override fun terminate() {

        MovementCommand.unregister()

        log.info("[Momentum] Extension disabled!")
    }

    companion object {
        lateinit var abilityManager: AbilityManager
    }
}