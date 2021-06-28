package com.goyanov.fear.events;

import com.goyanov.fear.instances.ScaredPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class InitializePlayerOnJoin implements Listener
{
    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        new ScaredPlayer(e.getPlayer());
    }
}
