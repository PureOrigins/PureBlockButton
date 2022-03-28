package it.pureorigins.pureblockbutton

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player

interface Region {
    val location: Block
    
    operator fun contains(pos: Block): Boolean
    fun getPositions(): Array<Block>
    
    operator fun contains(pos: Location): Boolean = contains(pos.block)
}

fun Region.onClick(listener: (player: Player, position: Location) -> Unit) =
    plugin.registerClickEvent(this, listener)

fun Region.onHover(listener: (player: Player, position: Location) -> Unit) =
    plugin.registerHoverEvent(this, listener)

fun Region.onHoverOff(listener: (player: Player, position: Location) -> Unit) =
    plugin.registerHoverOffEvent(this, listener)

fun Player.sendRegionChange(region: Region, newBlocks: Block) {
    val locDelta = newBlocks.location.subtract(region.location.location)
    sendMultiBlockChange(region.getPositions().associate { it.location to it.location.add(locDelta).block.blockData }.toMap())
}

fun Player.sendRegionChange(region: Region, newBlocks: Location) = sendRegionChange(region, newBlocks.block)

fun Player.clearRegionChange(region: Region) {
    sendMultiBlockChange(region.getPositions().associate { it.location to it.blockData }.toMap())
}