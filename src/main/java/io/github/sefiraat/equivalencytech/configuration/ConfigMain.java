package io.github.sefiraat.equivalencytech.configuration;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.misc.DatabaseManager;
import io.github.sefiraat.equivalencytech.misc.DatabaseManager.ChestData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class ConfigMain {

    private static File cchestStore;
    private static FileConfiguration cchestStoreConfig;
    private static File dchestStore;
    private static FileConfiguration dchestStoreConfig;
    private static File cchest;
    private static FileConfiguration cchestConfig;
    private static File dchest;
    private static FileConfiguration dchestConfig;
    private static File playerEMC;
    private static FileConfiguration playerEMCConfig;
    private static File playerUnlocks;
    private static FileConfiguration playerUnlocksConfig;

    public static void setup(EMCShopMiragEdge plugin) {
        // 创建和加载配置文件
        setupFiles(plugin);

        // 如果启用数据库，尝试迁移数据
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            migrateToDatabase(plugin);
        }
    }

    private static void setupFiles(EMCShopMiragEdge plugin) {
        // 初始化配置文件
        playerEMC = new File(plugin.getDataFolder(), "playerEMC.yml");
        playerUnlocks = new File(plugin.getDataFolder(), "playerUnlocks.yml");
        cchestStore = new File(plugin.getDataFolder(), "cchest-store.yml");
        dchestStore = new File(plugin.getDataFolder(), "dchest-store.yml");
        cchest = new File(plugin.getDataFolder(), "cchest.yml");
        dchest = new File(plugin.getDataFolder(), "dchest.yml");

        // 确保文件存在
        if (!playerEMC.exists()) {
            plugin.saveResource("playerEMC.yml", false);
        }
        if (!playerUnlocks.exists()) {
            plugin.saveResource("playerUnlocks.yml", false);
        }
        if (!cchestStore.exists()) {
            plugin.saveResource("cchest-store.yml", false);
        }
        if (!dchestStore.exists()) {
            plugin.saveResource("dchest-store.yml", false);
        }
        if (!cchest.exists()) {
            plugin.saveResource("cchest.yml", false);
        }
        if (!dchest.exists()) {
            plugin.saveResource("dchest.yml", false);
        }

        // 加载配置
        playerEMCConfig = YamlConfiguration.loadConfiguration(playerEMC);
        playerUnlocksConfig = YamlConfiguration.loadConfiguration(playerUnlocks);
        cchestStoreConfig = YamlConfiguration.loadConfiguration(cchestStore);
        dchestStoreConfig = YamlConfiguration.loadConfiguration(dchestStore);
        cchestConfig = YamlConfiguration.loadConfiguration(cchest);
        dchestConfig = YamlConfiguration.loadConfiguration(dchest);
    }

    public static void saveAll() {
        try {
            playerEMCConfig.save(playerEMC);
            playerUnlocksConfig.save(playerUnlocks);
            cchestStoreConfig.save(cchestStore);
            dchestStoreConfig.save(dchestStore);
            cchestConfig.save(cchest);
            dchestConfig.save(dchest);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save configuration files", e);
        }
    }

    // ==================== 玩家EMC操作 ====================
    public static double getPlayerEMC(EMCShopMiragEdge plugin, Player player) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            return plugin.getDatabaseManager().getPlayerEMC(player.getUniqueId());
        } else {
            return playerEMCConfig.getDouble(player.getUniqueId().toString(), 0.0);
        }
    }

    public static void setPlayerEMC(EMCShopMiragEdge plugin, Player player, double emc) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            plugin.getDatabaseManager().setPlayerEMC(player.getUniqueId(), emc);
        } else {
            playerEMCConfig.set(player.getUniqueId().toString(), emc);
            savePlayerEMC();
        }
    }

    public static void addPlayerEMC(EMCShopMiragEdge plugin, Player player, double amount) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            plugin.getDatabaseManager().addPlayerEMC(player.getUniqueId(), amount);
        } else {
            double current = getPlayerEMC(plugin, player);
            setPlayerEMC(plugin, player, current + amount);
        }
    }

    private static void savePlayerEMC() {
        try {
            playerEMCConfig.save(playerEMC);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save playerEMC.yml", e);
        }
    }

    // ==================== 玩家解锁物品操作 ====================
    public static List<String> getPlayerUnlockedItems(EMCShopMiragEdge plugin, Player player) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            return plugin.getDatabaseManager().getUnlockedItems(player.getUniqueId());
        } else {
            return playerUnlocksConfig.getStringList(player.getUniqueId().toString());
        }
    }

    public static void setPlayerUnlockedItems(EMCShopMiragEdge plugin, Player player, List<String> items) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下需要先清空再添加
            List<String> current = plugin.getDatabaseManager().getUnlockedItems(player.getUniqueId());
            for (String item : current) {
                plugin.getDatabaseManager().removeUnlockedItem(player.getUniqueId(), item);
            }
            for (String item : items) {
                plugin.getDatabaseManager().addUnlockedItem(player.getUniqueId(), item);
            }
        } else {
            playerUnlocksConfig.set(player.getUniqueId().toString(), items);
            savePlayerUnlocks();
        }
    }

    public static void addPlayerUnlockedItem(EMCShopMiragEdge plugin, Player player, String itemId) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            plugin.getDatabaseManager().addUnlockedItem(player.getUniqueId(), itemId);
        } else {
            List<String> items = getPlayerUnlockedItems(plugin, player);
            if (!items.contains(itemId)) {
                items.add(itemId);
                setPlayerUnlockedItems(plugin, player, items);
            }
        }
    }

    public static void removePlayerUnlockedItem(EMCShopMiragEdge plugin, Player player, String itemId) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            plugin.getDatabaseManager().removeUnlockedItem(player.getUniqueId(), itemId);
        } else {
            List<String> items = getPlayerUnlockedItems(plugin, player);
            items.remove(itemId);
            setPlayerUnlockedItems(plugin, player, items);
        }
    }

    public static boolean hasPlayerUnlockedItem(EMCShopMiragEdge plugin, Player player, String itemId) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            return plugin.getDatabaseManager().hasUnlockedItem(player.getUniqueId(), itemId);
        } else {
            return getPlayerUnlockedItems(plugin, player).contains(itemId);
        }
    }

    private static void savePlayerUnlocks() {
        try {
            playerUnlocksConfig.save(playerUnlocks);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save playerUnlocks.yml", e);
        }
    }

    // ==================== 凝聚箱子操作 ====================
    public static void addCChestStore(EMCShopMiragEdge plugin, Location location) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return;
        }

        int id = getNextChestId(cchestStoreConfig);
        String locString = locationToString(location);
        cchestStoreConfig.set(id + "", locString);
        saveCchestStore();
    }

    public static Integer getCChestIdStore(EMCShopMiragEdge plugin, Location location) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下不需要ID
            return null;
        }

        String locString = locationToString(location);
        for (String key : cchestStoreConfig.getKeys(false)) {
            if (cchestStoreConfig.getString(key).equals(locString)) {
                return Integer.parseInt(key);
            }
        }
        return null;
    }

    public static void removeCChestStore(EMCShopMiragEdge plugin, Integer id) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return;
        }

        cchestStoreConfig.set(id + "", null);
        saveCchestStore();
    }

    public static void setupCChest(EMCShopMiragEdge plugin, Integer id, Player player) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return;
        }

        if (id != null) {
            cchestConfig.set(id + ".owner", player.getUniqueId().toString());
            saveCchest();
        }
    }

    public static void setCChestItem(EMCShopMiragEdge plugin, Integer id, ItemStack itemStack) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return;
        }

        if (id != null) {
            if (itemStack == null) {
                cchestConfig.set(id + ".item", null);
            } else {
                cchestConfig.set(id + ".item", itemStack);
            }
            saveCchest();
        }
    }

    public static ItemStack getCChestItem(EMCShopMiragEdge plugin, Integer id) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return null;
        }

        if (id != null) {
            return cchestConfig.getItemStack(id + ".item");
        }
        return null;
    }

    public static boolean isOwnerCChest(EMCShopMiragEdge plugin, Player player, Integer id) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return false;
        }

        if (id != null) {
            String ownerUUID = cchestConfig.getString(id + ".owner");
            return player.getUniqueId().toString().equals(ownerUUID);
        }
        return false;
    }

    public static void removeCChest(EMCShopMiragEdge plugin, Integer id) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return;
        }

        if (id != null) {
            cchestConfig.set(id + "", null);
            saveCchest();
        }
    }

    private static void saveCchestStore() {
        try {
            cchestStoreConfig.save(cchestStore);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save cchest-store.yml", e);
        }
    }

    private static void saveCchest() {
        try {
            cchestConfig.save(cchest);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save cchest.yml", e);
        }
    }

    // ==================== 溶解箱子操作 ====================
    public static void addDChestStore(EMCShopMiragEdge plugin, Location location) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return;
        }

        int id = getNextChestId(dchestStoreConfig);
        String locString = locationToString(location);
        dchestStoreConfig.set(id + "", locString);
        saveDchestStore();
    }

    public static Integer getDChestIdStore(EMCShopMiragEdge plugin, Location location) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下不需要ID
            return null;
        }

        String locString = locationToString(location);
        for (String key : dchestStoreConfig.getKeys(false)) {
            if (dchestStoreConfig.getString(key).equals(locString)) {
                return Integer.parseInt(key);
            }
        }
        return null;
    }

    public static void removeDChestStore(EMCShopMiragEdge plugin, Integer id) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return;
        }

        dchestStoreConfig.set(id + "", null);
        saveDchestStore();
    }

    public static void setupDChest(EMCShopMiragEdge plugin, Integer id, Player player) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return;
        }

        if (id != null) {
            dchestConfig.set(id + ".owner", player.getUniqueId().toString());
            saveDchest();
        }
    }

    public static boolean isOwnerDChest(EMCShopMiragEdge plugin, Player player, Integer id) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return false;
        }

        if (id != null) {
            String ownerUUID = dchestConfig.getString(id + ".owner");
            return player.getUniqueId().toString().equals(ownerUUID);
        }
        return false;
    }

    public static void removeDChest(EMCShopMiragEdge plugin, Integer id) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            // 数据库模式下由ChestPlaceListener处理
            return;
        }

        if (id != null) {
            dchestConfig.set(id + "", null);
            saveDchest();
        }
    }

    private static void saveDchestStore() {
        try {
            dchestStoreConfig.save(dchestStore);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save dchest-store.yml", e);
        }
    }

    private static void saveDchest() {
        try {
            dchestConfig.save(dchest);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save dchest.yml", e);
        }
    }

    // ==================== 通用工具方法 ====================
    private static int getNextChestId(FileConfiguration config) {
        int highest = 0;
        for (String key : config.getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                if (id > highest) {
                    highest = id;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return highest + 1;
    }

    private static String locationToString(Location location) {
        return location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
    }

    // ==================== 数据迁移方法 ====================
    private static void migrateToDatabase(EMCShopMiragEdge plugin) {
        DatabaseManager dbManager = plugin.getDatabaseManager();
        if (!dbManager.isConnected()) return;

        plugin.getLogger().info("Starting data migration to database...");
        long startTime = System.currentTimeMillis();

        // 迁移玩家EMC数据
        migratePlayerEMC(plugin, dbManager);

        // 迁移玩家解锁物品
        migratePlayerUnlocks(plugin, dbManager);

        // 迁移凝聚箱子
        migrateChests(plugin, dbManager, cchestStoreConfig, cchestConfig, DatabaseManager.ChestType.CONDENSATION);

        // 迁移溶解箱子
        migrateChests(plugin, dbManager, dchestStoreConfig, dchestConfig, DatabaseManager.ChestType.DISSOLUTION);

        long endTime = System.currentTimeMillis();
        plugin.getLogger().info("Data migration completed in " + (endTime - startTime) + "ms");
    }

    private static void migratePlayerEMC(EMCShopMiragEdge plugin, DatabaseManager dbManager) {
        ConfigurationSection section = playerEMCConfig.getConfigurationSection("");
        if (section != null) {
            int count = 0;
            for (String uuidStr : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    double emc = playerEMCConfig.getDouble(uuidStr);
                    dbManager.setPlayerEMC(uuid, emc);
                    count++;
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in playerEMC.yml: " + uuidStr);
                }
            }
            plugin.getLogger().info("Migrated " + count + " player EMC records to database");
        }
    }

    private static void migratePlayerUnlocks(EMCShopMiragEdge plugin, DatabaseManager dbManager) {
        ConfigurationSection section = playerUnlocksConfig.getConfigurationSection("");
        if (section != null) {
            int count = 0;
            for (String uuidStr : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    List<String> items = playerUnlocksConfig.getStringList(uuidStr);
                    for (String itemId : items) {
                        dbManager.addUnlockedItem(uuid, itemId);
                        count++;
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in playerUnlocks.yml: " + uuidStr);
                }
            }
            plugin.getLogger().info("Migrated " + count + " player unlock records to database");
        }
    }

    private static void migrateChests(EMCShopMiragEdge plugin, DatabaseManager dbManager,
                                      FileConfiguration storeConfig, FileConfiguration chestConfig,
                                      DatabaseManager.ChestType type) {
        int count = 0;
        for (String idStr : storeConfig.getKeys(false)) {
            String locString = storeConfig.getString(idStr);
            if (locString != null) {
                String[] parts = locString.split(",");
                if (parts.length == 4) {
                    try {
                        String worldName = parts[0];
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int z = Integer.parseInt(parts[3]);
                        Location location = new Location(Bukkit.getWorld(worldName), x, y, z);

                        // 获取箱子所有者
                        UUID owner = getChestOwner(chestConfig, idStr);

                        // 获取目标物品（仅凝聚箱子）
                        ItemStack targetItem = null;
                        if (type == DatabaseManager.ChestType.CONDENSATION) {
                            targetItem = chestConfig.getItemStack(idStr + ".item");
                        }

                        // 保存到数据库
                        dbManager.saveChest(location, type, owner, targetItem);
                        count++;
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Invalid location format for chest " + idStr + ": " + locString);
                    }
                }
            }
        }
        plugin.getLogger().info("Migrated " + count + " " + type.name() + " chests to database");
    }

    private static UUID getChestOwner(FileConfiguration chestConfig, String idStr) {
        String ownerUuidStr = chestConfig.getString(idStr + ".owner");
        if (ownerUuidStr != null) {
            try {
                return UUID.fromString(ownerUuidStr);
            } catch (IllegalArgumentException e) {
                // 无效的UUID，返回默认值
            }
        }
        // 如果找不到有效所有者，返回一个默认UUID（例如控制台）
        return new UUID(0, 0);
    }

    // ==================== 玩家离线数据操作 ====================
    public static double getOfflinePlayerEMC(EMCShopMiragEdge plugin, OfflinePlayer player) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            return plugin.getDatabaseManager().getPlayerEMC(player.getUniqueId());
        } else {
            return playerEMCConfig.getDouble(player.getUniqueId().toString(), 0.0);
        }
    }

    public static void setOfflinePlayerEMC(EMCShopMiragEdge plugin, OfflinePlayer player, double emc) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            plugin.getDatabaseManager().setPlayerEMC(player.getUniqueId(), emc);
        } else {
            playerEMCConfig.set(player.getUniqueId().toString(), emc);
            savePlayerEMC();
        }
    }

    public static List<String> getOfflinePlayerUnlockedItems(EMCShopMiragEdge plugin, OfflinePlayer player) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            return plugin.getDatabaseManager().getUnlockedItems(player.getUniqueId());
        } else {
            return playerUnlocksConfig.getStringList(player.getUniqueId().toString());
        }
    }

    public static void addOfflinePlayerUnlockedItem(EMCShopMiragEdge plugin, OfflinePlayer player, String itemId) {
        if (plugin.isUsingDatabase() && plugin.getDatabaseManager().isConnected()) {
            plugin.getDatabaseManager().addUnlockedItem(player.getUniqueId(), itemId);
        } else {
            List<String> items = getOfflinePlayerUnlockedItems(plugin, player);
            if (!items.contains(itemId)) {
                items.add(itemId);
                playerUnlocksConfig.set(player.getUniqueId().toString(), items);
                savePlayerUnlocks();
            }
        }
    }
}