package world.cepi.momentum.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.ArgumentCallback
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.entity.Player
import world.cepi.kepi.messages.sendFormattedMessage
import world.cepi.kepi.messages.sendFormattedTranslatableMessage
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.literal
import world.cepi.momentum.Momentum
import world.cepi.momentum.ability

object MovementCommand : Command("movement") {

    init {
        val set = "set".literal()
        val clear = "clear".literal()
        val info = "info".literal()

        val abilityName = ArgumentType.Word("ability").from(*Momentum.abilityManager.abilities.map { it.name }.toTypedArray()).map { abilityName ->
            return@map Momentum.abilityManager[abilityName]
                ?: throw ArgumentSyntaxException("Unknown Ability", abilityName, 1)
        }

        abilityName.callback = ArgumentCallback { sender, exception ->
            sender.sendFormattedTranslatableMessage("momentum", "unknown", Component.text(exception.input, NamedTextColor.BLUE))
        }

        addSyntax(set, abilityName) { player, args ->
            if (player is Player) {
                val ability = args.get(abilityName)

                player.ability = ability
                player.sendFormattedTranslatableMessage("momentum", "set",
                    Component.text(ability.name, NamedTextColor.BLUE)
                        .hoverEvent(Component.text("Click to see more information about this ability!", NamedTextColor.GRAY))
                        .clickEvent(ClickEvent.suggestCommand("/movement info ${ability.name}")))
            }
        }

        addSyntax(clear) { player ->
            if (player is Player) {
                Momentum.abilityManager[player] = null
                player.sendFormattedTranslatableMessage("momentum", "clear")
            }
        }

        addSyntax(info, abilityName) { sender, args ->
            args.get(abilityName).description.trim().forEach { sender.sendFormattedMessage(Component.text(it)) }
        }
    }
}