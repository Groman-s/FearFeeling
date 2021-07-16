package com.goyanov.fear.utils;

import com.goyanov.fear.main.FearFeeling;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PluginSettings
{
    public static class FearSettings
    {
        private static byte lightLevelBorder;
        private static double fearIncreasePerTick;
        private static double fearDecreasePerTick;
        private static double permissionModifier;
        private static FearShowStyle defaultShowStyle;
        private static boolean considerNightVision;

        public static byte getLightLevelBorder()
        {
            return lightLevelBorder;
        }
        public static FearShowStyle getDefaultShowStyle()
        {
            return defaultShowStyle;
        }
        public static boolean getConsiderNightVision()
        {
            return considerNightVision;
        }
        public static double getFearIncreasePerTick()
        {
            return fearIncreasePerTick;
        }
        public static double getFearDecreasePerTick()
        {
            return fearDecreasePerTick;
        }
        public static double getPermissionModifier()
        {
            return permissionModifier;
        }

        public static class CriticalLevel
        {
            private static double border;
            private static List<PotionEffect> effects = new ArrayList<>();
            private static boolean playHeartbeatSounds;
            private static double healthDecreasePerTickWhileFull;
            private static double healthDecreaseWhileFullBorder;
            private static String titleLine1;
            private static String titleLine2;

            public static double getBorder()
            {
                return border;
            }
            public static List<PotionEffect> getEffects()
            {
                return effects;
            }
            public static boolean getPlayHeartbeatSounds()
            {
                return playHeartbeatSounds;
            }
            public static double getHealthDecreasePerTickWhileFull()
            {
                return healthDecreasePerTickWhileFull;
            }
            public static double getHealthDecreaseWhileFullBorder()
            {
                return healthDecreaseWhileFullBorder;
            }
            public static String getTitleLine1()
            {
                return titleLine1;
            }
            public static String getTitleLine2()
            {
                return titleLine2;
            }
        }
        public static class FearFromMonsters
        {
            private static double damageToFearPercent;
            private static double increaseWhileNearbyRadius;
            private static double fearIncreaseWhileNearbyPerTick;

            public static double getDamageToFearPercent()
            {
                return damageToFearPercent;
            }
            public static double getIncreaseWhileNearbyRadius()
            {
                return increaseWhileNearbyRadius;
            }
            public static double getFearIncreaseWhileNearbyPerTick()
            {
                return fearIncreaseWhileNearbyPerTick;
            }
        }
    }

    public static class BossBarSettings
    {
        private static String name;
        private static BarColor color;
        private static boolean enableFog;
        private static boolean enableDarkenSky;

        public static String getName()
        {
            return name;
        }
        public static BarColor getColor()
        {
            return color;
        }
        public static boolean getEnableFog()
        {
            return enableFog;
        }
        public static boolean getEnableDarkenSky()
        {
            return enableDarkenSky;
        }
    }

    public static class OptifineSettings
    {
        public static final ArrayList<Material> GLOWING_ITEMS = new ArrayList<>();

        private static boolean considerOptifine;
        private static double droppedItemsLightRadius;
        private static double burningMobsLightRadius;

        public static boolean getConsiderOptifine()
        {
            return considerOptifine;
        }
        public static double getBurningMobsLightRadius()
        {
            return burningMobsLightRadius;
        }
        public static double getDroppedItemsLightRadius()
        {
            return droppedItemsLightRadius;
        }
    }

    private static boolean checkAuthorized;
    public static boolean getCheckAuthorized()
    {
        return checkAuthorized;
    }

    private static final List<World> worldsBlacklist = new ArrayList<>();
    public static List<World> getWorldsBlacklist()
    {
        return worldsBlacklist;
    }

    private static String actionBarMessage;
    public static String getActionBarMessage()
    {
        return actionBarMessage;
    }

    public static void reload()
    {
        FileConfiguration config = FearFeeling.inst().getConfig();

        FearSettings.lightLevelBorder = (byte) config.getInt("fear-settings.light-level-border");
        FearSettings.fearIncreasePerTick = config.getDouble("fear-settings.fear-increase-per-second")/20;
        FearSettings.fearDecreasePerTick = config.getDouble("fear-settings.fear-decrease-per-second")/20;
        FearSettings.permissionModifier = config.getDouble("fear-settings.permission-modifier");
        String fearShowStyleName = config.getString("fear-settings.default-show-style").toUpperCase();
        try {
            FearSettings.defaultShowStyle = FearShowStyle.valueOf(fearShowStyleName);
        } catch (Exception e) {
            FearSettings.defaultShowStyle = FearShowStyle.NONE;
            FearFeeling.inst().getLogger().warning("No such FearShowStyle with name " + fearShowStyleName + ". Set to NONE.");
        }
        FearSettings.considerNightVision = config.getBoolean("fear-settings.consider-night-vision");

        FearSettings.CriticalLevel.border = config.getDouble("fear-settings.critical-level.border");
        FearSettings.CriticalLevel.effects.clear();
        List<String> effectsSettings = config.getStringList("fear-settings.critical-level.effects");
        for (String line : effectsSettings)
        {
            try {
                FearSettings.CriticalLevel.effects.add(new PotionEffect(PotionEffectType.getByName(line.split(":")[0]), 1728000, Integer.parseInt(line.split(":")[1])-1));
            } catch (Exception e) {
                FearFeeling.inst().getLogger().warning("<<effects>> Error in line " + line + ". Skipping.");
            }
        }
        FearSettings.CriticalLevel.playHeartbeatSounds = config.getBoolean("fear-settings.critical-level.play-heartbeat-sounds");
        FearSettings.CriticalLevel.healthDecreasePerTickWhileFull = config.getDouble("fear-settings.critical-level.health-decrease-per-second-while-full")/20;
        FearSettings.CriticalLevel.healthDecreaseWhileFullBorder = config.getDouble("fear-settings.critical-level.health-decrease-while-full-border");
        FearSettings.CriticalLevel.titleLine1 = config.getString("fear-settings.critical-level.title.line-1").replace("&", "§");
        FearSettings.CriticalLevel.titleLine2 = config.getString("fear-settings.critical-level.title.line-2").replace("&", "§");

        FearSettings.FearFromMonsters.damageToFearPercent = config.getDouble("fear-settings.fear-from-monsters.damage-to-fear-percent")/100;
        FearSettings.FearFromMonsters.increaseWhileNearbyRadius = config.getDouble("fear-settings.fear-from-monsters.increase-while-nearby.radius");
        FearSettings.FearFromMonsters.fearIncreaseWhileNearbyPerTick = config.getDouble("fear-settings.fear-from-monsters.increase-while-nearby.fear-increase-per-second")/20;

        BossBarSettings.name = config.getString("bossbar-settings.name").replace("&", "§");
        String barColorName = config.getString("bossbar-settings.color");
        try {
            BossBarSettings.color = BarColor.valueOf(barColorName);
        } catch (Exception e) {
            BossBarSettings.color = BarColor.WHITE;
            FearFeeling.inst().getLogger().warning("<<bossbar-settings>> No BarColor with color " + barColorName + ". Set to WHITE.");
        }
        BossBarSettings.enableFog = config.getBoolean("bossbar-settings.enable-fog");
        BossBarSettings.enableDarkenSky = config.getBoolean("bossbar-settings.enable-darken-sky");

        checkAuthorized = config.getBoolean("check-authorized");

        worldsBlacklist.clear();
        List<String> worldsNames = config.getStringList("worlds-blacklist");
        for (String worldName : worldsNames)
        {
            World w = Bukkit.getWorld(worldName);
            if (w != null) worldsBlacklist.add(w);
            else FearFeeling.inst().getLogger().warning("<<worlds-blacklist>> No world with name " + worldName + ". Skipping.");
        }

        OptifineSettings.considerOptifine = config.getBoolean("optifine.consider");
        OptifineSettings.droppedItemsLightRadius = config.getDouble("optifine.dropped-glowing-items-light-radius");
        OptifineSettings.burningMobsLightRadius = config.getDouble("optifine.burning-mobs-light-radius");
        OptifineSettings.GLOWING_ITEMS.clear();
        for (String line : config.getStringList("optifine.items"))
        {
            try {
                OptifineSettings.GLOWING_ITEMS.add(Material.valueOf(line.toUpperCase()));
            } catch (Exception e) {
                FearFeeling.inst().getLogger().warning("<<optifine-items>> No Material with name " + line);
            }
        }

        actionBarMessage = config.getString("actionbar-settings.message").replace("&", "§");
    }
}