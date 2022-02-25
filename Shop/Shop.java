public class Shop {
    private Player buyer = null;
    private Chest shopChest = null;

    public Shop(Player buyer) {
        this.buyer = buyer;
    }

    public Shop(Player buyer, Chest shopChest) {
        this.buyer = buyer;
        this.shopChest = shopChest;
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

    public boolean buyItem(String buyMat, String currencyMat) {
        int amount = getNumberFromSign(buyMat);
        int money = getNumberFromSign(currencyMat);

        if (getNumberFromSign(buyMat) != -1)
            buyMat = buyMat.replace(String.valueOf(amount), "").trim();
        else
            amount = 0;

        if (getNumberFromSign(currencyMat) != -1)
            currencyMat = currencyMat.replace(String.valueOf(money), "").trim();
        else
            money = 0;

        if (Material.valueOf(buyMat) == null || Material.valueOf(currencyMat) == null) {
            new Chat(buyer, "&cThis shop has invalid materials listed!").message(true);
            return false;
        }

        ItemStack buyItem = new ItemStack(Material.valueOf(buyMat));
        ItemStack currencyItem = new ItemStack(Material.valueOf(currencyMat));

        Inventory buyerInv = buyer.getInventory();
        Inventory shopInv = shopChest.getBlockInventory();

        int playerSpace = getFreeSpace(buyerInv, buyItem, 4);
        int shopSpace = getFreeSpace(shopInv, currencyItem, 3);

        if (shopInv.isEmpty()) {
            new Chat(buyer, "&cThis shop is currently empty!").message(true);
            return false;
        }

        if (getAvailableItems(shopInv, buyItem) < amount) {
            new Chat(buyer, "&cThe shop doesn't have enough of the requested items.").message(true);
            return false;
        }

        if (getAvailableItems(buyerInv, currencyItem) < money) {
            new Chat(buyer, "&cYou don't have enough material to purchase this item.").message(true);
            return false;
        }

        if (playerSpace < amount) {
            new Chat(buyer, "&cYour inventory is too full to purchase items!").message(true);
            return false;
        }

        if (shopSpace < money) {
            new Chat(buyer, "&cThis shop is full.").message(true);
            return false;
        }

        if (!takeItems(buyerInv, currencyItem, money)) {
            new LogMe("Cant take items from player...").Error();
            return false;
        }

        if (!takeItems(shopInv, buyItem, amount)) {
            new LogMe("Cant take items from shop...").Error();
            return false;
        }

        placeItemInInventory(shopInv, currencyItem, money);
        placeItemInInventory(buyerInv, buyItem, amount);
        buyer.updateInventory();
        new Chat(buyer, "&aTransaction successful!").message(true);
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
