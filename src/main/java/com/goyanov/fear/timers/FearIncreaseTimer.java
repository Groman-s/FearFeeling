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
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

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

    private boolean checkOptifine(Player p)
    {
        if (!PluginSettings.OptifineSettings.getConsiderOptifine()) return false;

        if (PluginSettings.OptifineSettings.GLOWING_ITEMS.contains(p.getInventory().getItemInMainHand().getType())) return true;
        if (PluginSettings.OptifineSettings.GLOWING_ITEMS.contains(p.getInventory().getItemInOffHand().getType())) return true;

        double droppedItemsLightRadius = PluginSettings.OptifineSettings.getDroppedItemsLightRadius();
        Optional<Item> dropped = p.getNearbyEntities(droppedItemsLightRadius,droppedItemsLightRadius,droppedItemsLightRadius).stream().filter(ent -> ent instanceof Item).map(ent -> (Item) ent).filter(drop -> PluginSettings.OptifineSettings.GLOWING_ITEMS.contains(drop.getItemStack().getType())).findAny();
        if (dropped.isPresent()) return true;

        double burningMobsLightRadius = PluginSettings.OptifineSettings.getBurningMobsLightRadius();
        Optional<Entity> burning = p.getNearbyEntities(burningMobsLightRadius,burningMobsLightRadius,burningMobsLightRadius).stream().filter(ent -> ent.getFireTicks() > 0).findAny();
        if (burning.isPresent()) return true;

        return false;
    }

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
            sp.getFearBossbar().setTitle(PluginSettings.BossBarSettings.getName().replace("%f", (int)currentFear+""));

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
                    double border = PluginSettings.FearSettings.CriticalLevel.getHealthDecreaseWhileFullBorder();
                    if (p.getHealth() > border)
                    {
                        double newHealth = p.getHealth()-PluginSettings.FearSettings.CriticalLevel.getHealthDecreasePerTickWhileFull();
                        if (newHealth < border)
                        {
                            newHealth = border;
                        }
                        if (newHealth <= 0)
                        {
                            newHealth = 0;
                            sp.setDiedOfFright(true);
                        }
                        p.setHealth(newHealth);
                    }
                }
            }

            double delta = finalFear - currentFear;
            delta = delta < -1 ? -1 : delta > 1 ? 1 : delta;
            sp.addCurrentFear(delta);

            byte lightLevel = p.getEyeLocation().getBlock().getLightLevel();

            if (lightLevel >= PluginSettings.FearSettings.getLightLevelBorder() || checkOptifine(p) || (PluginSettings.FearSettings.getConsiderNightVision() && p.hasPotionEffect(PotionEffectType.NIGHT_VISION)))
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
