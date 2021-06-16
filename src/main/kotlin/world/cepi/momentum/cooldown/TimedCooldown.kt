package world.cepi.momentum.cooldown

import net.minestom.server.entity.Player
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * A simple timed cooldown that will expire for each player after a set amount of time has passed.
 */
class TimedCooldown(private val duration: Duration): Cooldown {
    private val lastFiredMap = ConcurrentHashMap<UUID, Instant>()

    override fun canRun(player: Player): Boolean {
        return this.lastFiredMap[player.uuid]?.plus(this.duration)?.isBefore(Instant.now()) ?: true
    }

    override fun onRun(player: Player) {
        this.lastFiredMap[player.uuid] = Instant.now()
    }
}