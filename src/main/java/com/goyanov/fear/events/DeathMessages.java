package com.goyanov.fear.events;

import com.goyanov.fear.utils.Fear;
import com.goyanov.fear.utils.MessagesManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessages implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e)
    {
        if (Fear.getScaredPlayer(e.getEntity()).getCurrentFear() == 100)
        {
            e.setDeathMessage(MessagesManager.Fear.getPlayerDied().replace("%p", e.getEntity().getName()));
        }
    }
}
