package com.kevinthegreat.noportals.mixin;

import com.kevinthegreat.noportals.NoPortals;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndGatewayBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndGatewayBlock.class)
public abstract class EndGatewayBlockEntityMixin {
    @Inject(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tryUsePortal(Lnet/minecraft/block/Portal;Lnet/minecraft/util/math/BlockPos;)V"), cancellable = true)
    protected void noportals_disableEndGateway(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (NoPortals.getOptions().isEndGatewayDisabled()) {
            if (entity instanceof PlayerEntity player) {
                NoPortals.sendPortalDisabledMessage(player, NoPortals.MOD_ID + ":disabled.endGateway");
            }
            ci.cancel();
        }
    }
}
