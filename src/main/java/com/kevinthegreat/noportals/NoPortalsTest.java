package com.kevinthegreat.noportals;

import com.kevinthegreat.noportals.option.SimpleBooleanOption;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public class NoPortalsTest implements FabricGameTest {
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void testDisableNetherPortal(TestContext context) {
        testDisablePortal(context, Blocks.NETHER_PORTAL, NoPortals.getOptions().disableNetherPortal);
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void testDisableEndPortal(TestContext context) {
        testDisablePortal(context, Blocks.END_PORTAL, NoPortals.getOptions().disableEndPortal);
    }

    private void testDisablePortal(TestContext context, Block portal, SimpleBooleanOption disablePortal) {
        context.setBlockState(0, 0, 0, portal);
        disablePortal.setValue(true);
        context.spawnEntity(EntityType.ZOMBIE, 0, 0, 0);

        // Wait 2 ticks to ensure the portal has time to teleport entities
        context.waitAndRun(2, () -> {
            // The mod should have blocked the portal from teleporting entities
            context.expectEntityAt(EntityType.ZOMBIE, 0, 0, 0);
            disablePortal.setValue(false);
        });

        context.waitAndRun(4, () -> {
            context.dontExpectEntityAt(EntityType.ZOMBIE, 0, 0, 0);
            context.complete();
        });
    }
}
