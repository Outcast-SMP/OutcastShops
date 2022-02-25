public class Chat {
    public static String watermark = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.prefix") + " ";

    private Player ply;
    private CommandSender sender;
    private String message;

    public Chat(CommandSender sender, String message) {
        this.sender = sender;
        this.message = ChatColor.translateAlternateColorCodes('&', message);
    }

    public Chat(Player ply, String message) {
        this.ply = ply;
        this.message = ChatColor.translateAlternateColorCodes('&', message);
    }

    public void message(boolean bWatermark) {
        ply.sendMessage(bWatermark ? watermark + message : message);
    }

    public void messageSender(boolean bWatermark) {
        sender.sendMessage(bWatermark ? watermark + message : message);
    }
}
