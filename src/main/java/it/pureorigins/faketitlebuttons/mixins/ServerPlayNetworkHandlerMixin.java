package it.pureorigins.faketitlebuttons.mixins;

import it.pureorigins.faketitlebuttons.FakeTitleButtons;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onHandSwing", at = @At("HEAD"))
    public void onHandSwing(HandSwingC2SPacket packet, CallbackInfo ci) {
        BlockPos pos = ((BlockHitResult) player.raycast(150, 1F, FakeTitleButtons.INSTANCE.getConfig().getIncludeFluids())).getBlockPos();
        FakeTitleButtons.INSTANCE.getClickListeners().forEach(listener -> listener.invoke(player, pos));
    }

    @Inject(method = "onPlayerMove", at = @At("HEAD"))
    public void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (packet.changesLook()) {
            BlockPos pos = ((BlockHitResult) player.raycast(150, 1F, FakeTitleButtons.INSTANCE.getConfig().getIncludeFluids())).getBlockPos();
            FakeTitleButtons.INSTANCE.getLookAtListeners().forEach(listener -> listener.invoke(player, pos));
        }
    }
}

