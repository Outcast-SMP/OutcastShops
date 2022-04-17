package me.illusion.outcastshops.Util.Config;

import me.illusion.outcastshops.Events.SignChange;

public class ConfigState {
    private CreateConfig config;

    public ConfigState(CreateConfig config) {
        this.config = config;
    }

    public void loadConfig() {
        this.config.loadMapInteger("shops.amount", SignChange.getShops());
    }

    public void saveConfig() {
        if (!SignChange.getShops().isEmpty())
            this.config.saveMap("shops.amount", SignChange.getShops());
    }

    public void configDefaults() {
        this.config.setDefault("shops.prefix", "&8[&aShops&8]");
        this.config.setDefault("shops.max-amount", "4");

        this.config.setDefault("shops.line-1", "&9&l[OutcastShop]");
        this.config.setDefault("shops.line-2", "&a#MATERIAL");
        this.config.setDefault("shops.line-3", "&c#MATERIAL");
        this.config.setDefault("shops.line-4", "&f#AUTHOR");

        this.config.setDefault("shops.permissions.create", "oc.shops.create");
        this.config.setDefault("shops.permissions.destroy", "oc.shops.destroy");
        this.config.setDefault("shops.permissions.open", "oc.shops.open");
        this.config.setDefault("shops.permissions.buy", "oc.shops.buy");
        this.config.setDefault("shops.permissions.limit-bypass", "oc.shops.limit.bypass");
        this.config.setDefault("shops.permissions.reload", "oc.shops.reload");
    }

    public void configChatMessages() {
        this.config.setDefault("chat.error.incorrect-command-use", "&cIncorrect command usage. Please use /shops [help | reload]");
        this.config.setDefault("chat.error.insufficient-permission", "&cYou don't have permission to do this.");

        this.config.setDefault("chat.success.reload", "&aConfigs reloaded.");

        this.config.setDefault("chat.error.buy.invalid-materials.message", "&cThis shop has invalid materials listed!");
        this.config.setDefault("chat.error.buy.empty.message", "&cThis shop is currently empty!");
        this.config.setDefault("chat.error.buy.not-enough-items.message", "&cThe shop doesn't have enough of the requested items.");
        this.config.setDefault("chat.error.buy.not-enough-items-player.message", "&cYou don't have enough material to purchase this item.");
        this.config.setDefault("chat.error.buy.inventory-full.message", "&cThis shop is full.");
        this.config.setDefault("chat.error.buy.inventory-full-player.message", "&cYour inventory is too full to purchase items!");

        this.config.setDefault("chat.error.remove-player-shop.message", "&cYou can't break another player's shop.");
        this.config.setDefault("chat.error.owner-buy-item.message", "&cYou can't buy from your own shop.");
        this.config.setDefault("chat.error.max-shop-created.message", "&cYou have reached your limit of created shops!");

        this.config.setDefault("chat.success.buy.message", "&aTransaction successful!");
        this.config.setDefault("chat.success.add-shop.message", "&aShop successfully created!");
    }
}
