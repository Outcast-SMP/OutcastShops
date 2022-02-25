package me.illusion.outcastshops.Events;

import com.google.common.collect.Maps;
import me.illusion.outcastshops.OutcastShops;
import me.illusion.outcastshops.Util.Communication.Chat;
import me.illusion.outcastshops.Util.Config.ConfigData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Map;

public class SignChange implements Listener {
    private static Map<String, Integer> shops = Maps.newHashMap();

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player ply = e.getPlayer();
        String[] lines = e.getLines();

        String shopPermission = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.permissions.create");

        if (!ply.hasPermission(shopPermission)) {
            return;
        }

        if (lines.length < 3) {
            return;
        }

        String shopTitle = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.line-1");
        String shopSellMaterial = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.line-2");
        String shopCurrency = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.line-3");
        String shopAuthor = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.line-4");

        if (!lines[0].equals(ChatColor.stripColor(shopTitle))) {
            return;
        }

        int shopLimit = Integer.parseInt(new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.max-amount"));
        String shopLimitPermission = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.permissions.limit-bypass");

        if (!ply.hasPermission(shopLimitPermission)) {
            new Chat(ply, "&cYou don't have permission to create a shop.").message(true);
            return;
        }

        if (playerHasShop(ply) && getPlayerShopTotal(ply) > shopLimit) {
            new Chat(ply, "&cYou have reached your limit of created shops!").message(true);
            return;
        }

        if (shopSellMaterial.contains("#MATERIAL"))
            shopSellMaterial = shopSellMaterial.replace("#MATERIAL", lines[1]);

        if (shopCurrency.contains("#MATERIAL"))
            shopCurrency = shopCurrency.replace("#MATERIAL", lines[2]);

        if (shopAuthor.contains("#AUTHOR"))
            shopAuthor = shopAuthor.replace("#AUTHOR", ply.getName());

        e.setLine(0, ChatColor.translateAlternateColorCodes('&', shopTitle));
        e.setLine(1, ChatColor.translateAlternateColorCodes('&', shopSellMaterial));
        e.setLine(2, ChatColor.translateAlternateColorCodes('&', shopCurrency));
        e.setLine(3, ChatColor.translateAlternateColorCodes('&', shopAuthor));

        new Chat(ply, "&aShop successfully created!").message(true);
        addShop(ply);
    }

    public static Map<String, Integer> getShops() {
        return shops;
    }

    private void addShop(Player ply) {
        if (!playerHasShop(ply)) {
            getShops().put(ply.getName(), 1);
            return;
        }

        int getShopTotal = getPlayerShopTotal(ply);
        getShops().put(ply.getName(), (getShopTotal + 1));
    }

    private boolean playerHasShop(Player ply) {
        if (getShops().isEmpty())
            return false;

        return shops.containsKey(ply.getName());
    }

    private int getPlayerShopTotal(Player ply) {
        return shops.get(ply.getName());
    }
}
