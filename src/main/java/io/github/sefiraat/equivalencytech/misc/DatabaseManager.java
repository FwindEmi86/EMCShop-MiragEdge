package io.github.sefiraat.equivalencytech.misc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.misc.Utils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final EMCShopMiragEdge plugin;
    private HikariDataSource dataSource;
    private boolean connected = false;

    public DatabaseManager(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        if (!plugin.getConfig().getBoolean("database.enabled")) {
            plugin.getLogger().info("Database is disabled in config");
            return;
        }

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" +
                    plugin.getConfig().getString("database.host") + ":" +
                    plugin.getConfig().getInt("database.port") + "/" +
                    plugin.getConfig().getString("database.database"));
            config.setUsername(plugin.getConfig().getString("database.username"));
            config.setPassword(plugin.getConfig().getString("database.password"));

            // 连接池配置
            config.setMaximumPoolSize(plugin.getConfig().getInt("database.pool.maximum-pool-size", 10));
            config.setMinimumIdle(plugin.getConfig().getInt("database.pool.minimum-idle", 5));
            config.setConnectionTimeout(plugin.getConfig().getLong("database.pool.connection-timeout", 30000));
            config.setIdleTimeout(plugin.getConfig().getLong("database.pool.idle-timeout", 600000));
            config.setMaxLifetime(plugin.getConfig().getLong("database.pool.max-lifetime", 1800000));

            // 优化配置
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            dataSource = new HikariDataSource(config);

            // 测试连接
            try (Connection conn = dataSource.getConnection()) {
                if (conn.isValid(1000)) {
                    connected = true;
                    plugin.getLogger().info("Successfully connected to MySQL database!");
                    createTables();
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to database", e);
            connected = false;
        }
    }

    private void createTables() {
        if (!connected) return;

        String playerEMCTable = "CREATE TABLE IF NOT EXISTS player_emc (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "player_uuid CHAR(36) NOT NULL UNIQUE," +
                "emc_value DOUBLE NOT NULL DEFAULT 0.0" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";

        String unlockedItemsTable = "CREATE TABLE IF NOT EXISTS player_unlocked_items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "player_uuid CHAR(36) NOT NULL," +
                "item_id VARCHAR(64) NOT NULL," +
                "UNIQUE KEY unique_item (player_uuid, item_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";

        String emcChestsTable = "CREATE TABLE IF NOT EXISTS emc_chests (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "chest_type ENUM('DISSOLUTION', 'CONDENSATION') NOT NULL," +
                "world VARCHAR(64) NOT NULL," +
                "x INT NOT NULL," +
                "y INT NOT NULL," +
                "z INT NOT NULL," +
                "owner_uuid CHAR(36) NOT NULL," +
                "target_item TEXT," +
                "UNIQUE KEY location (world, x, y, z)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(playerEMCTable);
            stmt.execute(unlockedItemsTable);
            stmt.execute(emcChestsTable);

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create database tables", e);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection closed");
        }
    }

    // ================ 玩家EMC操作 ================
    public double getPlayerEMC(UUID playerUUID) {
        if (!connected) return 0.0;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT emc_value FROM player_emc WHERE player_uuid = ?")) {

            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("emc_value");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get player EMC", e);
        }
        return 0.0;
    }

    public void setPlayerEMC(UUID playerUUID, double emc) {
        if (!connected) return;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO player_emc (player_uuid, emc_value) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE emc_value = ?")) {

            stmt.setString(1, playerUUID.toString());
            stmt.setDouble(2, emc);
            stmt.setDouble(3, emc);
            stmt.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set player EMC", e);
        }
    }

    public void addPlayerEMC(UUID playerUUID, double amount) {
        if (!connected) return;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO player_emc (player_uuid, emc_value) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE emc_value = emc_value + ?")) {

            stmt.setString(1, playerUUID.toString());
            stmt.setDouble(2, amount);
            stmt.setDouble(3, amount);
            stmt.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add player EMC", e);
        }
    }

    // ================ 解锁物品操作 ================
    public List<String> getUnlockedItems(UUID playerUUID) {
        List<String> items = new ArrayList<>();
        if (!connected) return items;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT item_id FROM player_unlocked_items WHERE player_uuid = ?")) {

            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(rs.getString("item_id"));
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get unlocked items", e);
        }
        return items;
    }

    public boolean hasUnlockedItem(UUID playerUUID, String itemId) {
        if (!connected) return false;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT 1 FROM player_unlocked_items WHERE player_uuid = ? AND item_id = ?")) {

            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, itemId);
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to check unlocked item", e);
        }
        return false;
    }

    public void addUnlockedItem(UUID playerUUID, String itemId) {
        if (!connected) return;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT IGNORE INTO player_unlocked_items (player_uuid, item_id) VALUES (?, ?)")) {

            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add unlocked item", e);
        }
    }

    public void removeUnlockedItem(UUID playerUUID, String itemId) {
        if (!connected) return;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM player_unlocked_items WHERE player_uuid = ? AND item_id = ?")) {

            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to remove unlocked item", e);
        }
    }

    // ================ EMC箱子操作 ================
    public void saveChest(Location loc, ChestType type, UUID owner, ItemStack targetItem) {
        if (!connected) return;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO emc_chests (chest_type, world, x, y, z, owner_uuid, target_item) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE chest_type = VALUES(chest_type), " +
                             "owner_uuid = VALUES(owner_uuid), target_item = VALUES(target_item)")) {

            stmt.setString(1, type.name());
            stmt.setString(2, loc.getWorld().getName());
            stmt.setInt(3, loc.getBlockX());
            stmt.setInt(4, loc.getBlockY());
            stmt.setInt(5, loc.getBlockZ());
            stmt.setString(6, owner.toString());
            stmt.setString(7, targetItem != null ? itemStackToBase64(targetItem) : null);

            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save chest", e);
        }
    }

    public ChestData getChest(Location loc) {
        if (!connected) return null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM emc_chests WHERE world = ? AND x = ? AND y = ? AND z = ?")) {

            stmt.setString(1, loc.getWorld().getName());
            stmt.setInt(2, loc.getBlockX());
            stmt.setInt(3, loc.getBlockY());
            stmt.setInt(4, loc.getBlockZ());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ChestType type = ChestType.valueOf(rs.getString("chest_type"));
                UUID owner = UUID.fromString(rs.getString("owner_uuid"));
                String itemBase64 = rs.getString("target_item");
                ItemStack targetItem = itemBase64 != null ? itemStackFromBase64(itemBase64) : null;

                return new ChestData(type, owner, targetItem);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get chest", e);
        }
        return null;
    }

    public void removeChest(Location loc) {
        if (!connected) return;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM emc_chests WHERE world = ? AND x = ? AND y = ? AND z = ?")) {

            stmt.setString(1, loc.getWorld().getName());
            stmt.setInt(2, loc.getBlockX());
            stmt.setInt(3, loc.getBlockY());
            stmt.setInt(4, loc.getBlockZ());

            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to remove chest", e);
        }
    }

    public void updateChestItem(Location loc, ItemStack item) {
        if (!connected) return;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE emc_chests SET target_item = ? WHERE world = ? AND x = ? AND y = ? AND z = ?")) {

            stmt.setString(1, item != null ? itemStackToBase64(item) : null);
            stmt.setString(2, loc.getWorld().getName());
            stmt.setInt(3, loc.getBlockX());
            stmt.setInt(4, loc.getBlockY());
            stmt.setInt(5, loc.getBlockZ());

            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update chest item", e);
        }
    }

    // ================ 物品序列化工具 ================
    private String itemStackToBase64(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to serialize item stack", e);
            return null;
        }
    }

    private ItemStack itemStackFromBase64(String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to deserialize item stack", e);
            return null;
        }
    }

    // ================ 数据类 ================
    public enum ChestType {
        DISSOLUTION,
        CONDENSATION
    }

    public static class ChestData {
        private final ChestType type;
        private final UUID owner;
        private final ItemStack targetItem;

        public ChestData(ChestType type, UUID owner, ItemStack targetItem) {
            this.type = type;
            this.owner = owner;
            this.targetItem = targetItem;
        }

        public ChestType getType() {
            return type;
        }

        public UUID getOwner() {
            return owner;
        }

        public ItemStack getTargetItem() {
            return targetItem;
        }
    }
}