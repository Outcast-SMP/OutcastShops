package me.illusion.outcastshops.Util.Communication;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import me.illusion.outcastshops.OutcastShops;
import me.illusion.outcastshops.Util.Config.ConfigData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Chat {
    private Player ply;
    private CommandSender sender;
    private String message;

    public Chat(CommandSender sender, String message) {
        this.sender = sender;
        this.message = IridiumColorAPI.process(message);
    }

    public Chat(Player ply, String message) {
        this.ply = ply;
        this.message = IridiumColorAPI.process(message);
    }

    public static String getPrefix() {
        return new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.prefix") + " ";
    }

    public void message(boolean bWatermark) {
        ply.sendMessage(bWatermark ? getPrefix() + message : message);
    }

    public void messageSender(boolean bWatermark) {
        sender.sendMessage(bWatermark ? getPrefix() + message : message);
    }
}
