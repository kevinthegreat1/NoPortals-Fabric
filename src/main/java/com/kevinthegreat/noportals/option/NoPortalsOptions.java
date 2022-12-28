package com.kevinthegreat.noportals.option;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.kevinthegreat.noportals.NoPortals;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;

import java.io.*;
import java.util.Map;

public class NoPortalsOptions {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File optionsFile;
    public final SimpleBooleanOption disableNetherPortal = new SimpleBooleanOption();
    public final SimpleBooleanOption disableEndPortal = new SimpleBooleanOption();
    public final SimpleBooleanOption disableEndGateway = new SimpleBooleanOption();
    public final Map<String, SimpleBooleanOption> options = ImmutableMap.of("disableNetherPortal", disableNetherPortal, "disableEndPortal", disableEndPortal, "disableEndGateway", disableEndGateway);

    public NoPortalsOptions(MinecraftServer server) {
        this.optionsFile = server.getSavePath(WorldSavePath.ROOT).resolve(NoPortals.MOD_ID + ".json").toFile();
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

    /**
     * Loads options from {@link #optionsFile} with Gson.
     */
    public void load() {
        if (!optionsFile.exists()) {
            return;
        }
        JsonObject optionsJson;
        try (BufferedReader reader = new BufferedReader(new FileReader(optionsFile))) {
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
        dataResult.error().ifPresent(error -> NoPortals.LOGGER.error("Error parsing option value " + optionsJson.get(option.getKey()) + " for option " + option.getKey() + ": " + error));
        dataResult.result().ifPresent(option.getValue()::setValue);
    }

    /**
     * Saves options to {@link #optionsFile} with Gson.
     */
    public void save() {
        JsonObject optionsJson = new JsonObject();
        options.entrySet().forEach(option -> saveOption(optionsJson, option));
        File tempFile;
        try {
            tempFile = File.createTempFile(NoPortals.MOD_ID, ".json", optionsFile.getParentFile());
        } catch (IOException e) {
            NoPortals.LOGGER.error("Failed to save options file", e);
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            GSON.toJson(optionsJson, writer);
        } catch (IOException e) {
            NoPortals.LOGGER.error("Failed to write options", e);
        }
        File backup = new File(optionsFile.getParentFile(), NoPortals.MOD_ID + ".json_old");
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
        dataResult.error().ifPresent(error -> NoPortals.LOGGER.error("Error encoding option value " + option.getValue().getValue() + " for option " + option.getKey() + ": " + error));
        dataResult.result().ifPresent(optionJson -> optionsJson.add(option.getKey(), optionJson));
    }
}
