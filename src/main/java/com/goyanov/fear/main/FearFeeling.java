package com.goyanov.fear.main;

import com.goyanov.fear.commands.CommandFear;
import com.goyanov.fear.commands.CommandFearTabCompleter;
import com.goyanov.fear.events.*;
import com.goyanov.fear.instances.ScaredPlayer;
import com.goyanov.fear.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class FearFeeling extends JavaPlugin
{
    private static FearFeeling plugin;
    public static FearFeeling inst()
    {
        return plugin;
    }

    public void onLoad()
    {
        try
        {
            Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Method getInstance = worldGuardClass.getDeclaredMethod("getInstance");
            Object worldGuardInstance = getInstance.invoke(worldGuardClass);
            Method getFlagRegistry = worldGuardInstance.getClass().getDeclaredMethod("getFlagRegistry");
            Object registry = getFlagRegistry.invoke(worldGuardInstance);

            Class<?> stateFlagClass = Class.forName("com.sk89q.worldguard.protection.flags.StateFlag");
            Constructor<?> stateFlagClassConstructor = stateFlagClass.getConstructor(String.class, boolean.class);

            Object fearGrowFlag = stateFlagClassConstructor.newInstance("fear-grow", true);
            Object fearFallFlag = stateFlagClassConstructor.newInstance("fear-fall", true);

            Method register = registry.getClass().getDeclaredMethod("register", fearGrowFlag.getClass().getSuperclass());
            try
            {
                register.invoke(registry, fearGrowFlag);
                ConsiderFlags.setFearGrowFlag(fearGrowFlag);
                register.invoke(registry, fearFallFlag);
                ConsiderFlags.setFearFallFlag(fearFallFlag);
            } catch (InvocationTargetException e)
            {
                Method get = registry.getClass().getDeclaredMethod("get", String.class);
                ConsiderFlags.setFearGrowFlag(get.invoke(registry, "fear-grow"));
                ConsiderFlags.setFearFallFlag(get.invoke(registry, "fear-fall"));
            }
        }
        catch (Exception ignored) { }
    }

    public void loadPlugin()
    {
        File config = new File(getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) saveDefaultConfig();
        reloadConfig();

        File messagesConfigFile = new File(getDataFolder() + File.separator + "messages.yml");
        if (!messagesConfigFile.exists()) saveResource("messages.yml", false);
        MessagesManager.setMessagesConfig(YamlConfiguration.loadConfiguration(messagesConfigFile));
        MessagesManager.reload();

        PluginSettings.reload();

        Bukkit.getOnlinePlayers().forEach(ScaredPlayer::new);
    }

    public void unloadPlugin()
    {
        try
        {
            SQLManager.getMainConnection().setAutoCommit(false);
            for (Object sp : Fear.SCARED_PLAYERS.values().toArray())
            {
                ((ScaredPlayer)sp).remove();
            }
            SQLManager.getMainConnection().commit();
        } catch (SQLException throwables) { throwables.printStackTrace(); }
    }

    @Override
    public void onEnable()
    {
        plugin = this;

        SQLManager.openConnectionWithDB();
        SQLManager.createFearTable();

        loadPlugin();

        getCommand("fear").setExecutor(new CommandFear());
        getCommand("fear").setTabCompleter(new CommandFearTabCompleter());

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
        {
            Bukkit.getConsoleSender().sendMessage("§8[§cFearFeeling§8] §9PlaceholderAPI support enabled! Use %fearfeeling_fear% to get the player fear level.");
            Bukkit.getConsoleSender().sendMessage("§8[§cFearFeeling§8] §9PlaceholderAPI support enabled! Use %fearfeeling_enabled% to check if a player fear enabled.");
            new IntegrationPlaceholders().register();
        }

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null)
        {
            Bukkit.getConsoleSender().sendMessage("§8[§cFearFeeling§8] §bWorldGuard support enabled! Use the fear-fall flag to block the fear falling in a region.");
            Bukkit.getConsoleSender().sendMessage("§8[§cFearFeeling§8] §bWorldGuard support enabled! Use the fear-grow flag to block the fear growing in a region.");
        }

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ConsiderFlags(), this);
        pm.registerEvents(new DeathMessages(), this);
        pm.registerEvents(new FearClearOnRespawn(), this);
        pm.registerEvents(new FearFromMonstersDamage(), this);
        pm.registerEvents(new InitializePlayerOnJoin(), this);
        pm.registerEvents(new ModifyFear(), this);
        pm.registerEvents(new NoMilkWhileCritical(), this);
        pm.registerEvents(new WorldBlackList(), this);

        new Metrics(this, 12053);
    }

    @Override
    public void onDisable()
    {
        unloadPlugin();
        SQLManager.closeConnectionWithDB();
    }
}
