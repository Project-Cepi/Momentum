package world.cepi.momentum.command

import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.asSubcommand
import world.cepi.momentum.Momentum
import world.cepi.momentum.ability

class MovementCommand : Command("movement") {

    init {
        val set = "set".asSubcommand()
        val remove = "remove".asSubcommand()
        val info = "info".asSubcommand()

        val abilityName = ArgumentType.Word("ability").from(*Momentum.abilityManager.abilities.map { it.name }.toTypedArray())

        addSyntax(set, abilityName) { player, args ->
            if (player is Player) {
                Momentum.abilityManager[args.get(abilityName)].let {
                    if (it != null) {
                        player.ability = it
                        player.sendMessage("Ability set to ${it.name}!")
                    } else {
                        player.sendMessage("Unknown ability!")
                    }
                }
            }
        }

        addSyntax(remove) { player ->
            if (player is Player) {
                Momentum.abilityManager[player] = null
            }
        }

        addSyntax(info, abilityName) { sender, args ->
            Momentum.abilityManager[args.get(abilityName)].let {
                if (it != null) {
                    sender.sendMessage(it.description)
                } else {
                    sender.sendMessage("Unknown ability!")
                }
            }
        }
    }
}