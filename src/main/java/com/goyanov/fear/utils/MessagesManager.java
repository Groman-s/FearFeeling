package com.goyanov.fear.utils;

import org.bukkit.configuration.file.FileConfiguration;

public class MessagesManager
{
    private static FileConfiguration messagesConfig;
    public static void setMessagesConfig(FileConfiguration messagesConfig)
    {
        MessagesManager.messagesConfig = messagesConfig;
    }

    private static String pluginReloaded;
    private static String noPlayer;
    private static String noPermission;
    private static String noSuchFearStyle;
    private static String cantDoForOthers;
    private static String playerNotSpecified;

    public static String getPluginReloaded()
    {
        return pluginReloaded;
    }
    public static String getNoPlayer()
    {
        return noPlayer;
    }
    public static String getNoPermission()
    {
        return noPermission;
    }
    public static String getNoSuchFearStyle()
    {
        return noSuchFearStyle;
    }
    public static String getCantDoForOthers()
    {
        return cantDoForOthers;
    }
    public static String getPlayerNotSpecified()
    {
        return playerNotSpecified;
    }

    public static class Fear
    {
        private static String enabled;
        private static String disabled;
        private static String worldBlackListed;
        private static String cleared;
        private static String changed;
        private static String set;
        private static String showStyleChanged;
        private static String wrongCmdArgument;
        private static String playerDied;

        public static String getEnabled()
        {
            return enabled;
        }
        public static String getDisabled()
        {
            return disabled;
        }
        public static String getWorldBlackListed()
        {
            return worldBlackListed;
        }
        public static String getCleared()
        {
            return cleared;
        }
        public static String getChanged()
        {
            return changed;
        }
        public static String getSet()
        {
            return set;
        }
        public static String getShowStyleChanged()
        {
            return showStyleChanged;
        }
        public static String getWrongCmdArgument()
        {
            return wrongCmdArgument;
        }
        public static String getPlayerDied()
        {
            return playerDied;
        }
    }

    public static void reload()
    {
        pluginReloaded = messagesConfig.getString("plugin-reloaded").replace("&", "§");
        noPlayer = messagesConfig.getString("no-player").replace("&", "§");
        noPermission = messagesConfig.getString("no-permission").replace("&", "§");
        noSuchFearStyle = messagesConfig.getString("no-such-fearstyle").replace("&", "§");
        cantDoForOthers = messagesConfig.getString("cant-do-for-others").replace("&", "§");
        playerNotSpecified = messagesConfig.getString("player-not-specified").replace("&", "§");

        Fear.enabled = messagesConfig.getString("fear.enabled").replace("&", "§");
        Fear.disabled = messagesConfig.getString("fear.disabled").replace("&", "§");
        Fear.worldBlackListed = messagesConfig.getString("fear.world-blacklisted").replace("&", "§");
        Fear.cleared = messagesConfig.getString("fear.cleared").replace("&", "§");
        Fear.changed = messagesConfig.getString("fear.changed").replace("&", "§");
        Fear.set = messagesConfig.getString("fear.set").replace("&", "§");
        Fear.showStyleChanged = messagesConfig.getString("fear.showstyle-changed").replace("&", "§");
        Fear.wrongCmdArgument = messagesConfig.getString("fear.wrong-cmd-argument").replace("&", "§");
        Fear.playerDied = messagesConfig.getString("fear.player-died").replace("&", "§");
    }
}
