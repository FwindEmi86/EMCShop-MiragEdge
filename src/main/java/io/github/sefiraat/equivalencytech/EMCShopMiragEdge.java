package io.github.sefiraat.equivalencytech;

import co.aikar.commands.PaperCommandManager;
import io.github.sefiraat.equivalencytech.commands.Commands;
import io.github.sefiraat.equivalencytech.configuration.ConfigMain;
import io.github.sefiraat.equivalencytech.item.EQItems;
import io.github.sefiraat.equivalencytech.listeners.ChestPlaceListener;
import io.github.sefiraat.equivalencytech.listeners.ManagerEvents;
import io.github.sefiraat.equivalencytech.misc.ManagerSupportedPlugins;
import io.github.sefiraat.equivalencytech.recipes.EmcDefinitions;
import io.github.sefiraat.equivalencytech.recipes.Recipes;
import io.github.sefiraat.equivalencytech.runnables.ManagerRunnables;
import io.github.sefiraat.equivalencytech.misc.DatabaseManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public class EMCShopMiragEdge extends JavaPlugin {

    private DatabaseManager databaseManager;
    private boolean usingDatabase = false;

    private static EMCShopMiragEdge instance;
    private PaperCommandManager commandManager;

    private ConfigMain configMainClass;
    private EmcDefinitions emcDefinitions;
    private EQItems eqItems;
    private Recipes recipes;
    private ManagerEvents managerEvents;
    private ManagerRunnables managerRunnables;
    private ManagerSupportedPlugins managerSupportedPlugins;

    private boolean isUnitTest = false;

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public static EMCShopMiragEdge getInstance() {
        return instance;
    }

    public ConfigMain getConfigMainClass() {
        return configMainClass;
    }

    public EmcDefinitions getEmcDefinitions() {
        return emcDefinitions;
    }

    public EQItems getEqItems() {
        return eqItems;
    }

    public Recipes getRecipes() {
        return recipes;
    }

    public ManagerEvents getManagerEvents() {
        return managerEvents;
    }

    public ManagerRunnables getManagerRunnables() {
        return managerRunnables;
    }

    public ManagerSupportedPlugins getManagerSupportedPlugins() {
        return managerSupportedPlugins;
    }

    // 默认构造函数
    public EMCShopMiragEdge() {
        super();
    }

    // 修复弃用警告：使用替代方案
    protected EMCShopMiragEdge(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        isUnitTest = true;
    }

    @Override
    public void onEnable() {
        // 保存默认配置
        saveDefaultConfig();

        // 初始化数据库
        initializeDatabase();

        // 初始化配置
        ConfigMain.setup(this);

        // 注册监听器
        new ChestPlaceListener(this);

        getLogger().info("########################################");
        getLogger().info(" EMCShop-MiragEdge - 狐风轩汐#FwindEmi  ");
        getLogger().info("       依据物品数量 加载时间可能较长        ");
        getLogger().info("########################################");

        instance = this;

        eqItems = new EQItems(this);
        managerSupportedPlugins = new ManagerSupportedPlugins(this);
        emcDefinitions = new EmcDefinitions(this);
        recipes = new Recipes(this);
        managerEvents = new ManagerEvents(this);
        managerRunnables = new ManagerRunnables(this);

        registerCommands();

        if (!isUnitTest) {
            int pluginId = 11527;
            Metrics metrics = new Metrics(this, pluginId);
            metrics.addCustomChart(new SimplePie("emc2", () -> String.valueOf(getManagerSupportedPlugins().isInstalledEMC2())));
        }
    }

    private void initializeDatabase() {
        if (getConfig().getBoolean("database.enabled")) {
            try {
                databaseManager = new DatabaseManager(this);
                usingDatabase = databaseManager.isConnected();
                getLogger().info("Database support: " + (usingDatabase ? "ENABLED" : "DISABLED"));
            } catch (Exception e) {
                getLogger().severe("Failed to initialize database: " + e.getMessage());
                usingDatabase = false;
            }
        } else {
            getLogger().info("Database support: DISABLED (config)");
        }
    }

    @Override
    public void onDisable() {
        // 关闭数据库连接
        if (databaseManager != null) {
            try {
                databaseManager.close();
            } catch (Exception e) {
                getLogger().warning("Failed to close database connection: " + e.getMessage());
            }
        }

        // 保存配置
        try {
            ConfigMain.saveAll();
        } catch (Exception e) {
            getLogger().warning("Failed to save configs: " + e.getMessage());
        }
    }

    // 添加缺失的方法
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public boolean isUsingDatabase() {
        return usingDatabase;
    }

    private void registerCommands() {
        try {
            commandManager = new PaperCommandManager(this);
            commandManager.registerCommand(new Commands(this));
        } catch (Exception e) {
            getLogger().severe("Failed to register commands: " + e.getMessage());
        }
    }
}