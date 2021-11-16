package it.pureorigins.pureblockbutton.mixins;

import it.pureorigins.pureblockbutton.PureBlockButton;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onHandSwing", at = @At("TAIL"))
    public void onHandSwing(HandSwingC2SPacket packet, CallbackInfo callback) {
        var hit = (BlockHitResult) player.raycast(PureBlockButton.INSTANCE.getMaxDistance(), 1F, PureBlockButton.INSTANCE.getIncludeFluids());
        if (hit.getType() == BlockHitResult.Type.BLOCK) {
            PureBlockButton.INSTANCE.click(player, hit.getBlockPos());
        }
    }

    @Inject(method = "onPlayerMove", at = @At("TAIL"))
    public void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo callback) {
        var hit = (BlockHitResult) player.raycast(PureBlockButton.INSTANCE.getMaxDistance(), 1F, PureBlockButton.INSTANCE.getIncludeFluids());
        if (hit.getType() == BlockHitResult.Type.BLOCK) {
            PureBlockButton.INSTANCE.hover(player, hit.getBlockPos());
        } else {
            PureBlockButton.INSTANCE.leave(player);
        }
    }
}

