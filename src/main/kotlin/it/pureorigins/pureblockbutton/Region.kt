package it.pureorigins.pureblockbutton

import it.pureorigins.pureblockbutton.mixins.ChunkDeltaUpdateS2CPacketAccessor
import it.unimi.dsi.fastutil.shorts.ShortArraySet
import it.unimi.dsi.fastutil.shorts.ShortSet
import net.minecraft.block.BlockState
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.World

interface Region {
    val position: BlockPos
    
    operator fun contains(pos: BlockPos): Boolean
    fun getPositions(): Array<BlockPos>
    fun move(position: BlockPos): Region
    
    fun getChunkedPositions(): Map<ChunkSectionPos, ShortSet> {
        val positions = mutableMapOf<ChunkSectionPos, ShortSet>()
        getPositions().forEach {
            val chunk = ChunkSectionPos.from(it)
            positions.computeIfAbsent(chunk) { ShortArraySet() } += ChunkSectionPos.packLocal(it)
        }
        return positions
    }
    
    fun getChunkedBlockStates(blocksPos: BlockPos, world: World): Map<ChunkSectionPos, List<BlockState>> {
        val positions = mutableMapOf<ChunkSectionPos, ArrayList<BlockState>>()
        val offset = blocksPos.subtract(position)
        getPositions().forEach {
            val chunk = ChunkSectionPos.from(it)
            positions.computeIfAbsent(chunk) { ArrayList() } += world.getBlockState(it.add(offset))
        }
        return positions
    }
}

fun Region.onClick(listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) =
    PureBlockButton.registerClickEvent(this, listener)

fun Region.onHover(listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) =
    PureBlockButton.registerHoverEvent(this, listener)

fun Region.onHoverOff(listener: (player: ServerPlayerEntity, position: BlockPos) -> Unit) =
    PureBlockButton.registerHoverOffEvent(this, listener)

fun ServerPlayerEntity.sendRegionChange(region: Region, newBlocks: BlockPos) {
    val positions = region.getChunkedPositions()
    val blockStates = region.getChunkedBlockStates(newBlocks, world)
    positions.forEach { (section, positions) ->
        val packet = ChunkDeltaUpdateS2CPacket(section, positions, world.getChunk(section.x, section.z).getSection(section.y), true).apply {
            (this as ChunkDeltaUpdateS2CPacketAccessor).setBlockStates(blockStates[section]!!.toTypedArray())
        }
        networkHandler.sendPacket(packet)
    }
}

fun ServerPlayerEntity.clearRegionChange(region: Region) {
    val positions = region.getChunkedPositions()
    positions.forEach { (section, positions) ->
        networkHandler.sendPacket(ChunkDeltaUpdateS2CPacket(section, positions, world.getChunk(section.x, section.z).getSection(section.y), true))
    }
}