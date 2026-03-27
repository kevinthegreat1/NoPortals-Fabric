package com.kevinthegreat.noportals;

import com.kevinthegreat.noportals.option.SimpleBooleanOption;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.gametest.framework.GameTestHelper;

public class NoPortalsTest {
    @GameTest
    public void testDisableNetherPortal(GameTestHelper context) {
        testDisablePortal(context, Blocks.NETHER_PORTAL, NoPortals.getOptions().disableNetherPortal);
    }

    @GameTest
    public void testDisableEndPortal(GameTestHelper context) {
        testDisablePortal(context, Blocks.END_PORTAL, NoPortals.getOptions().disableEndPortal);
    }

    private void testDisablePortal(GameTestHelper context, Block portal, SimpleBooleanOption disablePortal) {
        context.setBlock(0, 0, 0, portal);
        disablePortal.setValue(true);
        context.spawn(EntityType.ZOMBIE, 0, 0, 0);

        // Wait 2 ticks to ensure the portal has time to teleport entities
        context.runAfterDelay(2, () -> {
            // The mod should have blocked the portal from teleporting entities
            context.assertEntityPresent(EntityType.ZOMBIE, 0, 0, 0);
            disablePortal.setValue(false);
        });

        context.runAfterDelay(4, () -> {
            context.assertEntityNotPresent(EntityType.ZOMBIE, 0, 0, 0);
            context.succeed();
        });
    }
}
