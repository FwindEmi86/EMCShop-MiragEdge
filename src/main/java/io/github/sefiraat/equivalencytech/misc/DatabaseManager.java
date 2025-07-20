package io.github.sefiraat.equivalencytech.misc;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final HikariDataSource dataSource;

    public DatabaseManager(EMCShopMiragEdge plugin) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" +
                plugin.getConfig().getString("database.host") + ":" +
                plugin.getConfig().getInt("database.port") + "/" +
                plugin.getConfig().getString("database.database"));
        config.setUsername(plugin.getConfig().getString("database.username"));
        config.setPassword(plugin.getConfig().getString("database.password"));
        config.setMaximumPoolSize(plugin.getConfig().getInt("database.pool.maximum-pool-size"));
        config.setMinimumIdle(plugin.getConfig().getInt("database.pool.minimum-idle"));
        config.setConnectionTimeout(plugin.getConfig().getInt("database.pool.connection-timeout"));
        config.setIdleTimeout(plugin.getConfig().getInt("database.pool.idle-timeout"));
        config.setMaxLifetime(plugin.getConfig().getInt("database.pool.max-lifetime"));
        dataSource = new HikariDataSource(config);
        createTables();
    }

    private void createTables() {
        try (Connection conn = getConnection()) {
            // 创建玩家EMC表
            conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS player_emc (" +
                            "uuid CHAR(36) PRIMARY KEY, " +
                            "emc_balance DOUBLE NOT NULL DEFAULT 0" +
                            ")"
            ).executeUpdate();

            // 创建解锁物品表
            conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS player_unlocks (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "player_uuid CHAR(36) NOT NULL, " +
                            "item_id VARCHAR(255) NOT NULL, " +
                            "UNIQUE KEY unique_unlock (player_uuid, item_id)" +
                            ")"
            ).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public double getPlayerEMC(UUID uuid) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT emc_balance FROM player_emc WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble("emc_balance") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setPlayerEMC(UUID uuid, double amount) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO player_emc (uuid, emc_balance) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE emc_balance = ?")) {
            stmt.setString(1, uuid.toString());
            stmt.setDouble(2, amount);
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUnlockedItem(UUID playerUuid, String itemId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT IGNORE INTO player_unlocks (player_uuid, item_id) VALUES (?, ?)")) {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeUnlockedItem(UUID playerUuid, String itemId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM player_unlocks WHERE player_uuid = ? AND item_id = ?")) {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getUnlockedItems(UUID playerUuid) {
        List<String> items = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT item_id FROM player_unlocks WHERE player_uuid = ?")) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(rs.getString("item_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public int getUnlockedItemCount(UUID playerUuid) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) AS count FROM player_unlocks WHERE player_uuid = ?")) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 添加关闭连接池的方法
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}