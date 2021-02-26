package world.cepi.momentum

import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension
import world.cepi.momentum.ability.DoubleJump
import world.cepi.momentum.ability.RockPillar
import world.cepi.momentum.ability.SuperJump
import world.cepi.momentum.command.MovementCommand

object Momentum : Extension() {
    lateinit var abilityManager: AbilityManager

    override fun initialize() {
        abilityManager = AbilityManager(DoubleJump(), RockPillar(), SuperJump())

        // register commands
        MinecraftServer.getCommandManager().register(MovementCommand())

        logger.info("[Momentum] Extension enabled - ${abilityManager.abilities.size} abilities loaded!")
    }

    override fun terminate() {
        logger.info("[Momentum] Extension disabled!")
    }
}