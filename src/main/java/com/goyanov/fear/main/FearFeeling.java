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
import java.sql.SQLException;

public class FearFeeling extends JavaPlugin
{
    private static FearFeeling plugin;
    public static FearFeeling inst()
    {
        return plugin;
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
            for (ScaredPlayer sp : Fear.SCARED_PLAYERS.values())
            {
                sp.remove();
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

        PluginManager pm = getServer().getPluginManager();
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
