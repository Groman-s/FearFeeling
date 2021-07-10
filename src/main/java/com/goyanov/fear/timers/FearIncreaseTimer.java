package com.goyanov.fear.timers;

import com.goyanov.fear.instances.FearChangeEvent;
import com.goyanov.fear.instances.ScaredPlayer;
import com.goyanov.fear.utils.FearShowStyle;
import com.goyanov.fear.utils.PluginSettings;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class FearIncreaseTimer extends BukkitRunnable
{
    private Player p;
    private ScaredPlayer sp;

    public FearIncreaseTimer(ScaredPlayer sp)
    {
        this.sp = sp;
        this.p = sp.getBukkitPlayer();
        if (sp.getCurrentFear() >= PluginSettings.FearSettings.CriticalLevel.getBorder()) effectsGiven = true;
        regLoc = p.getLocation();
    }

    private Location regLoc;
    private boolean effectsGiven;
    private double fearChangeAmount;

    @Override
    public void run()
    {
        if (p.isOnline())
        {
            if (sp.getFearBlocked()) return;

            if (!sp.isLoggedIn())
            {
                if (!PluginSettings.getCheckAuthorized() || !p.getWorld().equals(regLoc.getWorld()) || p.getLocation().distance(regLoc) > 3)
                {
                    sp.setLoggedIn(true);
                }
                else
                {
                    return;
                }
            }

            fearChangeAmount = 0;
            double currentFear = sp.getCurrentFear();
            double finalFear = sp.getFinalFear();

            sp.getFearBossbar().setProgress(currentFear/100);

            if (sp.getFearShowStyle() == FearShowStyle.ACTIONBAR)
            {
                // заменить на конфигурабельное сообщение
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(PluginSettings.getActionBarMessage().replace("%f", (int)currentFear+"")).create());
            }

            if (currentFear >= PluginSettings.FearSettings.CriticalLevel.getBorder() && !effectsGiven)
            {
                sp.giveCriticalEffects();
                p.sendTitle(PluginSettings.FearSettings.CriticalLevel.getTitleLine1(), PluginSettings.FearSettings.CriticalLevel.getTitleLine2(), 20,30,20);
                if (PluginSettings.FearSettings.CriticalLevel.getPlayHeartbeatSounds()) new HeartBeat(p, sp);
                effectsGiven = true;
            }
            else if (currentFear < PluginSettings.FearSettings.CriticalLevel.getBorder() && effectsGiven)
            {
                sp.removeCriticalEffects();
                effectsGiven = false;
            }
            if (currentFear == 100)
            {
                if (!p.isDead() && !p.isInvulnerable() && p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)
                {
                    double newHealth = p.getHealth()-PluginSettings.FearSettings.CriticalLevel.getHealthDecreasePerTickWhileFull();
                    if (newHealth <= 0) newHealth = 0;
                    p.setHealth(newHealth);
                }
            }

            double delta = finalFear - currentFear;
            delta = delta < -1 ? -1 : delta > 1 ? 1 : delta;
            sp.addCurrentFear(delta);

            byte lightLevel = p.getEyeLocation().getBlock().getLightLevel();

            if (lightLevel >= PluginSettings.FearSettings.getLightLevelBorder() || (PluginSettings.FearSettings.getConsiderNightVision() && p.hasPotionEffect(PotionEffectType.NIGHT_VISION)))
            {
                fearChangeAmount -= PluginSettings.FearSettings.getFearDecreasePerTick();
            }
            else
            {
                fearChangeAmount += PluginSettings.FearSettings.getFearIncreasePerTick();
            }

            double radius = PluginSettings.FearSettings.FearFromMonsters.getIncreaseWhileNearbyRadius();
            p.getNearbyEntities(radius,radius,radius).stream().filter(ent -> ent instanceof Monster).findAny().ifPresent(invoke -> fearChangeAmount += PluginSettings.FearSettings.FearFromMonsters.getFearIncreaseWhileNearbyPerTick());

            Bukkit.getPluginManager().callEvent(new FearChangeEvent(p, sp, finalFear, fearChangeAmount));
        }
        else
        {
            sp.remove();
            cancel();
        }
    }
}
