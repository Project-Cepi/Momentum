package world.cepi.momentum

import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.momentum.ability.DoubleJump
import world.cepi.momentum.ability.JumpSmash
import world.cepi.momentum.ability.RockPillar
import world.cepi.momentum.ability.SuperJump
import world.cepi.momentum.command.MovementCommand

object Momentum : Extension() {
    lateinit var abilityManager: AbilityManager

    override fun initialize() {
        abilityManager = AbilityManager(DoubleJump, RockPillar, SuperJump, JumpSmash)

        // register commands
        MovementCommand.register()

        logger.info("[Momentum] Extension enabled - ${abilityManager.abilities.size} abilities loaded!")
    }

    override fun terminate() {

        MovementCommand.unregister()

        logger.info("[Momentum] Extension disabled!")
    }
}