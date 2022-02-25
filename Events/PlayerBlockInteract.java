public class PlayerBlockInteract implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player ply = e.getPlayer();
        Block block = e.getClickedBlock();
        Action action = e.getAction();

        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        if (block.getType().equals(Material.CHEST)) {
            if (getBlockFromFace(block).isEmpty()) {
                return;
            }

            for (int i = 0; i < getBlockFromFace(block).size(); i++) {
                Block attachedBlock = getBlockFromFace(block).get(i);

                if (!(attachedBlock.getState() instanceof Sign)) {
                    continue;
                }

                if (!canOpenChest(ply, attachedBlock)) {
                    e.setCancelled(true);
                    return;
                }
            }
            return;
        }
        
        if (!(e.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        String shopBuyPermission = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.permissions.buy");

        if (!ply.hasPermission(shopBuyPermission)) {
            new Chat(ply, "&cYou don't have permission to do this.").message(true);
            return;
        }

        Sign shopSign = (Sign) block.getState();

        if (shopSign.getLines().length < 4) {
            return;
        }

        String[] lines = shopSign.getLines();
        String shopTitle = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.line-1");

        if (!lines[0].equals(shopTitle)) {
            return;
        }

        if (ChatColor.stripColor(lines[3]).equals(ply.getName())) {
            new Chat(ply, "&cYou can't buy from your own shop.").message(true);
            return;
        }

        Block blockBehindSign = block.getRelative(e.getBlockFace().getOppositeFace());

        if (blockBehindSign.getType() != Material.CHEST) {
            return;
        }

        Chest shopChest = (Chest) blockBehindSign.getState();

        if (!new Shop(ply, shopChest).buyItem(ChatColor.stripColor(lines[1]), ChatColor.stripColor(lines[2]))) {
            return;
        }
    }

    private ArrayList<Block> getBlockFromFace(Block block) {
        BlockFace face = null;
        ArrayList<Block> getBlocks = new ArrayList<>();

        if (block.getRelative(BlockFace.EAST) != null)
            getBlocks.add(block.getRelative(BlockFace.EAST));

        if (block.getRelative(BlockFace.NORTH) != null)
            getBlocks.add(block.getRelative(BlockFace.NORTH));

        if (block.getRelative(BlockFace.WEST) != null)
            getBlocks.add(block.getRelative(BlockFace.WEST));

        if (block.getRelative(BlockFace.SOUTH) != null)
            getBlocks.add(block.getRelative(BlockFace.SOUTH));

        return getBlocks;
    }

    private boolean canOpenChest(Player ply, Block block) {
        Sign shopSign = (Sign) block.getState();

        if (shopSign.getLines().length < 4) {
            return false;
        }

        String[] lines = shopSign.getLines();
        String shopTitle = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.line-1");

        if (lines[0].equals(shopTitle)) {
            String shopAuthor = ChatColor.stripColor(lines[3]);
            String shopOpenPermission = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.permissions.open");

            if (shopAuthor.equals(ply.getName()) || ply.hasPermission(shopOpenPermission)) {
                return true;
            }

            new Chat(ply, "&cYou don't have permission to view the contents of this shop.").message(true);
            return false;
        }

        return false;
    }
}
