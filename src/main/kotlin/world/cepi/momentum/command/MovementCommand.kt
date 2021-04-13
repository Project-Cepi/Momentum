package world.cepi.momentum.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
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
        val clear = "clear".asSubcommand()
        val info = "info".asSubcommand()

        val abilityName = ArgumentType.Word("ability").from(*Momentum.abilityManager.abilities.map { it.name }.toTypedArray())

        addSyntax(set, abilityName) { player, args ->
            if (player is Player) {
                Momentum.abilityManager[args.get(abilityName)].let {
                    if (it != null) {
                        player.ability = it
                        player.sendMessage(Component.text("Ability set to ", NamedTextColor.DARK_GRAY)
                            .append(Component.text(it.name, NamedTextColor.GRAY)
                                .hoverEvent(Component.text("Click to see more information about this ability!", NamedTextColor.GRAY))
                                .clickEvent(ClickEvent.suggestCommand("/movement info ${it.name}")))
                            .append(Component.text("!", NamedTextColor.DARK_GRAY)))
                    } else {
                        player.sendMessage(Component.text("Unknown ability!", NamedTextColor.RED))
                    }
                }
            }
        }

        addSyntax(clear) { player ->
            if (player is Player) {
                Momentum.abilityManager[player] = null
            }
        }

        addSyntax(info, abilityName) { sender, args ->
            Momentum.abilityManager[args.get(abilityName)].let {
                if (it != null) {
                    sender.sendMessage(Component.text(it.description, NamedTextColor.GRAY))
                } else {
                    sender.sendMessage(Component.text("Unknown ability!", NamedTextColor.RED))
                }
            }
        }
    }
}