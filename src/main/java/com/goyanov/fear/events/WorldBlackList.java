package com.goyanov.fear.events;

import com.goyanov.fear.instances.ScaredPlayer;
import com.goyanov.fear.utils.Fear;
import com.goyanov.fear.utils.PluginSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldBlackList implements Listener
{
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e)
    {
        Player p = e.getPlayer();
        ScaredPlayer sp = Fear.getScaredPlayer(p);
        if (sp.getFearBlocked() && !sp.getFearDisabledByWorldBlackList()) return;
        if (PluginSettings.getWorldsBlacklist().contains(p.getWorld()))
        {
            if (!sp.getFearBlocked())
            {
                sp.toggleFear();
                sp.setFearDisabledByWorldBlackList(true);
            }
        }
        else
        {
            if (sp.getFearBlocked() && sp.getFearDisabledByWorldBlackList())
            {
                sp.toggleFear();
                sp.setFearDisabledByWorldBlackList(false);
            }
        }
    }
}
