package com.goyanov.fear.timers;

import com.goyanov.fear.instances.ScaredPlayer;
import com.goyanov.fear.main.FearFeeling;
import com.goyanov.fear.utils.PluginSettings;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HeartBeat extends BukkitRunnable
{
    private Player p;
    private ScaredPlayer sp;
    private byte counter = 0;

    private int beatTimer = 0;
    private int nextBeat = 4;

    private double moreBeatsBorder;

    public HeartBeat(Player p, ScaredPlayer sp)
    {
        this.p = p;
        this.sp = sp;
        this.moreBeatsBorder = PluginSettings.FearSettings.CriticalLevel.getBorder()+(100-PluginSettings.FearSettings.CriticalLevel.getBorder())/2;
        this.runTaskTimer(FearFeeling.inst(), 0, 1);
    }

    public void run()
    {
        double currentFear = sp.getCurrentFear();
        if (currentFear < PluginSettings.FearSettings.CriticalLevel.getBorder())
        {
            cancel();
            return;
        }
        if (beatTimer++ == nextBeat)
        {
            beatTimer = 0;
            if (currentFear >= moreBeatsBorder) nextBeat = 2;
            else nextBeat = 4;

            switch (counter++)
            {
                case 1:
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.5F, 0.7F);
                    break;
                case 2:
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.5F, 0.2F);
                    break;
                case 3:
                    counter = 0;
            }
        }
    }
}
