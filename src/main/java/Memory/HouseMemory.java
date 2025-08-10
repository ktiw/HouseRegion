package Memory;

import org.bukkit.Location;
import java.util.UUID;

public class HouseMemory {
    private UUID owner;
    private String name;
    private double price;
    private Location pos1;
    private Location pos2;

    public HouseMemory(UUID owner, String name, double price, Location pos1, Location pos2) {
        this.owner = owner;
        this.name = name;
        this.price = price;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }
}