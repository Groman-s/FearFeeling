package com.goyanov.fear.events;

import com.goyanov.fear.utils.Fear;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class FearClearOnRespawn implements Listener
{
    @EventHandler
    public void onDeath(PlayerRespawnEvent e)
    {
        Fear.getScaredPlayer(e.getPlayer()).clearFear();
    }
}
