package it.pureorigins.pureblockbutton

import org.bukkit.Location

data class CuboidRegion(override val location: Location, val width: Int, val height: Int, val depth: Int) : Region {
    private val min get() = location
    private val max get() = location.add(width.toDouble(), height.toDouble(), depth.toDouble())
    
    override operator fun contains(pos: Location): Boolean {
        return pos.x >= min.x && pos.x <= max.x && pos.y >= min.y && pos.y <= max.y && pos.z >= min.z && pos.z <= max.z
    }
    
    override fun getPositions() = Array(width * height * depth) {
        val x = min.x + it % width
        val y = min.y + (it / width) % height
        val z = min.z + it / (width * height)
        Location(location.world, x, y, z)
    }
    
    override fun move(location: Location): Region = copy(location = this.location)
}
