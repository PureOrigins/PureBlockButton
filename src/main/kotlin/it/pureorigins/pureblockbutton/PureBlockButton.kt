package it.pureorigins.pureblockbutton

import com.destroystokyo.paper.block.TargetBlockInfo
import com.destroystokyo.paper.block.TargetBlockInfo.FluidMode.ALWAYS
import com.destroystokyo.paper.block.TargetBlockInfo.FluidMode.NEVER
import it.pureorigins.common.file
import it.pureorigins.common.json
import it.pureorigins.common.readFileAs
import it.pureorigins.common.registerEvents
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var plugin: PureBlockButton private set

class PureBlockButton : JavaPlugin(), Listener {
    private val clickListeners = HashMap<Region, MutableList<(Player, Location) -> Unit>>()
    private val hoverListeners = HashMap<Region, MutableList<(Player, Location) -> Unit>>()
    private val hoverOffListeners = HashMap<Region, MutableList<(Player, Location) -> Unit>>()
    
    private val clickTimestamps = HashMap<Player, Long>()
    private val hoverTimestamps = HashMap<Player, Long>()
    private val hoverPositions = HashMap<Player, Location>()
    
    var clickDelay: Long = 0
        private set
    
    var hoverDelay: Long = 0
        private set
    
    var maxDistance: Int = 150
        private set
    
    var includeFluids: TargetBlockInfo.FluidMode = NEVER
        private set
    
    fun registerClickEvent(region: Region, listener: (player: Player, position: Location) -> Unit) {
        clickListeners.computeIfAbsent(region) { ArrayList() } += listener
    }
    
    fun registerHoverEvent(region: Region, listener: (player: Player, position: Location) -> Unit) {
        hoverListeners.computeIfAbsent(region) { ArrayList() } += listener
    }
    
    fun registerHoverOffEvent(region: Region, listener: (player: Player, position: Location) -> Unit) {
        hoverOffListeners.computeIfAbsent(region) { ArrayList() } += listener
    }
    
    fun unregisterClickEvent(region: Region, listener: (player: Player, block: Location) -> Unit) {
        clickListeners[region]?.remove(listener)
    }
    
    fun unregisterHoverEvent(region: Region, listener: (player: Player, block: Location) -> Unit) {
        hoverListeners[region]?.remove(listener)
    }
    
    fun unregisterHoverOffEvent(region: Region, listener: (player: Player, block: Location) -> Unit) {
        hoverOffListeners[region]?.remove(listener)
    }
    
    override fun onLoad() {
        plugin = this
    }
    
    override fun onEnable() {
        val (clickDelay, hoverDelay, maxDistance, includeFluids) = json.readFileAs(file("config.json"), Config())
        this.clickDelay = clickDelay
        this.hoverDelay = hoverDelay
        this.maxDistance = maxDistance
        this.includeFluids = if (includeFluids) ALWAYS else NEVER

        registerEvents(this)
    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        if (e.action != Action.LEFT_CLICK_AIR && e.action != Action.LEFT_CLICK_BLOCK) return
        val block = e.player.getTargetBlock(maxDistance, includeFluids) ?: return
        val lastClickMillis = clickTimestamps[e.player]
        val now = System.currentTimeMillis()
        if (lastClickMillis == null || now - lastClickMillis > clickDelay) {
            clickListeners.forEach { (region, listener) ->
                if (block.location in region) {
                    listener.forEach { it(e.player, block.location) }
                    clickTimestamps[e.player] = now
                }
            }
        }
    }

    @EventHandler
    fun hover(e: PlayerMoveEvent) {
        val player = e.player
        val block = player.getTargetBlock(maxDistance, includeFluids) ?: return
        val oldPos = hoverPositions[player]
        val lastHoverMillis = hoverTimestamps[player]
        val now = System.currentTimeMillis()
        if (lastHoverMillis == null || now - lastHoverMillis > hoverDelay) {
            hoverListeners.forEach { (region, listener) ->
                if (block.location in region && (oldPos == null || oldPos !in region)) {
                    listener.forEach { it(player, block.location) }
                    hoverPositions[player] = block.location
                    hoverTimestamps[player] = now
                }
            }
            hoverOffListeners.forEach { (region, listener) ->
                if (oldPos != null && oldPos in region && block.location !in region) {
                    listener.forEach { it(player, oldPos) }
                    hoverPositions[player] = block.location
                }
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        clickTimestamps -= e.player
        hoverTimestamps -= e.player
        hoverPositions -= e.player
    }

    @Serializable
    data class Config(
        val clickDelay: Long = 1000,
        val hoverDelay: Long = 200,
        val maxDistance: Int = 120,
        val includeFluids: Boolean = false
    )
}
