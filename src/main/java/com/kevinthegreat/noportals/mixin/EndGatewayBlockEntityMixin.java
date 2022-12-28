package com.kevinthegreat.noportals.mixin;

import com.kevinthegreat.noportals.NoPortals;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.EndGatewayBlockEntity;
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

@Mixin(EndGatewayBlockEntity.class)
public abstract class EndGatewayBlockEntityMixin {
    @Shadow
    private static void startTeleportCooldown(World world, BlockPos pos, BlockState state, EndGatewayBlockEntity blockEntity) {
    }

    @Inject(method = "tryTeleportingEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/EndGatewayBlockEntity;teleportCooldown:I", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private static void noportals_disableEndGateway(World world, BlockPos pos, BlockState state, Entity entity, EndGatewayBlockEntity blockEntity, CallbackInfo ci) {
        if (NoPortals.getOptions().isEndGatewayDisabled()) {
            if (entity instanceof PlayerEntity player) {
                NoPortals.sendPortalDisabledMessage(player, NoPortals.MOD_ID + ":disabled.endGateway");
            }
            ci.cancel();
            startTeleportCooldown(world, pos, state, blockEntity);
        }
    }
}
