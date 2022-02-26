package world.cepi.momentum.ability

import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.EventCallback
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerStartFlyingEvent
import net.minestom.server.sound.SoundEvent
import world.cepi.kstom.event.listenOnly
import world.cepi.kstom.util.viewersAndSelfAsAudience
import world.cepi.momentum.cooldown.Cooldown
import world.cepi.momentum.cooldown.PredicateCooldown
import world.cepi.particle.Particle
import world.cepi.particle.ParticleType
import world.cepi.particle.data.OffsetAndSpeed
import world.cepi.particle.renderer.Renderer
import world.cepi.particle.renderer.render
import world.cepi.particle.renderer.translate

/**
 * By double tapping the space bar (the default way to toggle flying in vanilla), the
 * player will get a brief boost to their momentum, as if they performed a second jump
 * in the air.
 */
object DoubleJump : MovementAbility(), EventCallback<PlayerStartFlyingEvent> {

    override val description: String = """
        By double tapping the space bar (the default way to toggle flying in vanilla), the
        player will get a brief boost to their momentum, as if they performed a second jump
        in the air.
    """.trimIndent()

    override val cooldown: Cooldown = PredicateCooldown(Player::isOnGround)

    override fun initialise() {
        node.listenOnly(::run)
        node.listenOnly<PlayerMoveEvent> {
            if (player.isOnGround) player.isAllowFlying = true
        }
    }

    override fun apply(player: Player) {
        player.isAllowFlying = true
    }

    override fun remove(player: Player) {
        player.isAllowFlying = player.isCreative || player.gameMode == GameMode.SPECTATOR
    }

    override fun run(event: PlayerStartFlyingEvent) = with(event) {
        // cancel the flying first
        player.isFlying = false
        player.isAllowFlying = false

        player.viewersAndSelfAsAudience.playSound(Sound.sound(SoundEvent.ENTITY_BAT_TAKEOFF, Sound.Source.MASTER, 1f, 2f))
        val circle = Renderer.circle(1.0).translate(player.position.asVec())
        val particle = Particle.particle(ParticleType.CLOUD, 1, OffsetAndSpeed())

        circle.render(particle, player.viewersAndSelfAsAudience)

        player.velocity = player.position.direction().mul(12.0).withY(10.0)

    }
}