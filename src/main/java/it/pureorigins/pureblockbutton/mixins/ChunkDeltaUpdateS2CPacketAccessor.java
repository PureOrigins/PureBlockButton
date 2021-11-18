package it.pureorigins.pureblockbutton.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public interface ChunkDeltaUpdateS2CPacketAccessor {
  @Mutable
  @Accessor("blockStates")
  void setBlockStates(BlockState[] states);
}
