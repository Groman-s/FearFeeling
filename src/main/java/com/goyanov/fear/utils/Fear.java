package com.goyanov.fear.utils;

import com.goyanov.fear.instances.FearChangeEvent;
import com.goyanov.fear.instances.ScaredPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Fear
{
    public static final HashMap<Player, ScaredPlayer> SCARED_PLAYERS = new HashMap<>();

    public static ScaredPlayer getScaredPlayer(Player p)
    {
        return SCARED_PLAYERS.get(p);
    }

    public static double getForPlayer(Player p)
    {
        return SCARED_PLAYERS.get(p).getCurrentFear();
    }

    public static void change(Player p, double amount)
    {
        Bukkit.getPluginManager().callEvent(new FearChangeEvent(p, amount));
    }
}
