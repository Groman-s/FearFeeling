package com.goyanov.fear.events;

import com.goyanov.fear.instances.FearChangeEvent;
import com.goyanov.fear.main.FearFeeling;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ConsiderFlags implements Listener
{
    private static Object FEAR_GROW_FLAG;
    public static void setFearGrowFlag(Object fearGrowFlag)
    {
        FEAR_GROW_FLAG = fearGrowFlag;
    }

    private static Object FEAR_FALL_FLAG;
    public static void setFearFallFlag(Object fearFallFlag)
    {
        FEAR_FALL_FLAG = fearFallFlag;
    }

    public static boolean regionBlocksAbilityWithFlag(Player p, Object flag)
    {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        return !query.testState(localPlayer.getLocation(), localPlayer, (StateFlag) flag);
    }

    @EventHandler
    public void growAndFall(FearChangeEvent e)
    {
        if (FEAR_GROW_FLAG == null || FEAR_FALL_FLAG == null) return;
        try
        {
            if (e.getFearChangeAmount() > 0 && regionBlocksAbilityWithFlag(e.getPlayer(), FEAR_GROW_FLAG)) e.setCancelled(true);
            if (e.getFearChangeAmount() < 0 && regionBlocksAbilityWithFlag(e.getPlayer(), FEAR_FALL_FLAG)) e.setCancelled(true);
        }
        catch (Error error)
        {
            FearFeeling.inst().getLogger().warning("It seems you have reloaded your WorldGuard with a third-party plugin. Fear" +
            " flags will not be available until you completely restart your server.");
            FEAR_FALL_FLAG = null;
            FEAR_GROW_FLAG = null;
        }
    }
}
