public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length <= 0) {
                new Chat(sender, "&cIncorrect command usage. Please use /shops [reload]").messageSender(true);
                return true;
            }

            String commandName = args[0];

            if (args.length == 1) {
                if (commandName.equals("reload")) {
                    OutcastShops.getInstance().config.reload();
                    new Chat(sender, "&aConfigs reloaded.").messageSender(true);
                    return true;
                }

                new Chat(sender, "&cIncorrect command usage. Please use /shops [reload]").messageSender(true);
                return true;
            }
            return true;
        }

        Player ply = (Player) sender;

        if (args.length <= 0) {
            new Chat(ply, "&cIncorrect command usage. Please use /shops [help | reload]").message(true);
            return true;
        }

        String commandName = args[0];

        if (args.length == 1) {
            if (commandName.equals("reload")) {
                if (!ply.hasPermission(new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.permissions.reload"))) {
                    new Chat(ply, "&cYou don't have permission to do this.").message(true);
                    return true;
                }

                OutcastShops.getInstance().config.reload();
                new Chat(ply, "&aConfigs reloaded.").message(true);
                return true;
            }

            if (commandName.equals("help")) {
                new Chat(ply, "&aOutcastShops v1.0").message(false);
                new Chat(ply, "&7Creator: &fIllusionDev").message(false);
                new Chat(ply, "&7Getting Started:").message(false);
                new Chat(ply, " ").message(false);
                new Chat(ply, "&7Attach a sign to a chest and enter the following format.").message(false);
                new Chat(ply, "&aLine 1: &f[OutcastShop]").message(false);
                new Chat(ply, "&aLine 2 (Buy Item): &f[AMOUNT] [Material]").message(false);
                new Chat(ply, "&aLine 3 (Currency Item): &f[AMOUNT] [Material]").message(false);
                return true;
            }

            new Chat(ply, "&cIncorrect command usage. Please use /shops [help | reload]").message(true);
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
