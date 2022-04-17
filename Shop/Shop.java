package me.illusion.outcastshops.Shop;

import me.illusion.outcastshops.OutcastShops;
import me.illusion.outcastshops.Util.Communication.Chat;
import me.illusion.outcastshops.Util.Communication.LogMe;
import me.illusion.outcastshops.Util.Config.ConfigData;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Shop {
    private Player buyer = null;
    private Chest shopChest = null;
    private Barrel shopBarrel = null;

    public Shop(Player buyer) {
        this.buyer = buyer;
    }

    public Shop(Player buyer, Chest shopChest) {
        this.buyer = buyer;
        this.shopChest = shopChest;
    }

    public Shop(Player buyer, Barrel shopBarrel) {
        this.buyer = buyer;
        this.shopBarrel = shopBarrel;
    }

    public static int getNumberFromSign(String line) {
        StringBuilder finalString = new StringBuilder();

        for (char ch : line.toCharArray()) {
            if (Character.isDigit(ch)) {
                finalString.append(ch);
            }
        }

        if (finalString.length() <= 0)
            return -1;

        return Integer.parseInt(finalString.toString());
    }

    public static Material getMaterialFromSign(String line, int amount) {
        String finalText = null;

        if (line.contains(String.valueOf(amount))) {
            finalText = line.replace(String.valueOf(amount), "");
        }

        if (line.contains(" ")) {
            finalText = line.replace(" ", "");
        }

        if (finalText == null) {
            return null;
        }

        return Material.getMaterial(finalText);
    }

    public boolean buyItem(String buyMat, String currencyMat, boolean chestShop) {
        int amount = getNumberFromSign(buyMat);
        int money = getNumberFromSign(currencyMat);

        if (amount != -1)
            buyMat = buyMat.replace(String.valueOf(amount), "").trim();
        else
            amount = 0;

        if (money != -1)
            currencyMat = currencyMat.replace(String.valueOf(money), "").trim();
        else
            money = 0;

        if (Shop.getMaterialFromSign(buyMat, amount) == null) {
            new Chat(buyer, new ConfigData(OutcastShops.getInstance().chatMessages)
                    .getStringFromConfig("chat.error.buy.invalid-materials.message")).message(true);
            return false;
        }

        if (Shop.getMaterialFromSign(currencyMat, money) == null) {
            new Chat(buyer, new ConfigData(OutcastShops.getInstance().chatMessages)
                    .getStringFromConfig("chat.error.buy.invalid-materials.message")).message(true);
            return false;
        }

        ItemStack buyItem = new ItemStack(Material.valueOf(buyMat));
        ItemStack currencyItem = new ItemStack(Material.valueOf(currencyMat));

        Inventory buyerInv = buyer.getInventory();

        if (chestShop) {
            Inventory shopInv = shopChest.getBlockInventory();

            if (!authenticateShop(buyerInv, shopInv, money, amount, currencyItem, buyItem)) {
                return false;
            }

            placeItemInInventory(shopInv, currencyItem, money);
            placeItemInInventory(buyerInv, buyItem, amount);
        }
        else {
            Inventory shopInv = shopBarrel.getInventory();

            if (!authenticateShop(buyerInv, shopInv, money, amount, currencyItem, buyItem)) {
                return false;
            }

            placeItemInInventory(shopInv, currencyItem, money);
            placeItemInInventory(buyerInv, buyItem, amount);
        }

        buyer.updateInventory();
        new Chat(buyer, new ConfigData(OutcastShops.getInstance().chatMessages)
                .getStringFromConfig("chat.success.buy.message")).message(true);
        return true;
    }

    private boolean authenticateShop(Inventory buyingInv, Inventory shopInv, int currency, int itemAmount, ItemStack currencyItem, ItemStack buyItem) {
        int playerSpace = getFreeSpace(buyingInv, buyItem, 4);
        int shopSpace = getFreeSpace(shopInv, currencyItem, 3);

        if (shopInv.isEmpty()) {
            new Chat(buyer, new ConfigData(OutcastShops.getInstance().chatMessages)
                    .getStringFromConfig("chat.error.buy.empty.message")).message(true);
            return false;
        }

        if (getAvailableItems(shopInv, buyItem) < itemAmount) {
            new Chat(buyer, new ConfigData(OutcastShops.getInstance().chatMessages)
                    .getStringFromConfig("chat.error.buy.not-enough-items.message")).message(true);
            return false;
        }

        if (getAvailableItems(buyingInv, currencyItem) < currency) {
            new Chat(buyer, new ConfigData(OutcastShops.getInstance().chatMessages)
                    .getStringFromConfig("chat.error.buy.not-enough-items-player.message")).message(true);
            return false;
        }

        if (playerSpace < itemAmount) {
            new Chat(buyer, new ConfigData(OutcastShops.getInstance().chatMessages)
                    .getStringFromConfig("chat.error.buy.inventory-full-player.message")).message(true);
            return false;
        }

        if (shopSpace < currency) {
            new Chat(buyer, new ConfigData(OutcastShops.getInstance().chatMessages)
                    .getStringFromConfig("chat.error.buy.inventory-full.message")).message(true);
            return false;
        }

        if (!takeItems(buyingInv, currencyItem, currency)) {
            //new LogMe("Cant take items from player...").Error();
            return false;
        }

        if (!takeItems(shopInv, buyItem, itemAmount)) {
            //new LogMe("Cant take items from shop...").Error();
            return false;
        }

        return true;
    }

    private boolean takeItems(Inventory inv, ItemStack item, int amount) {
        Material mat = item.getType();
        Map<Integer, ? extends ItemStack> total = inv.all(mat);

        int found = 0;
        for (ItemStack stack : total.values())
        {
            found += stack.getAmount();
        }

        if (amount > found)
            return false;

        for (Integer i : total.keySet())
        {
            ItemStack stack = total.get(i);

            if (stack.isSimilar(item))
            {
                int removed = Math.min(amount, stack.getAmount());
                amount -= removed;

                if (stack.getAmount() == removed)
                    inv.setItem(i, null);
                else
                    stack.setAmount(stack.getAmount() - removed);

                if (amount <= 0)
                    break;
            }
        }
        return true;
    }

    private int getAvailableItems(Inventory inv, ItemStack item) {
        Material mat = item.getType();
        Map<Integer, ? extends ItemStack> total = inv.all(mat);

        int found = 0;
        for (ItemStack stack : total.values())
        {
            found += stack.getAmount();
        }

        return found;
    }

    private void placeItemInInventory(Inventory inv, ItemStack item, int amount) {
        if (item.getMaxStackSize() == 1 || item.getMaxStackSize() == 16) {
            item.setAmount(1);
            for (int i = 0; i < amount; i++) {
                inv.addItem(item);
            }
        }
        else {
            item.setAmount(amount);
            inv.addItem(item);
        }
    }

    private int getFreeSpace(Inventory inv, ItemStack item, int rows)
    {
        int spaceCount = 0;
        int invSize = (rows * 9) - 1;
        for (int i = 0; i <= invSize; i++)
        {
            ItemStack items = inv.getItem(i);

            if (items == null || items.getType() == Material.AIR)
            {
                spaceCount += item.getMaxStackSize();
            }
            else
            {
                if (items.isSimilar(item))
                {
                    spaceCount += Math.max(0, items.getMaxStackSize() - items.getAmount());
                }
            }
        }
        return spaceCount;
    }
}
