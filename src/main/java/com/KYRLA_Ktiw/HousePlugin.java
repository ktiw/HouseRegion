package com.KYRLA_Ktiw;

import Command.HousePos;
import Command.TabCompleter;
import Events.HouseProtector;
import Memory.HouseConfig;
import Memory.HouseMemory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public final class HousePlugin extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    public HashMap<String, HouseMemory> houses = new HashMap<>();
    public HashMap<UUID, Location[]> housePositions = new HashMap<>();
    public HashMap <UUID, HouseMemory> pendingPurchases = new HashMap<>();
    private HouseConfig houseConfig;



    @Override
    public void onEnable() {
        getLogger().info("Плагин на недвижимость включен");
        this.saveDefaultConfig();

        if (!setupEconomy()) {
            log.severe(String.format("[%s] отсутствия Vault", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        houseConfig = new HouseConfig(this);
        houseConfig.loadHouses();
        getServer().getPluginManager().registerEvents(new HouseProtector(this), this);
        getCommand("house").setExecutor(new HousePos(this));
        getCommand("house").setTabCompleter(new TabCompleter());
    }

    @Override
    public void onDisable() {
        if (houseConfig != null) {
            houseConfig.saveHouses();
        }
        getLogger().info("Плагин на недвижимость выключен");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public HouseConfig getHouseConfig() {
        return houseConfig;
    }
}