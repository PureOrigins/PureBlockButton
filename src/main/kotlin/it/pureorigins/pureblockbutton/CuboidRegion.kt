package it.pureorigins.pureblockbutton

import org.bukkit.Location
import org.bukkit.block.Block

data class CuboidRegion(override val location: Block, val width: Int, val height: Int, val depth: Int) : Region {
    constructor(location: Location, width: Int, height: Int, depth: Int) : this(location.block, width, height, depth)
    
    private val min get() = location
    private val max get() = location.location.add(width - 1.0, height - 1.0, depth - 1.0).block
    
    override operator fun contains(pos: Block): Boolean {
        val max = max // performance optimization
        return pos.x >= min.x && pos.x <= max.x && pos.y >= min.y && pos.y <= max.y && pos.z >= min.z && pos.z <= max.z
    }
    
    override fun getPositions() = Array(width * height * depth) {
        val x = min.x + it % width
        val y = min.y + (it / width) % height
        val z = min.z + it / (width * height)
        Location(location.world, x.toDouble(), y.toDouble(), z.toDouble()).block
    }
}
