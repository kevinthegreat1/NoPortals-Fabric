package com.kevinthegreat.noportals.mixin;

import com.kevinthegreat.noportals.NoPortals;
import com.kevinthegreat.noportals.option.SimpleBooleanOption;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({NetherPortalBlock.class, EndPortalBlock.class, EndGatewayBlock.class})
public abstract class PortalBlockMixin extends Block {
    public PortalBlockMixin(Properties settings) {
        super(settings);
    }

    @ModifyExpressionValue(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;canUsePortal(Z)Z"))
    private boolean noportals$canUsePortal(boolean canUsePortals, @Local(argsOnly = true) Entity entity) {
        if (!canUsePortals) return false;

        SimpleBooleanOption disableOption = NoPortals.getOptions().getDisablePortalOption(this);
        if (disableOption != null && disableOption.getValue()) {
            if (entity instanceof Player player) {
                NoPortals.sendPortalDisabledMessage(player, disableOption);
            }
            return false;
        }
        return true;
    }
}
