package com.goyanov.fear.utils;

import com.goyanov.fear.main.FearFeeling;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class IntegrationPlaceholders extends PlaceholderExpansion
{
    @Override
    public String getAuthor()
    {
        return FearFeeling.inst().getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier()
    {
        return "fearfeeling";
    }

    @Override
    public String getVersion()
    {
        return FearFeeling.inst().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier)
    {
        if (p == null) return "";

        if (identifier.equals("fear"))
            return Fear.getScaredPlayer(p) != null ? (int)Fear.getScaredPlayer(p).getCurrentFear() + "" : "0";// %fearfeeling_fear%

        else if (identifier.equals("enabled"))
            return Fear.getScaredPlayer(p) != null ? !Fear.getScaredPlayer(p).getFearBlocked() + "" : "false";// %fearfeeling_enabled%

        return null;
    }
}