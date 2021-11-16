package it.pureorigins.pureblockbutton;

import net.minecraft.util.math.BlockPos;

data class CuboidRegion(val position: BlockPos, val width: Int, val height: Int, val depth: Int) : Region {
    val min get() = position
    val max get() = BlockPos(min.x + width, min.y + height, min.z + depth)
    
    constructor(min: BlockPos, max: BlockPos) : this(min, max.x - min.x, max.y - min.y, max.z - min.z)
    
    override operator fun contains(pos: BlockPos): Boolean {
        return pos.x >= min.x && pos.x <= max.x && pos.y >= min.y && pos.y <= max.y && pos.z >= min.z && pos.z <= max.z
    }
}
