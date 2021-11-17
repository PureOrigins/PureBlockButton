package it.pureorigins.pureblockbutton;

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos

data class CuboidRegion(val position: BlockPos, val width: Int, val height: Int, val depth: Int) : Region {
    val min get() = position
    val max get() = BlockPos(min.x + width, min.y + height, min.z + depth)

    constructor(min: BlockPos, max: BlockPos) : this(min, max.x - min.x, max.y - min.y, max.z - min.z)

    override operator fun contains(pos: BlockPos): Boolean {
        val max = this.max
        return pos.x >= min.x && pos.x <= max.x && pos.y >= min.y && pos.y <= max.y && pos.z >= min.z && pos.z <= max.z
    }

    override val chunkSections: List<ChunkSectionPos>
        get() {
            val chunks = HashSet<ChunkSectionPos>()
            for (x in min.x..max.x step 16)
                for (y in min.y..max.y step 16)
                    for (z in min.z..max.z step 16)
                        chunks.add(ChunkSectionPos.from(x, y, z))
            for (y in min.y..max.y)
                chunks.add(ChunkSectionPos.from(max.x, y, max.z))
            for (z in min.z..max.z)
                chunks.add(ChunkSectionPos.from(max.x, max.y, z))
            for (x in min.x..max.x)
                chunks.add(ChunkSectionPos.from(x, max.y, max.z))

            return chunks.toList()
        }
}
