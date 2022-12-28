package com.kevinthegreat.noportals;

import com.kevinthegreat.noportals.option.NoPortalsOptions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoPortals implements ModInitializer {
    public static final String MOD_ID = "noportals";
    public static final String MOD_NAME = "No Portals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static NoPortalsOptions options;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> options = new NoPortalsOptions(server));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> options.save());
        LOGGER.info(MOD_NAME + " initialized.");
    }

    public static NoPortalsOptions getOptions() {
        return options;
    }

    public static void sendPortalDisabledMessage(PlayerEntity player, String message) {
        player.sendMessage(Text.literal(message).formatted(Formatting.BOLD, Formatting.RED), true);
    }
}
