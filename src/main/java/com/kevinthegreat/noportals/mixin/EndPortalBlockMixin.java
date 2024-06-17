package com.kevinthegreat.noportals.mixin;

import com.kevinthegreat.noportals.NoPortals;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public abstract class EndPortalBlockMixin {
    @Inject(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getRegistryKey()Lnet/minecraft/registry/RegistryKey;"), cancellable = true)
    private void noportals$disableEndPortal(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (NoPortals.getOptions().isEndPortalDisabled()) {
            if (entity instanceof PlayerEntity player) {
                NoPortals.sendPortalDisabledMessage(player, NoPortals.MOD_ID + ":disabled.end");
            }
            ci.cancel();
        }
    }
}
