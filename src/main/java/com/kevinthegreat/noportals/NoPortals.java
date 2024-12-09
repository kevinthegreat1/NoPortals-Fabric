package com.kevinthegreat.noportals;

import com.kevinthegreat.noportals.option.NoPortalsOptions;
import com.kevinthegreat.noportals.option.SimpleBooleanOption;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NoPortals implements ModInitializer {
    public static final String MOD_ID = "noportals";
    public static final String MOD_NAME = "No Portals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static NoPortalsOptions options;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> options = new NoPortalsOptions(server));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> options.save());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal(MOD_ID).requires(source -> source.hasPermissionLevel(2))
                .then(literal("reload").executes(context -> {
                    options = new NoPortalsOptions(context.getSource().getServer());
                    context.getSource().sendFeedback(() -> Text.translatable(MOD_ID + ":commands.option.reload").formatted(Formatting.GREEN), true);
                    return Command.SINGLE_SUCCESS;
                })).then(literal("save").executes(context -> {
                    options.save();
                    context.getSource().sendFeedback(() -> Text.translatable(MOD_ID + ":commands.option.save").formatted(Formatting.GREEN), true);
                    return Command.SINGLE_SUCCESS;
                })).then(argument("option", StringArgumentType.string()).suggests((context, builder) -> {
                            String input = context.getInput();
                            String inputOption = input.substring(input.lastIndexOf(' ') + 1);
                            options.options.keySet().stream().filter(name -> name.startsWith(inputOption)).forEach(builder::suggest);
                            return builder.buildFuture();
                        }).then(argument("value", BoolArgumentType.bool()).executes(context -> {
                            String option = StringArgumentType.getString(context, "option");
                            boolean value = BoolArgumentType.getBool(context, "value");
                            options.options.get(option).setValue(value);
                            context.getSource().sendFeedback(() -> Text.translatable(MOD_ID + ":commands.option.set", option, String.valueOf(value)).formatted(Formatting.GREEN), true);
                            return Command.SINGLE_SUCCESS;
                        })).executes(context -> {
                            context.getSource().sendFeedback(() -> Text.translatable(MOD_ID + ":commands.option.query", StringArgumentType.getString(context, "option"), String.valueOf(options.options.get(StringArgumentType.getString(context, "option")).getValue())).formatted(Formatting.GREEN), false);
                            return Command.SINGLE_SUCCESS;
                        })
                )
        ));
        LOGGER.info(MOD_NAME + " initialized.");
    }

    public static NoPortalsOptions getOptions() {
        return options;
    }

    public static void sendPortalDisabledMessage(PlayerEntity player, SimpleBooleanOption option) {
        player.sendMessage(Text.translatable(MOD_ID + ":disabled." + option.getTranslationKey()).formatted(Formatting.BOLD, Formatting.RED), true);
    }
}
