package it.pureorigins.faketitlebuttons;

import net.minecraft.util.math.BlockPos;

public class CuboidRegion(val min: BlockPos, val max: BlockPos) : Region {
    override fun contains(pos: BlockPos): Boolean {
        return pos.x >= min.x && pos.x <= max.x && pos.y >= min.y && pos.y <= max.y && pos.z >= min.z && pos.z <= max.z
    }
}
