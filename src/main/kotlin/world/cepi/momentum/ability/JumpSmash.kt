package world.cepi.momentum.ability

import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.EventCallback
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerStartFlyingEvent
import net.minestom.server.event.player.PlayerStartSneakingEvent
import net.minestom.server.sound.SoundEvent
import net.minestom.server.utils.time.TimeUnit
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listenOnly
import world.cepi.kstom.util.playSoundToViewersAndSelf
import world.cepi.kstom.util.viewersAndSelfAsAudience
import world.cepi.momentum.cooldown.PredicateCooldown

object JumpSmash : MovementAbility(), EventCallback<PlayerStartFlyingEvent> {

    override val cooldown = PredicateCooldown(Player::isOnGround)

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

    override fun run(event: PlayerStartFlyingEvent): Unit = with(event) {

        // cancel the flying first
        player.isFlying = false
        player.isAllowFlying = false

        player.velocity = player.position.direction().mul(5.0).withY(15.0)
        player.playSoundToViewersAndSelf(Sound.sound(SoundEvent.ENTITY_ENDER_DRAGON_SHOOT, Sound.Source.MASTER, 1f, 2f))

        Manager.scheduler.buildTask {
            player.velocity = player.position.direction().mul(5.0).withY(-15.0)
        }.delay(10, TimeUnit.CLIENT_TICK).schedule()

    }
}