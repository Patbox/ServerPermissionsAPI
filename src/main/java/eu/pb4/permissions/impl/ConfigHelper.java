package eu.pb4.permissions.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.permissions.PermissionsAPIMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class ConfigHelper {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final VanillaConfig getConfig() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "permissions.json");

        VanillaConfig configData;
        try {
            configData = configFile.exists() ? GSON.fromJson(new InputStreamReader(new FileInputStream(configFile), "UTF-8"), VanillaConfig.class) : new VanillaConfig();
        } catch (Exception e) {
            PermissionsAPIMod.LOGGER.error("Error occurred while loading config!");
            e.printStackTrace();
            configData = new VanillaConfig();
        }

        return configData;
    }

    public static final void saveConfig(Object config) {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "permissions.json");
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8"));
            writer.write(GSON.toJson(config));
            writer.close();
        } catch (Exception e) {
            PermissionsAPIMod.LOGGER.error("Error occurred while saving config!");
            e.printStackTrace();
        }
    }
}
