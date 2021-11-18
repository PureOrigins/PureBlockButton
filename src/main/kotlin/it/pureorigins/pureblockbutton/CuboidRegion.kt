package it.pureorigins.pureblockbutton

import net.minecraft.util.math.BlockPos

data class CuboidRegion(override val position: BlockPos, val width: Int, val height: Int, val depth: Int) : Region {
    val min get() = position
    val max get() = BlockPos(min.x + width, min.y + height, min.z + depth)
    
    override operator fun contains(pos: BlockPos): Boolean {
        val max = max
        return pos.x >= min.x && pos.x <= max.x && pos.y >= min.y && pos.y <= max.y && pos.z >= min.z && pos.z <= max.z
    }
    
    override fun getPositions() = Array(width * height * depth) {
        val x = min.x + it % width
        val y = min.y + (it / width) % height
        val z = min.z + it / (width * height)
        BlockPos(x, y, z)
    }
    
    override fun move(position: BlockPos) = copy(position = position)
}
