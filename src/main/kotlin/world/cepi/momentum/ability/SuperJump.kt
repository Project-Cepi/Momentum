package world.cepi.momentum.ability

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.EventCallback
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket.Action.START_SNEAKING
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket.Action.STOP_SNEAKING
import net.minestom.server.utils.Vector
import world.cepi.kstom.addEventCallback
import world.cepi.momentum.MovementAbility
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class SuperJump : MovementAbility(), EventCallback<PlayerMoveEvent> {
    private val players = HashSet<UUID>()
    private val sneakingTime = HashMap<UUID, Long>()

    override fun getDescription(): String = """
        Holding shift whilst on the ground will "charge" the super jump. When you release
        shift you will be launched into the air a certain amount dependent on how long you
        were shifting for. Moving whilst charging the super jump will cancel the jump.
    """.trimIndent()

    override fun initialise() {
        // todo switch to the new sneaking events when they are pulled
        MinecraftServer.getConnectionManager().onPacketReceive { player, _, packet ->
            if (packet is ClientEntityActionPacket && players.contains(player.uuid)) {
                @Suppress("NON_EXHAUSTIVE_WHEN")
                when (packet.action) {
                    START_SNEAKING -> sneakingTime[player.uuid] = System.currentTimeMillis()
                    STOP_SNEAKING -> performSuperJump(player)
                }
            }
        }
    }

    private fun performSuperJump(player: Player) {
        sneakingTime.remove(player.uuid).let {
            if (it != null) {
                val vector = getMultiplier(System.currentTimeMillis() - it)
                player.sendMessage(vector.toString())
                player.velocity = vector
            }
        }
    }

    /**
     * A function that, given a wait time in seconds, will return the vector for a
     * jump. This essentially uses the number of seconds waited as the new vector with a
     * set limit to prevent jumping too high.
     * @param milliseconds the amount of seconds the jump has been charging
     * @return the vector for the jump
     */
    private fun getMultiplier(milliseconds: Long): Vector {
        println(milliseconds / 1000.0)
        return Vector(0.0, (milliseconds / 1000.0).coerceAtMost(10.0), 0.0)
    }

    override fun apply(player: Player) {
        players.add(player.uuid)
        player.addEventCallback(::run)
    }

    override fun remove(player: Player) {
        sneakingTime.remove(player.uuid)
        players.remove(player.uuid)
        player.removeEventCallback(PlayerMoveEvent::class.java, ::run)
    }

    override fun run(event: PlayerMoveEvent) {
        // cancel super jump on move todo check if we want to do this - make it configurable?
        if (!event.newPosition.isSimilar(event.player.position)) {
            sneakingTime.remove(event.player.uuid)
        }
    }
}