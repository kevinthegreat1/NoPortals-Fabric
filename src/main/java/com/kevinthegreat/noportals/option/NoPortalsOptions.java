package com.kevinthegreat.noportals.option;

import com.google.gson.*;
import com.kevinthegreat.noportals.NoPortals;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;

import java.io.*;
import java.util.Optional;

public class NoPortalsOptions {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File optionsFile;
    private boolean disableNetherPortal;
    private boolean disableEndPortal;
    private boolean disableEndGateway;

    public NoPortalsOptions(MinecraftServer server) {
        this.optionsFile = server.getSavePath(WorldSavePath.ROOT).resolve(NoPortals.MOD_ID + ".json").toFile();
        load();
    }

    public boolean isNetherPortalDisabled() {
        return disableNetherPortal;
    }

    public boolean isEndPortalDisabled() {
        return disableEndPortal;
    }

    public boolean isEndGatewayDisabled() {
        return disableEndGateway;
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
        disableNetherPortal = parseOption(optionsJson, "disableNetherPortal").orElse(false);
        disableEndPortal = parseOption(optionsJson, "disableEndPortal").orElse(false);
        disableEndGateway = parseOption(optionsJson, "disableEndGateway").orElse(false);
    }

    /**
     * Parses a boolean option from a {@link JsonObject} with {@link Codec#BOOL}.
     *
     * @param optionsJson the {@link JsonObject} to parse from
     * @param name        the name of the option
     * @return the parsed value of the option
     */
    private Optional<Boolean> parseOption(JsonObject optionsJson, String name) {
        DataResult<Boolean> dataResult = Codec.BOOL.parse(JsonOps.INSTANCE, optionsJson.get(name));
        dataResult.error().ifPresent(error -> NoPortals.LOGGER.error("Error parsing option value " + optionsJson.get(name) + " for option " + name + ": " + error));
        return dataResult.result();
    }

    /**
     * Saves options to {@link #optionsFile} with Gson.
     */
    public void save() {
        JsonObject optionsJson = new JsonObject();
        saveOption(optionsJson, "disableNetherPortal", disableNetherPortal);
        saveOption(optionsJson, "disableEndPortal", disableEndPortal);
        saveOption(optionsJson, "disableEndGateway", disableEndGateway);
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
     * @param name        the name of the option
     * @param value       the value of the option
     */
    private void saveOption(JsonObject optionsJson, String name, boolean value) {
        DataResult<JsonElement> dataResult = Codec.BOOL.encodeStart(JsonOps.INSTANCE, value);
        dataResult.error().ifPresent(error -> NoPortals.LOGGER.error("Error encoding option value " + value + " for option " + name + ": " + error));
        dataResult.result().ifPresent(optionJson -> optionsJson.add(name, optionJson));
    }
}
