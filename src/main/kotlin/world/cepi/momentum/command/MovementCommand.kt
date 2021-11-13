package world.cepi.momentum.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.ArgumentCallback
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.entity.Player
import world.cepi.kepi.messages.sendFormattedMessage
import world.cepi.kepi.messages.sendFormattedTranslatableMessage
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.kommand.Kommand
import world.cepi.momentum.Momentum
import world.cepi.momentum.ability

object MovementCommand : Kommand({

    val set by literal
    val clear by literal
    val info by literal

    val abilityName = ArgumentType.Word("ability").from(*Momentum.abilityManager.abilities.map { it.name }.toTypedArray()).map { abilityName ->
        return@map Momentum.abilityManager[abilityName]
            ?: throw ArgumentSyntaxException("Unknown Ability", abilityName, 1)
    }

    abilityName.callback = ArgumentCallback { sender, exception ->
        sender.sendFormattedTranslatableMessage("momentum", "unknown", Component.text(exception.input, NamedTextColor.BLUE))
    }

    syntax(set, abilityName) {
        val ability = !abilityName

        player.ability = ability
        sender.sendFormattedTranslatableMessage("momentum", "set",
            Component.text(ability.name, NamedTextColor.BLUE)
                .hoverEvent(Component.text("Click to see more information about this ability!", NamedTextColor.GRAY))
                .clickEvent(ClickEvent.suggestCommand("/movement info ${ability.name}")))

    }

    syntax(clear).onlyPlayers {
        Momentum.abilityManager[player] = null
        sender.sendFormattedTranslatableMessage("momentum", "clear")

    }

    syntax(info, abilityName) {
        (!abilityName).description.trim().forEach { sender.sendFormattedMessage(Component.text(it)) }
    }

}, "movement")