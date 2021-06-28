package com.goyanov.fear.events;

import com.goyanov.fear.instances.FearChangeEvent;
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
            e.getScaredPlayer().addFinalFear(e.getFearChangeAmount());
        }
    }
}
