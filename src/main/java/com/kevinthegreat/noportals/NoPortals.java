package com.kevinthegreat.noportals;

import com.kevinthegreat.noportals.option.NoPortalsOptions;
import com.kevinthegreat.noportals.option.SimpleBooleanOption;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class NoPortals implements ModInitializer {
    public static final String MOD_ID = "noportals";
    public static final String MOD_NAME = "No Portals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static NoPortalsOptions options;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> options = new NoPortalsOptions(server));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> options.save());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal(MOD_ID).requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("reload").executes(context -> {
                    options = new NoPortalsOptions(context.getSource().getServer());
                    context.getSource().sendSuccess(() -> Component.translatable(MOD_ID + ":commands.option.reload").withStyle(ChatFormatting.GREEN), true);
                    return Command.SINGLE_SUCCESS;
                })).then(literal("save").executes(context -> {
                    options.save();
                    context.getSource().sendSuccess(() -> Component.translatable(MOD_ID + ":commands.option.save").withStyle(ChatFormatting.GREEN), true);
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
                            context.getSource().sendSuccess(() -> Component.translatable(MOD_ID + ":commands.option.set", option, String.valueOf(value)).withStyle(ChatFormatting.GREEN), true);
                            return Command.SINGLE_SUCCESS;
                        })).executes(context -> {
                            context.getSource().sendSuccess(() -> Component.translatable(MOD_ID + ":commands.option.query", StringArgumentType.getString(context, "option"), String.valueOf(options.options.get(StringArgumentType.getString(context, "option")).getValue())).withStyle(ChatFormatting.GREEN), false);
                            return Command.SINGLE_SUCCESS;
                        })
                )
        ));
        LOGGER.info(MOD_NAME + " initialized.");
    }

    public static NoPortalsOptions getOptions() {
        return options;
    }

    public static void sendPortalDisabledMessage(Player player, SimpleBooleanOption option) {
        player.displayClientMessage(Component.translatable(MOD_ID + ":disabled." + option.getTranslationKey()).withStyle(ChatFormatting.BOLD, ChatFormatting.RED), true);
    }
}
