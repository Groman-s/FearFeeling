package com.goyanov.fear.commands;

import com.goyanov.fear.instances.ScaredPlayer;
import com.goyanov.fear.main.FearFeeling;
import com.goyanov.fear.utils.Fear;
import com.goyanov.fear.utils.FearShowStyle;
import com.goyanov.fear.utils.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFear implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        // fear switch [player]
        // fear clear [player]

        // fear set <value> [player]
        // fear change <value> [player]
        // fear showstyle <value> [player]

        if (args.length == 0)
        {
            sender.sendMessage(MessagesManager.Fear.getWrongCmdArgument());
            return true;
        }

        // fear reload
        if (args.length == 1 && args[0].equalsIgnoreCase("reload"))
        {
            if (sender.hasPermission("FearFeeling.fear.reload"))
            {
                FearFeeling.inst().unloadPlugin();
                FearFeeling.inst().loadPlugin();
                sender.sendMessage(MessagesManager.getPluginReloaded());
            }
            else
            {
                sender.sendMessage(MessagesManager.getNoPermission());
            }
            return true;
        }

        if (args.length <= 3)
        {
            String param = args[0].toLowerCase();

            if (!sender.hasPermission("FearFeeling.fear." + param))
            {
                sender.sendMessage(MessagesManager.getNoPermission());
                return true;
            }

            Player target;
            int playerParamIndex = 2;
            Object value = 0;

            if (param.equals("toggle") || param.equals("clear"))
            {
                playerParamIndex = 1;
            }
            else if (param.equals("set") || param.equals("change"))
            {
                try {
                    value = Double.parseDouble(args[1]);
                } catch (Exception e) {
                    sender.sendMessage("§cThe second argument will be a number!");
                    return true;
                }
            }
            else if (param.equals("showstyle"))
            {
                try {
                    value = FearShowStyle.valueOf(args[1].toUpperCase());
                } catch (Exception e) {
                    sender.sendMessage(MessagesManager.getNoSuchFearStyle());
                    return true;
                }
            }

            if (args.length == playerParamIndex+1)
            {
                if (!sender.hasPermission("FearFeeling.fear." + param + ".others"))
                {
                    sender.sendMessage(MessagesManager.getCantDoForOthers());
                    return true;
                }
                target = Bukkit.getPlayer(args[playerParamIndex]);
                if (target == null)
                {
                    sender.sendMessage(MessagesManager.getNoPlayer());
                    return true;
                }
            }
            else
            {
                if (sender instanceof Player)
                {
                    target = (Player) sender;
                }
                else
                {
                    sender.sendMessage(MessagesManager.getPlayerNotSpecified());
                    return true;
                }
            }

            ScaredPlayer sp = Fear.getScaredPlayer(target);

            if (param.equals("toggle"))
            {
                if (sp.toggleFear())
                {
                    if (sp.getFearBlocked()) sender.sendMessage(MessagesManager.Fear.getDisabled());
                    else sender.sendMessage(MessagesManager.Fear.getEnabled());
                }
                else
                {
                    sender.sendMessage(MessagesManager.Fear.getWorldBlackListed());
                }
            }
            else if (param.equals("clear"))
            {
                sp.clearFear();
                sender.sendMessage(MessagesManager.Fear.getCleared());
            }
            else if (param.equals("set"))
            {
                sp.setFinalFear((double)value);
                sender.sendMessage(MessagesManager.Fear.getSet().replace("%f", value+""));
            }
            else if (param.equals("change"))
            {
                sp.addFinalFear((double)value);
                sender.sendMessage(MessagesManager.Fear.getChanged().replace("%f", value+""));
            }
            else if (param.equals("showstyle"))
            {
                sp.changeFearStyle((FearShowStyle)value);
                sender.sendMessage(MessagesManager.Fear.getShowStyleChanged().replace("%s", ((FearShowStyle)value).name()));
            }
            else
            {
                sender.sendMessage(MessagesManager.Fear.getWrongCmdArgument());
                return true;
            }
        }
        return true;
    }
}
