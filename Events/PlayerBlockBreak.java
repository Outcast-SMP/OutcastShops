package me.illusion.outcastshops.Events;

import me.illusion.outcastshops.OutcastShops;
import me.illusion.outcastshops.Util.Communication.Chat;
import me.illusion.outcastshops.Util.Config.ConfigData;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerBlockBreak implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player ply = e.getPlayer();
        UUID pID = ply.getUniqueId();
        Block block = e.getBlock();
        Material blockMaterial = block.getType();

        if (blockMaterial.equals(Material.CHEST)) {
            if (getBlockFromFace(block).isEmpty()) {
                return;
            }

            for (int i = 0; i < getBlockFromFace(block).size(); i++) {
                Block attachedBlock = getBlockFromFace(block).get(i);

                if (!(attachedBlock.getState() instanceof Sign)) {
                    continue;
                }

                Sign shopSign = (Sign) attachedBlock.getState();

                if (shopSign.getLines().length < 4) {
                    continue;
                }

                String[] lines = shopSign.getLines();
                String shopTitle = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.line-1");

                if (lines[0].equals(shopTitle)) {
                    String shopAuthor = ChatColor.stripColor(lines[3]);
                    String shopDestroyPermission = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.permissions.destroy");

                    if (shopAuthor.equals(ply.getName())) {
                        new SignChange().removeShop(ply.getName());
                        return;
                    }

                    if (ply.hasPermission(shopDestroyPermission)) {
                        new SignChange().removeShop(shopAuthor);
                        return;
                    }

                    new Chat(ply, "&cYou can't break another player's shop.").message(true);
                    e.setCancelled(true);
                    return;
                }
            }
            return;
        }

        if (block.getState() instanceof Sign) {
            Sign shopSign = (Sign) block.getState();

            if (shopSign.getLines().length < 4) {
                return;
            }

            String[] lines = shopSign.getLines();
            String shopTitle = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.line-1");

            if (lines[0].equals(shopTitle)) {
                String shopAuthor = ChatColor.stripColor(lines[3]);
                String shopDestroyPermission = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.permissions.destroy");

                if (shopAuthor.equals(ply.getName())) {
                    new SignChange().removeShop(ply.getName());
                    return;
                }

                if (ply.hasPermission(shopDestroyPermission)) {
                    new SignChange().removeShop(shopAuthor);
                    return;
                }

                new Chat(ply, "&cYou can't break another player's shop.").message(true);
                e.setCancelled(true);
            }
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
}
