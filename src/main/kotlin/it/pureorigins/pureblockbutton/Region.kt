package it.pureorigins.pureblockbutton

import org.bukkit.Location
import org.bukkit.entity.Player

interface Region {
    val location: Location
    
    operator fun contains(pos: Location): Boolean
    fun getPositions(): Array<Location>
    fun move(location: Location): Region
}

fun Region.onClick(listener: (player: Player, position: Location) -> Unit) =
    PureBlockButton.registerClickEvent(this, listener)

fun Region.onHover(listener: (player: Player, position: Location) -> Unit) =
    PureBlockButton.registerHoverEvent(this, listener)

fun Region.onHoverOff(listener: (player: Player, position: Location) -> Unit) =
    PureBlockButton.registerHoverOffEvent(this, listener)

fun Player.sendRegionChange(region: Region, newBlocks: Location) {
    val locDelta = newBlocks.subtract(region.location)
    sendMultiBlockChange(region.getPositions().associateWith { it.add(locDelta).block.blockData }.toMap())
}

fun Player.clearRegionChange(region: Region) {
    sendMultiBlockChange(region.getPositions().associateWith { it.block.blockData }.toMap())
}