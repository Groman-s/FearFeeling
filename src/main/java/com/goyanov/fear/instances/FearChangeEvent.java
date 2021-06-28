package com.goyanov.fear.instances;

import com.goyanov.fear.utils.Fear;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FearChangeEvent extends Event implements Cancellable
{
    ///============================================================================================================================================

    private Player player;
    private ScaredPlayer scaredPlayer;

    private double oldFearAmount;
    private double fearChangeAmount;

    ///============================================================================================================================================

    public FearChangeEvent(Player player, ScaredPlayer scaredPlayer, double oldFearAmount, double fearChangeAmount)
    {
        this.player = player;
        this.scaredPlayer = scaredPlayer;
        this.oldFearAmount = oldFearAmount;
        this.fearChangeAmount = fearChangeAmount;
    }

    public FearChangeEvent(Player player, double fearChangeAmount)
    {
        this.player = player;
        this.scaredPlayer = Fear.getScaredPlayer(player);
        this.oldFearAmount = scaredPlayer.getFinalFear();
        this.fearChangeAmount = fearChangeAmount;
    }

    ///============================================================================================================================================

    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    public HandlerList getHandlers()
    {
        return handlers;
    }

    private boolean cancelled;
    public boolean isCancelled()
    {
        return cancelled;
    }
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public Player getPlayer()
    {
        return player;
    }
    public ScaredPlayer getScaredPlayer()
    {
        return scaredPlayer;
    }

    public double getOldFearAmount()
    {
        return oldFearAmount;
    }
    public double getFearChangeAmount()
    {
        return fearChangeAmount;
    }
    public void setFearChangeAmount(double fearChangeAmount)
    {
        this.fearChangeAmount = fearChangeAmount;
    }

    ///============================================================================================================================================
}
