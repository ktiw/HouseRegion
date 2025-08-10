package Events;

import com.KYRLA_Ktiw.HousePlugin;
import Memory.HouseMemory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class HouseProtector implements Listener {

    private final HousePlugin plugin;

    public HouseProtector(HousePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.isOp()) {
            return;
        }

        Location blockLocation = event.getBlock().getLocation();

        for (HouseMemory house : plugin.houses.values()) {
            if (isInsideRegion(blockLocation, house.getPos1(), house.getPos2())) {
                UUID owner = house.getOwner();

                if (owner == null) {
                    player.sendMessage(ChatColor.RED + "Эта территория не куплена, вы не можете здесь ломать!");
                    event.setCancelled(true);
                    return;
                }

                if (!owner.equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Вы не можете ломать блоки в этом доме!");
                    event.setCancelled(true);
                }
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.isOp()) {
            return;
        }

        Location blockLocation = event.getBlock().getLocation();

        for (HouseMemory house : plugin.houses.values()) {
            if (isInsideRegion(blockLocation, house.getPos1(), house.getPos2())) {
                UUID owner = house.getOwner();

                if (owner == null) {
                    player.sendMessage(ChatColor.RED + "Эта территория не куплена, вы не можете здесь строить!");
                    event.setCancelled(true);
                    return;
                }

                if (!owner.equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Вы не можете строить в этом доме!");
                    event.setCancelled(true);
                }
                return;
            }
        }
    }

    private boolean isInsideRegion(Location loc, Location pos1, Location pos2) {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
}