package com.kevinthegreat.noportals.option;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.kevinthegreat.noportals.NoPortals;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.block.Block;
import net.minecraft.block.EndGatewayBlock;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class NoPortalsOptions {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path optionsFile;
    public final SimpleBooleanOption disableNetherPortal = new SimpleBooleanOption("disableNetherPortal", "nether");
    public final SimpleBooleanOption disableEndPortal = new SimpleBooleanOption("disableEndPortal", "end");
    public final SimpleBooleanOption disableEndGateway = new SimpleBooleanOption("disableEndGateway", "endGateway");
    public final Map<String, SimpleBooleanOption> options = ImmutableMap.of(disableNetherPortal.getName(), disableNetherPortal, disableEndPortal.getName(), disableEndPortal, disableEndGateway.getName(), disableEndGateway);

    public NoPortalsOptions(MinecraftServer server) {
        this.optionsFile = server.getSavePath(WorldSavePath.ROOT).resolve(NoPortals.MOD_ID + ".json");
        load();
    }

    public boolean isNetherPortalDisabled() {
        return disableNetherPortal.getValue();
    }

    public boolean isEndPortalDisabled() {
        return disableEndPortal.getValue();
    }

    public boolean isEndGatewayDisabled() {
        return disableEndGateway.getValue();
    }

    @Nullable
    public SimpleBooleanOption getDisablePortalOption(Block block) {
        return switch (block) {
            case NetherPortalBlock netherPortal -> disableNetherPortal;
            case EndPortalBlock endPortal -> disableEndPortal;
            case EndGatewayBlock endGateway -> disableEndGateway;
            default -> null;
        };
    }

    /**
     * Loads options from {@link #optionsFile} with Gson.
     */
    public void load() {
        if (!Files.isRegularFile(optionsFile)) {
            return;
        }
        JsonObject optionsJson;
        try (BufferedReader reader = Files.newBufferedReader(optionsFile)) {
            optionsJson = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            NoPortals.LOGGER.warn("Options file not found", e);
            return;
        } catch (IOException e) {
            NoPortals.LOGGER.error("Failed to load options", e);
            return;
        }
        options.entrySet().forEach(entry -> parseOption(optionsJson, entry));
    }

    /**
     * Parses a boolean option from a {@link JsonObject} with {@link Codec#BOOL}.
     *
     * @param optionsJson the {@link JsonObject} to parse from
     * @param option      the option to parse to
     */
    private void parseOption(JsonObject optionsJson, Map.Entry<String, SimpleBooleanOption> option) {
        DataResult<Boolean> dataResult = Codec.BOOL.parse(JsonOps.INSTANCE, optionsJson.get(option.getKey()));
        dataResult.error().ifPresent(error -> NoPortals.LOGGER.error("Error parsing option value {} for option {}: {}", optionsJson.get(option.getKey()), option.getKey(), error));
        dataResult.result().ifPresent(option.getValue()::setValue);
    }

    /**
     * Saves options to {@link #optionsFile} with Gson.
     */
    public void save() {
        JsonObject optionsJson = new JsonObject();
        options.entrySet().forEach(option -> saveOption(optionsJson, option));
        Path tempFile;
        try {
            tempFile = Files.createTempFile(optionsFile.getParent(), NoPortals.MOD_ID, ".json");
        } catch (IOException e) {
            NoPortals.LOGGER.error("Failed to save options file", e);
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            GSON.toJson(optionsJson, writer);
        } catch (IOException e) {
            NoPortals.LOGGER.error("Failed to write options", e);
        }
        Path backup = optionsFile.getParent().resolve(NoPortals.MOD_ID + ".json_old");
        Util.backupAndReplace(optionsFile, tempFile, backup);
    }

    /**
     * Saves a boolean option to a {@link JsonObject} with {@link Codec#BOOL}.
     *
     * @param optionsJson the {@link JsonObject} to save to
     * @param option      the option to save
     */
    private void saveOption(JsonObject optionsJson, Map.Entry<String, SimpleBooleanOption> option) {
        DataResult<JsonElement> dataResult = Codec.BOOL.encodeStart(JsonOps.INSTANCE, option.getValue().getValue());
        dataResult.error().ifPresent(error -> NoPortals.LOGGER.error("Error encoding option value {} for option {}: {}", option.getValue().getValue(), option.getKey(), error));
        dataResult.result().ifPresent(optionJson -> optionsJson.add(option.getKey(), optionJson));
    }
}
