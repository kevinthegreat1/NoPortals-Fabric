package com.kevinthegreat.noportals.mixin;

import com.kevinthegreat.noportals.NoPortals;
import com.kevinthegreat.noportals.option.SimpleBooleanOption;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.EndGatewayBlock;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({NetherPortalBlock.class, EndPortalBlock.class, EndGatewayBlock.class})
public abstract class PortalBlockMixin extends Block {
    public PortalBlockMixin(Settings settings) {
        super(settings);
    }

    @ModifyExpressionValue(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;canUsePortals(Z)Z"))
    private boolean noportals$canUsePortal(boolean canUsePortals, @Local(argsOnly = true) Entity entity) {
        if (!canUsePortals) return false;

        SimpleBooleanOption disableOption = NoPortals.getOptions().getDisablePortalOption(this);
        if (disableOption != null && disableOption.getValue()) {
            if (entity instanceof PlayerEntity player) {
                NoPortals.sendPortalDisabledMessage(player, disableOption);
            }
            return false;
        }
        return true;
    }
}
