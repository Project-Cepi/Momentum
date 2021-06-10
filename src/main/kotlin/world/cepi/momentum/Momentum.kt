package world.cepi.momentum

import net.minestom.server.extensions.Extension
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.kstom.extension.ExtensionCompanion
import world.cepi.momentum.ability.DoubleJump
import world.cepi.momentum.ability.JumpSmash
import world.cepi.momentum.ability.RockPillar
import world.cepi.momentum.ability.SuperJump
import world.cepi.momentum.command.MovementCommand

class Momentum : Extension() {

    override fun initialize() {
        // register commands
        MovementCommand.register()

        logger.info("[Momentum] Extension enabled - ${abilityManager.abilities.size} abilities loaded!")
    }

    override fun terminate() {

        MovementCommand.unregister()

        logger.info("[Momentum] Extension disabled!")
    }

    companion object: ExtensionCompanion<Momentum>(Momentum::class) {
        val abilityManager = AbilityManager(DoubleJump, RockPillar, SuperJump, JumpSmash)
    }
}