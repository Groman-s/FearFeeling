package com.goyanov.fear.commands;

import com.goyanov.fear.utils.FearShowStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandFearTabCompleter implements TabCompleter
{
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
    {
        List<String> commands = new ArrayList<>();

        String arg1 = args[0].toLowerCase();

        if (args.length == 1)
        {
            if (sender.hasPermission("FearFeeling.fear.set") && "set".startsWith(arg1)) commands.add("set");
            if (sender.hasPermission("FearFeeling.fear.change") && "change".startsWith(arg1)) commands.add("change");
            if (sender.hasPermission("FearFeeling.fear.clear") && "clear".startsWith(arg1)) commands.add("clear");
            if (sender.hasPermission("FearFeeling.fear.toggle") && "toggle".startsWith(arg1)) commands.add("toggle");
            if (sender.hasPermission("FearFeeling.fear.showstyle") && "showstyle".startsWith(arg1)) commands.add("showstyle");
            if (sender.hasPermission("FearFeeling.fear.reload") && "reload".startsWith(arg1)) commands.add("reload");

            return commands;
        }
        else if (args.length == 2)
        {
            if (arg1.equals("showstyle"))
            {
                Arrays.stream(FearShowStyle.values()).map(Enum::name).filter(s -> s.startsWith(args[1].toUpperCase())).forEach(commands::add);
                return commands;
            }
            else if (arg1.equals("change") || arg1.equals("set")) return commands;
        }

        return null;
    }
}
