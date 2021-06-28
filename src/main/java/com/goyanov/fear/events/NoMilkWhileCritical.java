package com.goyanov.fear.events;

import com.goyanov.fear.instances.ScaredPlayer;
import com.goyanov.fear.main.FearFeeling;
import com.goyanov.fear.utils.Fear;
import com.goyanov.fear.utils.PluginSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class NoMilkWhileCritical implements Listener
{
    @EventHandler
    public void onMilkDrink(PlayerItemConsumeEvent e)
    {
        if (e.getItem().getType() == Material.MILK_BUCKET)
        {
            ScaredPlayer sp = Fear.getScaredPlayer(e.getPlayer());
            if (sp.getCurrentFear() >= PluginSettings.FearSettings.CriticalLevel.getBorder())
            {
                Bukkit.getScheduler().scheduleSyncDelayedTask(FearFeeling.inst(), sp::giveCriticalEffects, 1);
            }
        }
    }
}
