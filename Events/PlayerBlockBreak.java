package me.illusion.outcastshops.Events;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
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

        if (blockMaterial.equals(Material.CHEST) || blockMaterial.equals(Material.BARREL)) {
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
                    return;
                }

                if (!isAuthor(shopSign, ply)) {
                    new Chat(ply, new ConfigData(OutcastShops.getInstance().chatMessages)
                            .getStringFromConfig("chat.error.remove-player-shop.message")).message(true);
                    e.setCancelled(true);
                }
            }
            return;
        }

        if (block.getState() instanceof Sign) {
            Sign shopSign = (Sign) block.getState();

            if (shopSign.getLines().length < 4) {
                return;
            }

            if (!isAuthor(shopSign, ply)) {
                new Chat(ply, new ConfigData(OutcastShops.getInstance().chatMessages)
                        .getStringFromConfig("chat.error.remove-player-shop.message")).message(true);
                e.setCancelled(true);
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

    private boolean isAuthor(Sign shopSign, Player ply) {
        String[] lines = shopSign.getLines();
        String shopTitle = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.line-1");

        if (lines[0].equals(shopTitle)) {
            String shopAuthor = IridiumColorAPI.stripColorFormatting(lines[3]);
            String shopDestroyPermission = new ConfigData(OutcastShops.getInstance().config).getStringFromConfig("shops.permissions.destroy");

            if (!shopAuthor.equals(ply.getName())) {
                if (ply.hasPermission(shopDestroyPermission)) {
                    new SignChange().removeShop(shopAuthor);
                    return true;
                }

                return false;
            }

            new SignChange().removeShop(ply.getName());
        }

        return true;
    }
}
