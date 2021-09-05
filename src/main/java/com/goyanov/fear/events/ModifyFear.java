package com.goyanov.fear.events;

import com.goyanov.fear.instances.FearChangeEvent;
import com.goyanov.fear.instances.ScaredPlayer;
import com.goyanov.fear.utils.PluginSettings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ModifyFear implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChange(FearChangeEvent e)
    {
        if (!e.isCancelled())
        {
            ScaredPlayer sp = e.getScaredPlayer();
            if (sp == null) return;
            double delta = e.getFearChangeAmount();
            if (delta > 0 && e.getPlayer().hasPermission("FearFeeling.lessfear")) delta *= PluginSettings.FearSettings.getPermissionModifier();
            else if (delta < 0 && e.getPlayer().hasPermission("FearFeeling.quicksedation")) delta /= PluginSettings.FearSettings.getPermissionModifier();
            sp.addFinalFear(delta);
        }
    }
}
