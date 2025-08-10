package Memory;

import com.KYRLA_Ktiw.HousePlugin;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class HouseConfig {
    private final HousePlugin plugin;


    public HouseConfig(HousePlugin plugin) {
        this.plugin = plugin;
    }
    public void saveHouses() {
        FileConfiguration config = plugin.getConfig();
        config.set("houses", null);

        for (HouseMemory house : plugin.houses.values()) {
            String path = "houses." + house.getName();
            config.set(path + ".owner", house.getOwner() != null ? house.getOwner().toString() : "none");
            config.set(path + ".price", house.getPrice());
            config.set(path + ".pos1", house.getPos1().serialize());
            config.set(path + ".pos2", house.getPos2().serialize());
        }
        plugin.saveConfig();
    }

    public void loadHouses() {
        FileConfiguration config = plugin.getConfig();
        if (config.getConfigurationSection("houses") == null) {
            return;
        }

        for (String houseName : config.getConfigurationSection("houses").getKeys(false)) {
            UUID owner = null;
            String ownerString = config.getString("houses." + houseName + ".owner");
            if (ownerString != null && !ownerString.equals("none")) {
                owner = UUID.fromString(ownerString);
            }

            double price = config.getDouble("houses." + houseName + ".price");
            Location pos1 = Location.deserialize(config.getConfigurationSection("houses." + houseName + ".pos1").getValues(false));
            Location pos2 = Location.deserialize(config.getConfigurationSection("houses." + houseName + ".pos2").getValues(false));

            HouseMemory house = new HouseMemory(owner, houseName, price, pos1, pos2);
            plugin.houses.put(houseName, house);
        }
    }
}
