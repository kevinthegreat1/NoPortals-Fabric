package com.kevinthegreat.noportals.mixin;

import com.kevinthegreat.noportals.NoPortals;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
    @Inject(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setInNetherPortal(Lnet/minecraft/util/math/BlockPos;)V"), cancellable = true)
    private void noportals_disableNetherPortal(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (NoPortals.getOptions().isNetherPortalDisabled()) {
            if (entity instanceof PlayerEntity player) {
                NoPortals.sendPortalDisabledMessage(player, NoPortals.MOD_ID + ":disabled.nether");
            }
            ci.cancel();
        }
    }
}
