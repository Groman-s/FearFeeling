package com.goyanov.fear.events;

import com.goyanov.fear.instances.FearChangeEvent;
import com.goyanov.fear.utils.PluginSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FearFromMonstersDamage implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e)
    {
        if (e.isCancelled()) return;

        if (e.getDamager() instanceof Monster && e.getEntity() instanceof Player)
        {
            Bukkit.getPluginManager().callEvent(new FearChangeEvent((Player) e.getEntity(), e.getDamage()* PluginSettings.FearSettings.FearFromMonsters.getDamageToFearPercent()));
        }
    }
}
