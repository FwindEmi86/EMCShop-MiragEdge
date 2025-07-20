package io.github.sefiraat.equivalencytech.configuration;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.misc.DatabaseManager;
import io.github.sefiraat.equivalencytech.misc.Utils;
import io.github.sefiraat.equivalencytech.statics.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConfigMain {

    private final EMCShopMiragEdge plugin;
    private final DatabaseManager databaseManager;

    private final ConfigStrings strings;
    private final ConfigEMC emc;
    private final ConfigBooleans bools;

    private File blockStoreConfigFile;
    private FileConfiguration blockStoreConfig;
    private File dChestConfigFile;
    private FileConfiguration dChestConfig;
    private File cChestConfigFile;
    private FileConfiguration cChestConfig;

    public ConfigStrings getStrings() {
        return strings;
    }
    public ConfigEMC getEmc() {
        return emc;
    }
    public ConfigBooleans getBools() {
        return bools;
    }

    public File getBlockStoreConfigFile() {
        return blockStoreConfigFile;
    }

    public FileConfiguration getBlockStoreConfig() {
        return blockStoreConfig;
    }

    public File getDChestConfigFile() {
        return dChestConfigFile;
    }

    public FileConfiguration getDChestConfig() {
        return dChestConfig;
    }

    public File getCChestConfigFile() {
        return cChestConfigFile;
    }

    public FileConfiguration getCChestConfig() {
        return cChestConfig;
    }

    public static final String DIS_CHEST_CFG = "DIS_CHESTS";
    public static final String CON_CHEST_CFG = "CON_CHESTS";

    public ConfigMain(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
        this.databaseManager = new DatabaseManager(plugin);

        strings = new ConfigStrings(plugin);
        emc = new ConfigEMC(plugin);
        bools = new ConfigBooleans(plugin);

        sortConfigs();
    }

    private void sortConfigs() {
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        createAdditionalConfigs();
    }

    private void createAdditionalConfigs() {
        createBlockStoreConfig();
        createDChestConfig();
        createCChestConfig();
    }

    public void saveAdditionalConfigs() {
        saveBlockStoreConfig();
        saveDChestConfig();
        saveCChestConfig();
    }

    private void createBlockStoreConfig() {
        blockStoreConfigFile = new File(plugin.getDataFolder(), "block_storage.yml");
        if (!blockStoreConfigFile.exists()) {
            blockStoreConfigFile.getParentFile().mkdirs();
            plugin.saveResource("block_storage.yml", false);
        }
        blockStoreConfig = new YamlConfiguration();
        try {
            blockStoreConfig.load(blockStoreConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void saveBlockStoreConfig() {
        try {
            blockStoreConfig.save(blockStoreConfigFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to save " + blockStoreConfigFile.getName());
        }
    }

    private void createDChestConfig() {
        dChestConfigFile = new File(plugin.getDataFolder(), "dissolution_chests.yml");
        if (!dChestConfigFile.exists()) {
            dChestConfigFile.getParentFile().mkdirs();
            plugin.saveResource("dissolution_chests.yml", false);
        }
        dChestConfig = new YamlConfiguration();
        try {
            dChestConfig.load(dChestConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void saveDChestConfig() {
        try {
            dChestConfig.save(dChestConfigFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to save " + dChestConfigFile.getName());
        }
    }

    private void createCChestConfig() {
        cChestConfigFile = new File(plugin.getDataFolder(), "condensation_chests.yml");
        if (!cChestConfigFile.exists()) {
            cChestConfigFile.getParentFile().mkdirs();
            plugin.saveResource("condensation_chests.yml", false);
        }
        cChestConfig = new YamlConfiguration();
        try {
            cChestConfig.load(cChestConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void saveCChestConfig() {
        try {
            cChestConfig.save(cChestConfigFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to save " + cChestConfigFile.getName());
        }
    }

    public static void addLearnedItem(EMCShopMiragEdge plugin, String uuid, String itemName) {
        // 使用数据库存储解锁物品
        plugin.getConfigMainClass().databaseManager.addUnlockedItem(UUID.fromString(uuid), itemName);
    }

    public static void removeLearnedItem(EMCShopMiragEdge plugin, Player player, String itemName) {
        // 使用数据库移除解锁物品
        plugin.getConfigMainClass().databaseManager.removeUnlockedItem(player.getUniqueId(), itemName);
    }

    public static List<String> getLearnedItems(EMCShopMiragEdge plugin, String uuid) {
        // 从数据库获取解锁物品列表
        return plugin.getConfigMainClass().databaseManager.getUnlockedItems(UUID.fromString(uuid));
    }

    public static int getLearnedItemAmount(EMCShopMiragEdge plugin, Player player) {
        // 从数据库获取解锁物品数量
        return plugin.getConfigMainClass().databaseManager.getUnlockedItemCount(player.getUniqueId());
    }

    public static void addPlayerEmc(EMCShopMiragEdge plugin, Player player, Double emcValue, Double totalEmc, int stackAmount) {
        double playerEmc = getPlayerEmc(plugin, player);
        int burnRate = plugin.getConfigMainClass().getEmc().getBurnRate();
        if (burnRate > 0) {
            totalEmc -= ((totalEmc / 100) * burnRate);
        }
        totalEmc = Utils.roundDown(totalEmc, 2);
        Double sum = playerEmc + totalEmc;
        if (sum.equals(Double.POSITIVE_INFINITY)) {
            sum = Double.MAX_VALUE;
        }
        setPlayerEmc(plugin, player, sum);
        player.sendMessage(Messages.messageGuiEmcGiven(plugin, player, emcValue, totalEmc, stackAmount, burnRate));
    }

    public static void addPlayerEmc(EMCShopMiragEdge plugin, String uuid, Double totalEmc) {
        double playerEmc = getPlayerEmc(plugin, uuid);
        int burnRate = plugin.getConfigMainClass().getEmc().getBurnRate();
        if (burnRate > 0) {
            totalEmc -= ((totalEmc / 100) * burnRate);
        }
        totalEmc = Utils.roundDown(totalEmc, 2);
        Double sum = playerEmc + totalEmc;
        if (sum.equals(Double.POSITIVE_INFINITY)) {
            sum = Double.MAX_VALUE;
        }
        setPlayerEmc(plugin, uuid, sum);
    }

    public static void removePlayerEmc(EMCShopMiragEdge plugin, Player player, Double emcValue) {
        setPlayerEmc(plugin, player, getPlayerEmc(plugin, player) - emcValue);
    }

    public static void removePlayerEmc(EMCShopMiragEdge plugin, String uuid, Double emcValue) {
        setPlayerEmc(plugin, uuid, getPlayerEmc(plugin, uuid) - emcValue);
    }

    public static void setPlayerEmc(EMCShopMiragEdge plugin, Player player, Double emcValue) {
        // 使用数据库设置玩家EMC
        plugin.getConfigMainClass().databaseManager.setPlayerEMC(player.getUniqueId(), emcValue);
    }

    public static void setPlayerEmc(EMCShopMiragEdge plugin, String uuid, Double emcValue) {
        // 使用数据库设置玩家EMC
        plugin.getConfigMainClass().databaseManager.setPlayerEMC(UUID.fromString(uuid), emcValue);
    }

    public static double getPlayerEmc(EMCShopMiragEdge plugin, Player player) {
        // 从数据库获取玩家EMC
        return plugin.getConfigMainClass().databaseManager.getPlayerEMC(player.getUniqueId());
    }

    public static double getPlayerEmc(EMCShopMiragEdge plugin, String uuid) {
        // 从数据库获取玩家EMC
        return plugin.getConfigMainClass().databaseManager.getPlayerEMC(UUID.fromString(uuid));
    }

    // 以下箱子相关方法保持不变
    public static Integer getNextDChestID(EMCShopMiragEdge plugin) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        ConfigurationSection section = c.getConfigurationSection(DIS_CHEST_CFG);
        int nextValue = 1;
        if (section != null) {
            for (String key : section.getKeys(false)) {
                int value = Integer.parseInt(key);
                if (value > nextValue) {
                    nextValue = value;
                }
            }
            nextValue++;
        }
        return nextValue;
    }

    public static void addDChestStore(EMCShopMiragEdge plugin, Location location) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        c.set(DIS_CHEST_CFG + "." + getNextDChestID(plugin).toString(), location);
    }

    @Nullable
    public static Integer getDChestIdStore(EMCShopMiragEdge plugin, Location location) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        ConfigurationSection section = c.getConfigurationSection(DIS_CHEST_CFG);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                Location l = section.getLocation(key);
                if (l.equals(location)) {
                    return Integer.parseInt(key);
                }
            }
        }
        return null;
    }

    public static void removeDChestStore(EMCShopMiragEdge plugin, Integer id) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        ConfigurationSection section = c.getConfigurationSection(DIS_CHEST_CFG);
        section.set(id.toString(), null);
    }

    public static void setupDChest(EMCShopMiragEdge plugin, Integer id, Player player) {
        FileConfiguration c = plugin.getConfigMainClass().dChestConfig;
        c.set(id + ".OWNING_PLAYER", player.getUniqueId().toString());
        c.set(id + ".LEVEL", 1);
    }

    public static void removeDChest(EMCShopMiragEdge plugin, Integer id) {
        FileConfiguration c = plugin.getConfigMainClass().dChestConfig;
        c.set(String.valueOf(id), null);
    }

    public static boolean isOwnerDChest(EMCShopMiragEdge plugin, Player player, Integer id) {
        FileConfiguration c = plugin.getConfigMainClass().dChestConfig;
        return c.getString(id + ".OWNING_PLAYER").equals(player.getUniqueId().toString());
    }

    public static String getOwnerDChest(EMCShopMiragEdge plugin, Integer id) {
        FileConfiguration c = plugin.getConfigMainClass().dChestConfig;
        return c.getString(id + ".OWNING_PLAYER");
    }

    public static Location getDChestLocation(EMCShopMiragEdge plugin, Integer id) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        ConfigurationSection section = c.getConfigurationSection(DIS_CHEST_CFG);
        return section.getLocation(id.toString());
    }

    public static List<Location> getAllDChestLocations(EMCShopMiragEdge plugin) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        ConfigurationSection section = c.getConfigurationSection(DIS_CHEST_CFG);
        List<Location> ids = new ArrayList<>();
        if (section != null) {
            for (String s : section.getKeys(false)) {
                ids.add(section.getLocation(s));
            }
        }
        return ids;
    }

    public static Integer getNextCChestID(EMCShopMiragEdge plugin) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        ConfigurationSection section = c.getConfigurationSection(CON_CHEST_CFG);
        int nextValue = 1;
        if (section != null) {
            for (String key : section.getKeys(false)) {
                int value = Integer.parseInt(key);
                if (value > nextValue) {
                    nextValue = value;
                }
            }
            nextValue++;
        }
        return nextValue;
    }

    public static void addCChestStore(EMCShopMiragEdge plugin, Location location) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        c.set(CON_CHEST_CFG + "." + getNextCChestID(plugin).toString(), location);
    }

    @Nullable
    public static Integer getCChestIdStore(EMCShopMiragEdge plugin, Location location) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        ConfigurationSection section = c.getConfigurationSection(CON_CHEST_CFG);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                Location l = section.getLocation(key);
                if (l.equals(location)) {
                    return Integer.parseInt(key);
                }
            }
        }
        return null;
    }

    public static void removeCChestStore(EMCShopMiragEdge plugin, Integer id) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        ConfigurationSection section = c.getConfigurationSection(CON_CHEST_CFG);
        section.set(id.toString(), null);
    }

    public static void setupCChest(EMCShopMiragEdge plugin, Integer id, Player player) {
        FileConfiguration c = plugin.getConfigMainClass().cChestConfig;
        c.set(id + ".OWNING_PLAYER", player.getUniqueId().toString());
        c.set(id + ".LEVEL", 1);
    }

    public static void removeCChest(EMCShopMiragEdge plugin, Integer id) {
        FileConfiguration c = plugin.getConfigMainClass().cChestConfig;
        c.set(String.valueOf(id), null);
    }

    public static boolean isOwnerCChest(EMCShopMiragEdge plugin, Player player, Integer id) {
        FileConfiguration c = plugin.getConfigMainClass().cChestConfig;
        return c.getString(id + ".OWNING_PLAYER").equals(player.getUniqueId().toString());
    }

    public static String getOwnerCChest(EMCShopMiragEdge plugin, Integer id) {
        FileConfiguration c = plugin.getConfigMainClass().cChestConfig;
        return c.getString(id + ".OWNING_PLAYER");
    }

    public static Location getCChestLocation(EMCShopMiragEdge plugin, Integer id) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        ConfigurationSection section = c.getConfigurationSection(CON_CHEST_CFG);
        return section.getLocation(id.toString());
    }

    public static List<Location> getAllCChestLocations(EMCShopMiragEdge plugin) {
        FileConfiguration c = plugin.getConfigMainClass().blockStoreConfig;
        ConfigurationSection section = c.getConfigurationSection(CON_CHEST_CFG);
        List<Location> ids = new ArrayList<>();
        if (section != null) {
            for (String s : section.getKeys(false)) {
                ids.add(section.getLocation(s));
            }
        }
        return ids;
    }

    @Nullable
    public static ItemStack getCChestItem(EMCShopMiragEdge plugin, Integer id) {
        FileConfiguration c = plugin.getConfigMainClass().cChestConfig;
        return c.getItemStack(id + ".ITEM");
    }

    public static void setCChestItem(EMCShopMiragEdge plugin, Integer id, ItemStack itemStack) {
        FileConfiguration c = plugin.getConfigMainClass().cChestConfig;
        c.set(id + ".ITEM", itemStack);
    }
}