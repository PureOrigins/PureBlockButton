package it.pureorigins.pureblockbutton

import it.pureorigins.framework.configuration.configFile
import it.pureorigins.framework.configuration.json
import it.pureorigins.framework.configuration.readFileAs
import kotlinx.serialization.Serializable
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos

object PureBlockButton : ModInitializer {
    private val clickListeners = HashMap<Region, MutableList<(ServerPlayerEntity, BlockPos) -> Unit>>()
    private val hoverListeners = HashMap<Region, MutableList<(ServerPlayerEntity, BlockPos) -> Unit>>()
    private val hoverOffListeners = HashMap<Region, MutableList<(ServerPlayerEntity, BlockPos) -> Unit>>()

    private val clickTimestamps = HashMap<ServerPlayerEntity, Long>()
    private val hoverPositions = HashMap<ServerPlayerEntity, BlockPos>()

    var clickDelay: Long = 0
        private set

    var tickDelta: Float = 1f
        private set
    
    var maxDistance: Double = 150.0
        private set
    
    var includeFluids: Boolean = false
        private set
    
    override fun onInitialize() {
        val (clickDelay, maxDistance, tickDelta, includeFluids) = json.readFileAs(configFile("fancyparticles.json"), Config())
        this.clickDelay = clickDelay
        this.maxDistance = maxDistance
        this.tickDelta = tickDelta
        this.includeFluids = includeFluids

        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
            clickTimestamps -= handler.player
            hoverPositions -= handler.player
        }
    }

    fun registerClickEvent(region: Region, listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) {
        clickListeners.computeIfAbsent(region) { ArrayList() } += listener
    }

    fun registerHoverEvent(region: Region, listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) {
        hoverListeners.computeIfAbsent(region) { ArrayList() } += listener
    }

    fun registerHoverOffEvent(region: Region, listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) {
        hoverOffListeners.computeIfAbsent(region) { ArrayList() } += listener
    }
    
    fun unregisterClickEvent(region: Region, listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) {
        clickListeners[region]?.remove(listener)
    }
    
    fun unregisterHoverEvent(region: Region, listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) {
        hoverListeners[region]?.remove(listener)
    }
    
    fun unregisterHoverOffEvent(region: Region, listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) {
        hoverOffListeners[region]?.remove(listener)
    }

    fun click(player: ServerPlayerEntity, position: BlockPos) {
        val lastClickMillis = clickTimestamps[player]
        val now = System.currentTimeMillis()
        if (lastClickMillis == null || now - lastClickMillis > clickDelay) {
            clickListeners.forEach { (region, listener) ->
                if (position in region) {
                    listener.forEach { it(player, position) }
                    clickTimestamps[player] = now
                }
            }
        }
    }

    fun hover(player: ServerPlayerEntity, position: BlockPos) {
        val oldPos = hoverPositions[player]
        hoverListeners.forEach { (region, listener) ->
            if (position in region && (oldPos == null || oldPos !in region)) {
                listener.forEach { it(player, position) }
                hoverPositions[player] = position
            }
        }
        hoverOffListeners.forEach { (region, listener) ->
            if (oldPos != null && oldPos in region && position !in region) {
                listener.forEach { it(player, oldPos) }
                hoverPositions[player] = position
            }
        }
    }

    fun hoverOff(player: ServerPlayerEntity) {
        val oldPos = hoverPositions[player] ?: return
        hoverOffListeners.forEach { (region, listener) ->
            if (oldPos in region) {
                listener.forEach { it(player, oldPos) }
                hoverPositions -= player
            }
        }
    }

    @Serializable
    data class Config(
        val clickDelay: Long = 1000,
        val maxDistance: Double = 150.0,
        val tickDelta: Float = 1f,
        val includeFluids: Boolean = false
    )
}
