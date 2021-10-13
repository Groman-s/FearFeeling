package com.goyanov.fear.instances;

import com.goyanov.fear.main.FearFeeling;
import com.goyanov.fear.timers.FearIncreaseTimer;
import com.goyanov.fear.timers.HeartBeat;
import com.goyanov.fear.utils.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class ScaredPlayer
{
    ///============================================================================================================================================

    private final Player bukkitPlayer;

    private boolean fearBlocked;
    private boolean isLoggedIn = false;
    private boolean diedOfFright = false;

    private double currentFear;
    private double finalFear;

    private BossBar fearBossbar;
    private FearShowStyle fearShowStyle;

    private FearIncreaseTimer timer;

    private boolean fearDisabledByWorldBlackList;

///============================================================================================================================================

    public ScaredPlayer(Player bukkitPlayer)
    {
        this.bukkitPlayer = bukkitPlayer;

        Object[] datas = SQLManager.loadPlayerFromDB(bukkitPlayer);
        currentFear = (double) datas[0];
        finalFear = (double) datas[1];
        fearBlocked = (boolean) datas[2];
        fearDisabledByWorldBlackList = (boolean) datas[3];
        fearShowStyle = bukkitPlayer.hasPermission("FearFeeling.fear.showstyle") ? (FearShowStyle) datas[4] : PluginSettings.FearSettings.getDefaultShowStyle();

        if (currentFear >= PluginSettings.FearSettings.CriticalLevel.getBorder())
        {
            if (PluginSettings.FearSettings.CriticalLevel.getPlayHeartbeatSounds()) new HeartBeat(bukkitPlayer, this);
            giveCriticalEffects();
        }

        BarFlag[] flags = new BarFlag[2];
        if (PluginSettings.BossBarSettings.getEnableDarkenSky()) flags[0] = BarFlag.DARKEN_SKY;
        if (PluginSettings.BossBarSettings.getEnableFog()) flags[1] = BarFlag.CREATE_FOG;
        fearBossbar = Bukkit.createBossBar(PluginSettings.BossBarSettings.getName().replace("%f", (int)currentFear+""), PluginSettings.BossBarSettings.getColor(), BarStyle.SEGMENTED_10, flags);
        fearBossbar.setProgress(currentFear/100);
        fearBossbar.addPlayer(bukkitPlayer);

        if (!PluginSettings.getWorldsBlacklist().contains(bukkitPlayer.getWorld()))
        {
            if (fearDisabledByWorldBlackList)
            {
                fearBlocked = false;
                fearDisabledByWorldBlackList = false;
            }
        }
        else
        {
            if (!fearBlocked && !fearDisabledByWorldBlackList)
            {
                fearDisabledByWorldBlackList = true;
                toggleFear();
            }
        }

        sendFearBar();

        timer = new FearIncreaseTimer(this);
        timer.runTaskTimer(FearFeeling.inst(), 1, 1);

        Fear.SCARED_PLAYERS.put(bukkitPlayer, this);
    }

    ///============================================================================================================================================

    public void sendFearBar()
    {
        if (fearBlocked)
        {
            fearBossbar.setVisible(false);
            return;
        }
        if (fearShowStyle == FearShowStyle.NONE)
        {
            fearBossbar.setVisible(false);
            return;
        }
        if (fearShowStyle == FearShowStyle.BOSSBAR)
        {
            fearBossbar.setVisible(currentFear >= PluginSettings.FearSettings.getFearShowLevel());
            return;
        }
        if (fearShowStyle == FearShowStyle.ACTIONBAR)
        {
            fearBossbar.setVisible(false);
            if (currentFear >= PluginSettings.FearSettings.getFearShowLevel())
                bukkitPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(PluginSettings.getActionBarMessage().replace("%f", (int)currentFear+"")).create());
            else
                bukkitPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("").create());
            return;
        }
    }

    public void remove()
    {
        SQLManager.savePlayerToDB(bukkitPlayer);
        timer.cancel();
        currentFear = 0;
        finalFear = 0;
        fearBossbar.setVisible(false);
        fearBossbar.removeAll();
        Fear.SCARED_PLAYERS.remove(bukkitPlayer);
        removeCriticalEffects();
    }

    public void addFinalFear(double fear)
    {
        if (fearBlocked) return;

        double newFear = finalFear + fear;
        if (newFear > 100) newFear = 100;
        else if (newFear < 0) newFear = 0;
        finalFear = newFear;
    }

    public void addCurrentFear(double fear)
    {
        if (fearBlocked) return;

        double newFear = currentFear + fear;
        if (newFear > 100) newFear = 100;
        else if (newFear < 0) newFear = 0;
        currentFear = newFear;
    }

    public void setFinalFear(double fear)
    {
        if (fearBlocked) return;

        if (fear > 100) fear = 100;
        else if (fear < 0) fear = 0;
        finalFear = fear;
    }

    public void clearFear()
    {
        currentFear = 0;
        finalFear = 0;
        fearBossbar.setProgress(0);
        removeCriticalEffects();
    }

    public void giveCriticalEffects()
    {
        bukkitPlayer.addPotionEffects(PluginSettings.FearSettings.CriticalLevel.getEffects());
    }

    public void removeCriticalEffects()
    {
        PluginSettings.FearSettings.CriticalLevel.getEffects().stream().map(PotionEffect::getType).forEach(bukkitPlayer::removePotionEffect);
    }

    public boolean toggleFear()
    {
        if (fearBlocked && PluginSettings.getWorldsBlacklist().contains(bukkitPlayer.getWorld())) return false;

        clearFear();

        fearBlocked = !fearBlocked;

        sendFearBar();

        return true;
    }

    public void changeFearStyle(FearShowStyle style)
    {
        this.fearShowStyle = style;
        sendFearBar();
    }

    public Player getBukkitPlayer()
    {
        return bukkitPlayer;
    }

    public double getCurrentFear()
    {
        return currentFear;
    }

    public double getFinalFear()
    {
        return finalFear;
    }

    public BossBar getFearBossbar()
    {
        return fearBossbar;
    }

    public FearShowStyle getFearShowStyle()
    {
        return fearShowStyle;
    }

    public boolean getFearBlocked()
    {
        return fearBlocked;
    }

    public boolean isLoggedIn()
    {
        return isLoggedIn;
    }

    public boolean getDiedOfFright()
    {
        return diedOfFright;
    }

    public void setDiedOfFright(boolean diedOfFright)
    {
        this.diedOfFright = diedOfFright;
    }

    public void setLoggedIn(boolean loggedIn)
    {
        isLoggedIn = loggedIn;
    }

    public boolean getFearDisabledByWorldBlackList()
    {
        return fearDisabledByWorldBlackList;
    }

    public void setFearDisabledByWorldBlackList(boolean fearDisabledByWorldBlackList) { this.fearDisabledByWorldBlackList = fearDisabledByWorldBlackList; }

    ///============================================================================================================================================
}