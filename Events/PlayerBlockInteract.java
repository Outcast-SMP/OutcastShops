package me.illusion.outcastshops.Events;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import me.illusion.outcastshops.OutcastShops;
import me.illusion.outcastshops.Shop.Shop;
import me.illusion.outcastshops.Util.Communication.Chat;
import me.illusion.outcastshops.Util.Communication.LogMe;
import me.illusion.outcastshops.Util.Config.ConfigData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.UUID;

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
            
            if (((InventoryHolder) block.getState()).getInventory() instanceof DoubleChestInventory) {
                DoubleChestInventory doubleInv = (DoubleChestInventory) ((InventoryHolder) block.getState()).getInventory();

                Block leftChest = doubleInv.getLeftSide().getLocation().getBlock();
                Block rightChest = doubleInv.getRightSide().getLocation().getBlock();

                checkBlockFaces(ply, leftChest, e);
                checkBlockFaces(ply, rightChest, e);
                return;
            }

            checkBlockFaces(ply, block, e);
            return;
        }
        
        if (!(e.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        String shopBuyPermission = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.permissions.buy");

        if (!ply.hasPermission(shopBuyPermission)) {
            new Chat(ply, new ConfigData(OutcastShops.getInstance().chatMessages)
                    .getStringFromConfig("chat.error.insufficient-permission")).message(true);
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
            new Chat(ply, new ConfigData(OutcastShops.getInstance().chatMessages)
                    .getStringFromConfig("chat.error.owner-buy-item.message")).message(true);
            return;
        }

        Block blockBehindSign = block.getRelative(e.getBlockFace().getOppositeFace());

        if (blockBehindSign.getType() == Material.CHEST) {
            Chest shopChest = (Chest) blockBehindSign.getState();

            if (!new Shop(ply, shopChest).buyItem(IridiumColorAPI.stripColorFormatting(lines[1]), IridiumColorAPI.stripColorFormatting(lines[2]), true)) {
                return;
            }
            return;
        }

        if (blockBehindSign.getType() == Material.BARREL) {
            Barrel shopBarrel = (Barrel) blockBehindSign.getState();

            if (!new Shop(ply, shopBarrel).buyItem(IridiumColorAPI.stripColorFormatting(lines[1]), IridiumColorAPI.stripColorFormatting(lines[2]), false)) {
                return;
            }
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
            String shopAuthor = IridiumColorAPI.stripColorFormatting(lines[3]);
            String shopOpenPermission = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.permissions.open");

            if (shopAuthor.equals(ply.getName()) || ply.hasPermission(shopOpenPermission)) {
                return true;
            }

            new Chat(ply, new ConfigData(OutcastShops.getInstance().chatMessages)
                    .getStringFromConfig("chat.error.insufficient-permission")).message(true);
            return false;
        }

        return false;
    }
    
    private void checkBlockFaces(Player ply, Block block, PlayerInteractEvent e) {
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
    }
}
