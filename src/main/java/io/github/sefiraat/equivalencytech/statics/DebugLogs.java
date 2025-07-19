package io.github.sefiraat.equivalencytech.statics;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public final class DebugLogs {

    private DebugLogs() {
        throw new IllegalStateException("工具类");
    }

    public static final String NEST = " >";

    // 日志方法
    public static void logEmcBaseValueLoaded(EMCShopMiragEdge plugin, String materialName, Double emcValue) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "加载基础EMC值 : {0} : {1}", new Object[]{materialName, emcValue});
        }
    }

    public static void logEmcTestingItemStack(EMCShopMiragEdge plugin, String itemStackName, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}正在测试EMC : {1}", new Object[]{StringUtils.repeat(NEST, nestLevel), itemStackName});
        }
    }

    public static void logEmcNoRecipes(EMCShopMiragEdge plugin, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}无配方 - 不可压缩", StringUtils.repeat(NEST, nestLevel));
        }
    }

    public static void logEmcIsBase(EMCShopMiragEdge plugin, Double emcValue, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}是基础物品 : 传递EMC值 : {1}", new Object[]{StringUtils.repeat(NEST, nestLevel), emcValue});
        }
    }

    public static void logEmcIsRegisteredExtended(EMCShopMiragEdge plugin, Double emcValue, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}在扩展中已注册 : 传递EMC值 : {1}", new Object[]{StringUtils.repeat(NEST, nestLevel), emcValue});
        }
    }

    public static void logCheckingRecipe(EMCShopMiragEdge plugin, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}找到配方 : 开始递归", StringUtils.repeat(NEST, nestLevel));
        }
    }

    public static void logEmcPosted(EMCShopMiragEdge plugin, Double emcValue, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}所有物品处理完成 : EMC : {1}", new Object[]{StringUtils.repeat(NEST, nestLevel), emcValue});
        }
    }

    public static void logEmcNull(EMCShopMiragEdge plugin, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}所有物品处理完成 : 存在物品返回空基础EMC - 未添加物品", StringUtils.repeat(NEST, nestLevel));
        }
    }

    public static void logRecipeType(EMCShopMiragEdge plugin, String type, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}配方类型 : {1}", new Object[]{StringUtils.repeat(NEST, nestLevel), type});
        }
    }

    public static void logRecipeMultipleOutputs(EMCShopMiragEdge plugin, Double emcTotal, Integer outputVolume, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}配方含多个输出物品 : 使用值 : ({1}/{2})", new Object[]{StringUtils.repeat(NEST, nestLevel), emcTotal, outputVolume});
        }
    }

    public static void logEmcRecipeResult(EMCShopMiragEdge plugin, Double emcValue, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}配方计算EMC结果 : {1}", new Object[]{StringUtils.repeat(NEST, nestLevel), emcValue});
        }
    }

    public static void logRecipeCheaper(EMCShopMiragEdge plugin, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}配方为首个/唯一或当前最便宜", StringUtils.repeat(NEST, nestLevel));
        }
    }

    public static void logRecipeNotCheaper(EMCShopMiragEdge plugin, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}配方有效但存在更便宜配方 - 已忽略", StringUtils.repeat(NEST, nestLevel));
        }
    }

    public static void logEQStart(EMCShopMiragEdge plugin, Integer nestLevel, ItemStack itemStack) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}开始等价配方检查 : 物品堆类型 : {1}", new Object[]{StringUtils.repeat(NEST, nestLevel), itemStack.getType()});
        }
    }

    public static void logEQisCrafting(EMCShopMiragEdge plugin, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}物品堆为可等价合成物品", new Object[]{StringUtils.repeat(NEST, nestLevel)});
        }
    }

    public static void logEQisNotCrafting(EMCShopMiragEdge plugin, Integer nestLevel) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, "{0}物品堆不可等价合成 - 转入扩展/基础", new Object[]{StringUtils.repeat(NEST, nestLevel)});
        }
    }

    public static void logBoring(EMCShopMiragEdge plugin, String string) {
        if (plugin.getConfigMainClass().getBools().getDebuggingLogs()) {
            plugin.getLogger().log(Level.INFO, string); // 直接输出传入字符串（需外部汉化）
        }
    }
}