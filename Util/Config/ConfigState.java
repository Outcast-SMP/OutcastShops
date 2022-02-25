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
}
