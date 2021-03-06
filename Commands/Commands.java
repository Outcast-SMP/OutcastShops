package me.illusion.outcastshops.Commands;

import me.illusion.outcastshops.OutcastShops;
import me.illusion.outcastshops.Util.Communication.Chat;
import me.illusion.outcastshops.Util.Config.ConfigData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String errorMsg = new ConfigData(OutcastShops.getInstance().chatMessages)
                .getStringFromConfig("chat.error.incorrect-command-use");

        if (!(sender instanceof Player)) {
            if (args.length <= 0) {
                new Chat(sender, errorMsg).messageSender(true);
                return true;
            }

            String commandName = args[0];

            if (args.length == 1) {
                if (commandName.equals("reload")) {
                    OutcastShops.getInstance().config.reload();
                    OutcastShops.getInstance().chatMessages.reload();

                    new Chat(sender, new ConfigData(OutcastShops.getInstance().chatMessages)
                            .getStringFromConfig("chat.success.reload")).messageSender(true);
                    return true;
                }

                new Chat(sender, errorMsg).messageSender(true);
                return true;
            }
            return true;
        }

        Player ply = (Player) sender;

        if (args.length <= 0) {
            new Chat(ply, errorMsg).message(true);
            return true;
        }

        String commandName = args[0];

        if (args.length == 1) {
            if (commandName.equals("reload")) {
                if (!ply.hasPermission(new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.permissions.reload"))) {
                    new Chat(ply, new ConfigData(OutcastShops.getInstance().chatMessages)
                            .getStringFromConfig("chat.error.insufficient-permission")).message(true);
                    return true;
                }

                OutcastShops.getInstance().config.reload();
                OutcastShops.getInstance().chatMessages.reload();

                new Chat(ply, new ConfigData(OutcastShops.getInstance().chatMessages)
                        .getStringFromConfig("chat.success.reload")).message(true);
                return true;
            }

            if (commandName.equals("help")) {
                new Chat(ply, " ").message(false);
                new Chat(ply, "&aOutcastShops v2.0").message(false);
                new Chat(ply, "&7Creator: &fIllusionDev").message(false);
                new Chat(ply, "&7Getting Started:").message(false);
                new Chat(ply, " ").message(false);
                new Chat(ply, "&7Attach a sign to a chest and enter the following format.").message(false);
                new Chat(ply, "&aLine 1: &f[OutcastShop]").message(false);
                new Chat(ply, "&aLine 2 (Buy Item): &f[AMOUNT] [Material]").message(false);
                new Chat(ply, "&aLine 3 (Currency Item): &f[AMOUNT] [Material]").message(false);
                new Chat(ply, " ").message(false);
                return true;
            }

            new Chat(ply, errorMsg).message(true);
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String commandName = command.getName();

        if (commandName.equals("shops")) {
            String subCommandName = args[0];
            List<String> itemList = new ArrayList<>();

            if (args.length == 1) {
                itemList.add("help");
                itemList.add("reload");
            }

            return itemList;
        }
        return null;
    }
}
