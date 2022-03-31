package it.pureorigins.pureblockbutton

import it.pureorigins.common.*
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.FluidCollisionMode.ALWAYS
import org.bukkit.FluidCollisionMode.NEVER
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
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
    
    var lookDelay: Long = 10
        private set
    
    var includeFluids: FluidCollisionMode = NEVER
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
        val (clickDelay, hoverDelay, maxDistance, lookDelay, includeFluids) = json.readFileAs(file("config.json"), Config())
        this.clickDelay = clickDelay
        this.hoverDelay = hoverDelay
        this.maxDistance = maxDistance
        this.lookDelay = lookDelay
        this.includeFluids = if (includeFluids) ALWAYS else NEVER

        registerEvents(this)
        runTaskTimer(0, lookDelay) {
            Bukkit.getOnlinePlayers().forEach {
                onHover(it)
            }
        }
    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        if (e.action != Action.LEFT_CLICK_AIR && e.action != Action.LEFT_CLICK_BLOCK) return
        val block = e.player.getTargetBlockExact(maxDistance, includeFluids) ?: return
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

    fun onHover(player: Player) {
        val block = player.getTargetBlockExact(maxDistance, includeFluids) ?: return
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
        val lookDelay: Long = 10,
        val includeFluids: Boolean = false
    )
}
