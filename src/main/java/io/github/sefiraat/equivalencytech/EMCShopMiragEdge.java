package io.github.sefiraat.equivalencytech;

import co.aikar.commands.PaperCommandManager;
import io.github.sefiraat.equivalencytech.commands.Commands;
import io.github.sefiraat.equivalencytech.configuration.ConfigMain;
import io.github.sefiraat.equivalencytech.misc.DatabaseManager; // 新增导入
import io.github.sefiraat.equivalencytech.item.EQItems;
import io.github.sefiraat.equivalencytech.listeners.ManagerEvents;
import io.github.sefiraat.equivalencytech.misc.ManagerSupportedPlugins;
import io.github.sefiraat.equivalencytech.recipes.EmcDefinitions;
import io.github.sefiraat.equivalencytech.recipes.Recipes;
import io.github.sefiraat.equivalencytech.runnables.ManagerRunnables;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.sql.SQLException; // 新增导入

public class EMCShopMiragEdge extends JavaPlugin {

    private static EMCShopMiragEdge instance;
    private PaperCommandManager commandManager;

    private ConfigMain configMainClass;
    private EmcDefinitions emcDefinitions;
    private EQItems eqItems;
    private Recipes recipes;
    private ManagerEvents managerEvents;
    private ManagerRunnables managerRunnables;
    private ManagerSupportedPlugins managerSupportedPlugins;
    private DatabaseManager databaseManager; // 新增数据库管理器

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

    // 新增方法：获取数据库管理器
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public EMCShopMiragEdge() {
        super();
    }

    protected EMCShopMiragEdge(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        isUnitTest = true;
    }

    @Override
    public void onEnable() {

        getLogger().info("########################################");
        getLogger().info(" EMCShop-MiragEdge - 狐风轩汐#FwindEmi  ");
        getLogger().info("       依据物品数量 加载时间可能较长        ");
        getLogger().info("########################################");

        saveDefaultConfig();
        reloadConfig();

        instance = this;

        configMainClass = new ConfigMain(this);

        initializeDatabaseManager();

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

    @Override
    public void onDisable() {
        saveConfig();
        configMainClass.saveAdditionalConfigs();

        // 关闭数据库连接池
        closeDatabaseManager();
    }

    private void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new Commands(this));
    }

    // 新增方法：初始化数据库连接
    private void initializeDatabaseManager() {
        try {
            databaseManager = new DatabaseManager(this);
            getLogger().info("数据库连接成功建立");
        } catch (Exception e) {
            getLogger().severe("数据库连接初始化失败: " + e.getMessage());
            e.printStackTrace();
            // 如果数据库连接失败，禁用插件
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    // 新增方法：关闭数据库连接
    private void closeDatabaseManager() {
        if (databaseManager != null) {
            try {
                databaseManager.close();
                getLogger().info("数据库连接已关闭");
            } catch (Exception e) {
                getLogger().warning("关闭数据库连接时出错: " + e.getMessage());
            }
        }
    }
}