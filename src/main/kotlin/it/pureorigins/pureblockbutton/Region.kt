package it.pureorigins.pureblockbutton

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos

interface Region {
    operator fun contains(pos: BlockPos): Boolean
    val chunkSections: List<ChunkSectionPos>
}

fun Region.onClick(listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) =
    PureBlockButton.registerClickEvent(this, listener)

fun Region.onHover(listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) =
    PureBlockButton.registerHoverEvent(this, listener)

fun Region.onHoverOff(listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) =
    PureBlockButton.registerHoverOffEvent(this, listener)
