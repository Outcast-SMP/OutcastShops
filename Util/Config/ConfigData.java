public class ConfigData {
    private CreateConfig config;

    public ConfigData(CreateConfig config) { this.config = config; }

    public Material getMatFromConfig(String location) {
        if (!config.getConfig().contains(location)) {
            return Material.BARRIER;
        }

        if (config.getConfig().get(location) == null) {
            return Material.BARRIER;
        }

        if (Material.valueOf(config.getConfig().get(location).toString()) == null) {
            return Material.BARRIER;
        }

        return Material.valueOf(config.getConfig().get(location).toString());
    }

    public String getStringFromConfig(String location) {
        if (!config.getConfig().contains(location)) {
            return ChatColor.RED + "None";
        }

        if (config.getConfig().get(location) == null) {
            return ChatColor.RED + "None";
        }

        return ChatColor.translateAlternateColorCodes('&', config.getItem(location).toString());
    }
}
