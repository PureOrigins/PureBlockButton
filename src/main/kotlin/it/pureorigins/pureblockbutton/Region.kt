package it.pureorigins.pureblockbutton

import net.minecraft.util.math.BlockPos

interface Region {
    operator fun contains(pos: BlockPos): Boolean
}
