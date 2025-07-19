package io.github.sefiraat.equivalencytech.statics;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.configuration.ConfigMain;
import io.github.sefiraat.equivalencytech.misc.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public final class Messages {

    public static final String PREFIX = "" + ChatColor.GRAY + "等价交换 ";
    public static final String SUFFIX = "" + ChatColor.GRAY + "";

    public static final ChatColor THEME_WARNING = ChatColor.YELLOW;
    public static final ChatColor THEME_ERROR = ChatColor.RED;
    public static final ChatColor THEME_NOTICE = ChatColor.WHITE;
    public static final ChatColor THEME_PASSIVE = ChatColor.GRAY;
    public static final ChatColor THEME_SUCCESS = ChatColor.GREEN;
    public static final ChatColor THEME_EMC_PURPLE = ChatColor.of("#5d2999");
    public static final ChatColor THEME_ITEM_NAME_GENERAL = ChatColor.of("#cfab1d");
    public static final ChatColor THEME_PASSIVE_GRAY = ChatColor.of("#a3a3a3");
    public static final ChatColor THEME_CLICK_INSTRUCTION = ChatColor.of("#cfab1d");
    public static final ChatColor THEME_PASSIVE_CONGRATULATE = ChatColor.of("#fff41f");

    private Messages() {
        throw new IllegalStateException("Utility class");
    }

    // region Commands

    public static String msgCmdSubcommand(EMCShopMiragEdge plugin) {
        return PREFIX + THEME_NOTICE + "请使用子命令: /emcshop itememc, /emcshop emc, /emcshop giveitem";
    }

    public static String msgCmdEmcMustHold(EMCShopMiragEdge plugin) {
        return PREFIX + THEME_WARNING + "你必须手持一个物品来查看其EMC值";
    }

    public static String msgCmdEmcNone(EMCShopMiragEdge plugin) {
        return PREFIX + THEME_WARNING + "该物品没有EMC值";
    }

    public static String msgCmdEmcDisplay(Material m, Double emc) {
        return PREFIX + THEME_WARNING + Utils.materialFriendlyName(m) + " x 1 = EMC " + emc;
    }

    public static String msgCmdEmcDisplay(String s, Double emc) {
        return PREFIX + THEME_WARNING + s + " x 1 = EMC " + emc;
    }

    public static String msgCmdEmcDisplayStack(Material m, Integer amount, Double emc) {
        return PREFIX + THEME_WARNING + Utils.materialFriendlyName(m) + " x " + amount + " = EMC " + emc;
    }

    public static String msgCmdEmcDisplayStack(String s, Integer amount, Double emc) {
        return PREFIX + THEME_WARNING + s + " x " + amount + " = EMC " + emc;
    }

    public static String messageCommandSelectItem(EMCShopMiragEdge plugin) {
        return PREFIX + THEME_NOTICE + "请选择要给予的物品: transmutationorb, dissolutionchest, condensatechest";
    }

    public static String messageCommandItemGiven(EMCShopMiragEdge plugin, String itemName) {
        return PREFIX + THEME_NOTICE + "已给予物品: " + itemName;
    }

    public static String messageCommandEmc(EMCShopMiragEdge plugin, Player player) {
        return PREFIX + THEME_NOTICE + "你有 " + THEME_SUCCESS + ConfigMain.getPlayerEmc(plugin, player) + THEME_NOTICE + " EMC.";
    }

    // endregion

    // region GUI

    public static String messageGuiItemLearned(EMCShopMiragEdge plugin) {
        return PREFIX + THEME_PASSIVE_CONGRATULATE + "你学会了新的等价交换物品!";
    }

    public static String messageGuiEmcGiven(EMCShopMiragEdge plugin, Player player, double emcBase, double emcTotal, int itemAmt, int burnRate) {
        return PREFIX + THEME_SUCCESS + "+" + emcTotal + " EMC " + THEME_PASSIVE + "(" + emcBase + " * " + itemAmt + ")" + THEME_ERROR + " 损耗 = " + burnRate + "%" + THEME_NOTICE + " : [总EMC : " + ConfigMain.getPlayerEmc(plugin, player) + "]";
    }

    public static String messageGuiEmcRemoved(EMCShopMiragEdge plugin, Player player, double emcBase, double emcTotal, int itemAmt) {
        return PREFIX + THEME_ERROR + "-" + emcTotal + " EMC " + THEME_PASSIVE + "(" + emcBase + " * " + itemAmt + ") : " + THEME_NOTICE + " [总EMC : " + ConfigMain.getPlayerEmc(plugin, player) + "]";
    }

    public static String messageGuiEmcNotEnough(EMCShopMiragEdge plugin, Player player) {
        return PREFIX + THEME_ERROR + "EMC不足!" + THEME_NOTICE + " [总EMC : " + ConfigMain.getPlayerEmc(plugin, player) + "]";
    }

    public static String messageGuiNoSpace(EMCShopMiragEdge plugin) {
        return PREFIX + THEME_ERROR + "背包空间不足!";
    }

    public static String messageGuiItemMeta(EMCShopMiragEdge plugin) {
        return PREFIX + THEME_ERROR + "物品元数据不匹配!";
    }

    // endregion

    // region Events

    public static String messageEventEMCChestPlace(EMCShopMiragEdge plugin) {
        return PREFIX + THEME_ERROR + "你不能将EMC箱子放在相邻位置!";
    }

    public static String messageEventCantOpenNotOwner(EMCShopMiragEdge plugin) {
        return PREFIX + THEME_ERROR + "你不是这个箱子的主人!";
    }

    public static String messageEventItemSet(EMCShopMiragEdge plugin) {
        return PREFIX + THEME_SUCCESS + "已设置合成物品!";
    }

    public static String messageEventItemUnset(EMCShopMiragEdge plugin) {
        return PREFIX + THEME_SUCCESS + "已取消合成物品!";
    }

    public static List<String> messageEMC2Installed(EMCShopMiragEdge plugin) {
        List<String> message = new ArrayList<>();
        message.add(THEME_ERROR + "你同时安装了EquiTech和EMCShop插件");
        message.add(THEME_NOTICE + "请注意这可能会导致冲突。查看以下链接了解更多信息。此消息并不意味着你需要移除EMCShop/EquiTech。");
        message.add("");
        message.add(THEME_NOTICE + "你可以在EquiTech配置中禁用此消息");
        message.add("");
        message.add(THEME_WARNING + "https://github.com/Sefiraat/EquivalencyTech/");
        message.add(THEME_WARNING + "https://github.com/Seggan/EMC2");
        return message;
    }

    // endregion

}