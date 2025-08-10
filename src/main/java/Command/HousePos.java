package Command;

import com.KYRLA_Ktiw.HousePlugin;
import Memory.HouseMemory;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HousePos implements CommandExecutor {
    private final HousePlugin plugin;

    public HousePos(HousePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Команда для игроков!");
            return true;
        }
        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("house")) {
            // Команда для установки точки 1
            if (args.length == 2 && args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("pos1")) {
                if (!plugin.housePositions.containsKey(player.getUniqueId())) {
                    plugin.housePositions.put(player.getUniqueId(), new Location[2]);
                }
                plugin.housePositions.get(player.getUniqueId())[0] = player.getLocation();
                player.sendMessage(ChatColor.GREEN + "Точка 1 установлена!");
                return true;
            }

            // Команда для установки точки 2
            if (args.length == 2 && args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("pos2")) {
                if (!plugin.housePositions.containsKey(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Сначала установите точку 1!");
                    return true;
                }
                plugin.housePositions.get(player.getUniqueId())[1] = player.getLocation();
                player.sendMessage(ChatColor.GREEN + "Точка 2 установлена!");
                return true;
            }

            // Команда для создания дома
            if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
                if (!plugin.housePositions.containsKey(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Сначала установите две точки!");
                    return true;
                }
                Location[] positions = plugin.housePositions.get(player.getUniqueId());
                if (positions[0] == null || positions[1] == null) {
                    player.sendMessage(ChatColor.RED + "Сначала установите две точки!");
                    return true;
                }

                String houseName = args[1];
                double housePrice;
                try {
                    housePrice = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Неверный формат цены!");
                    return true;
                }

                HouseMemory newHouse = new HouseMemory(null, houseName, housePrice, positions[0], positions[1]);
                plugin.houses.put(houseName, newHouse);
                plugin.getHouseConfig().saveHouses();
                plugin.housePositions.remove(player.getUniqueId());

                player.sendMessage(ChatColor.GREEN + "Дом '" + houseName + "' успешно создан за " + housePrice + " рублей!");
                return true;
            }


            if (args.length == 1 && args[0].equalsIgnoreCase("buy")) {
                for (HouseMemory house : plugin.houses.values()) {
                    if (isInsideRegion(player.getLocation(), house.getPos1(), house.getPos2())) {
                        if (house.getOwner() == null) {
                            if (HousePlugin.getEconomy().has(player, house.getPrice())) {
                                plugin.pendingPurchases.put(player.getUniqueId(), house);

                                TextComponent message = new TextComponent(ChatColor.YELLOW + "Вы собираетесь купить дом '" + house.getName() + "' за " + house.getPrice() + " рублей. ");
                                TextComponent confirm = new TextComponent(ChatColor.GREEN + "[ПОДТВЕРДИТЬ]");
                                confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/house confirm"));
                                TextComponent cancel = new TextComponent(ChatColor.RED + " [ОТКАЗАТЬСЯ]");
                                cancel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/house cancel"));

                                player.sendMessage(ChatColor.YELLOW + "-------------------------------");
                                player.spigot().sendMessage(message, confirm, cancel);
                                player.sendMessage(ChatColor.YELLOW + "-------------------------------");

                                return true;
                            } else {
                                player.sendMessage(ChatColor.RED + "У вас недостаточно средств для покупки этого дома.");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Этот дом уже куплен.");
                            return true;
                        }
                    }
                }
                player.sendMessage(ChatColor.RED + "Вы не находитесь внутри ни одного дома.");
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
                if (plugin.pendingPurchases.containsKey(player.getUniqueId())) {
                    HouseMemory houseToBuy = plugin.pendingPurchases.get(player.getUniqueId());
                    HousePlugin.getEconomy().withdrawPlayer(player, houseToBuy.getPrice());
                    houseToBuy.setOwner(player.getUniqueId());
                    plugin.getHouseConfig().saveHouses();
                    plugin.pendingPurchases.remove(player.getUniqueId());

                    player.sendMessage(ChatColor.GREEN + "Поздравляем! Вы купили дом '" + houseToBuy.getName() + "' за " + houseToBuy.getPrice() + " рублей!");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "У вас нет активной покупки для подтверждения.");
                    return true;
                }
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("cancel")) {
                if (plugin.pendingPurchases.containsKey(player.getUniqueId())) {
                    plugin.pendingPurchases.remove(player.getUniqueId());
                    player.sendMessage(ChatColor.RED + "Вы отказались от покупки.");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "У вас нет активной покупки для отмены.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInsideRegion(Location playerLoc, Location pos1, Location pos2) {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return playerLoc.getX() >= minX && playerLoc.getX() <= maxX &&
                playerLoc.getY() >= minY && playerLoc.getY() <= maxY &&
                playerLoc.getZ() >= minZ && playerLoc.getZ() <= maxZ;
    }
}