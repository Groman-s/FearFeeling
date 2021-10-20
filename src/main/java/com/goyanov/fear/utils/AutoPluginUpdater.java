package com.goyanov.fear.utils;

import com.goyanov.fear.main.FearFeeling;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutoPluginUpdater
{
    public static void update()
    {
        File configFile = new File(FearFeeling.inst().getDataFolder() + File.separator + "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        boolean configUpdated = false;

        if (!config.contains("fear-settings.fear-show-level"))
        {
            config.set("fear-settings.fear-show-level", 0);
            configUpdated = true;
        }
        if (config.contains("fear-settings.critical-level.effects"))
        {
            List<String> stableLines = config.getStringList("fear-settings.critical-level.effects");
            config.set("fear-settings.critical-level.effects", null);
            config.set("fear-settings.critical-level.stable-effects", stableLines);
            List<String> randomLines = new ArrayList<>();
            randomLines.add("CONFUSION:1:6");
            config.set("fear-settings.critical-level.random-effects", randomLines);
            configUpdated = true;
        }

        if (configUpdated)
        {
            try {
                config.save(configFile);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}
