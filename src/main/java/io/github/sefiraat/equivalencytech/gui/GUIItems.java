package io.github.sefiraat.equivalencytech.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.triumphteam.gui.guis.GuiItem;
import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.configuration.ConfigMain;
import io.github.sefiraat.equivalencytech.configuration.ConfigStrings;
import io.github.sefiraat.equivalencytech.misc.Utils;
import io.github.sefiraat.equivalencytech.statics.Messages;
import io.github.sefiraat.equivalencytech.statics.SkullTextures;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIItems {

    private GUIItems() {
        throw new IllegalStateException("Utility class");
    }

    public static GuiItem guiOrbInfo(EMCShopMiragEdge plugin, Player player) {
        // 使用修复后的方法创建头颅
        ItemStack skull = createCustomSkull(plugin, SkullTextures.GUI_INFO_ALL);
        GuiItem g = new GuiItem(skull);
        ItemStack i = g.getItemStack();
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(guiDisplayNameInfo(plugin));
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        im.setLore(guiDisplayLoreInfo(plugin, player));
        i.setItemMeta(im);
        g.setItemStack(i);
        g.setAction(event -> event.setCancelled(true));
        return g;
    }

    public static GuiItem guiOrbBorder(EMCShopMiragEdge plugin) {
        GuiItem g = new GuiItem(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack i = g.getItemStack();
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(Messages.THEME_PASSIVE_GRAY + plugin.getConfigMainClass().getStrings().getGuiBorderName());
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        i.setItemMeta(im);
        g.setItemStack(i);
        g.setAction(event -> event.setCancelled(true));
        return g;
    }

    public static GuiItem guiOrbFiller(EMCShopMiragEdge plugin) {
        GuiItem g = new GuiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemStack i = g.getItemStack();
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(Messages.THEME_PASSIVE_GRAY + plugin.getConfigMainClass().getStrings().getGuiFillerName());
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        i.setItemMeta(im);
        g.setItemStack(i);
        g.setAction(event -> event.setCancelled(true));
        return g;
    }

    public static GuiItem guiEMCItem(EMCShopMiragEdge plugin, ItemStack itemStack, boolean isVanilla) {
        GuiItem g = new GuiItem(itemStack);
        ItemMeta im = itemStack.getItemMeta();

        if (isVanilla) {
            im.setDisplayName(ChatColor.WHITE + Utils.materialFriendlyName(itemStack.getType()));
        } else {
            im.setDisplayName(ChatColor.WHITE + ChatColor.stripColor(im.getDisplayName()));
        }

        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        im.setLore(getEmcItemLore(plugin, itemStack));
        itemStack.setItemMeta(im);
        g.setItemStack(itemStack);
        return g;
    }

    public static List<String> getEmcItemLore(EMCShopMiragEdge plugin, ItemStack itemStack) {
        ConfigStrings c = plugin.getConfigMainClass().getStrings();
        List<String> lore = new ArrayList<>();
        lore.add(Messages.THEME_EMC_PURPLE + "EMC: " + Utils.getEMC(plugin, itemStack));
        lore.add("");
        lore.add(Messages.THEME_CLICK_INSTRUCTION + "左键点击: " + ChatColor.WHITE + "购买");
        lore.add(Messages.THEME_CLICK_INSTRUCTION + "右键点击: " + ChatColor.WHITE + "出售");
        return lore;
    }

    public static String guiDisplayNameInfo(EMCShopMiragEdge plugin) {
        return ChatColor.RED + "EMC商店信息";
    }

    public static List<String> guiDisplayLoreInfo(EMCShopMiragEdge plugin, Player player) {
        List<String> l = new ArrayList<>();
        int recipesKnown = ConfigMain.getLearnedItemAmount(plugin, player);
        int recipesTotal = Utils.totalRecipes(plugin);
        l.add("" + ChatColor.GOLD + ChatColor.BOLD + "已学习配方: " + ChatColor.WHITE + recipesKnown + "/" + recipesTotal);
        l.add("");
        l.add("" + ChatColor.GOLD + ChatColor.BOLD + "当前EMC: " + ChatColor.WHITE + ConfigMain.getPlayerEmc(plugin, player));
        return l;
    }

    /**
     * 手动创建自定义头颅 - 兼容 1.21.1 版本
     * @param plugin 插件实例
     * @param base64 头颅的Base64纹理
     * @return 自定义头颅物品
     */
    private static ItemStack createCustomSkull(EMCShopMiragEdge plugin, String base64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if (skull.getItemMeta() == null) {
            return skull; // 防止meta为null
        }

        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        // 修复：使用固定名称 "FwindEmi" 替代 null
        GameProfile profile = new GameProfile(UUID.randomUUID(), "FwindEmi");
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            // 尝试使用反射设置头颅元数据
            Field profileField;
            try {
                // 尝试获取新版字段
                profileField = meta.getClass().getDeclaredField("profile");
            } catch (NoSuchFieldException e) {
                // 回退到旧版字段名
                profileField = meta.getClass().getDeclaredField("profile");
            }

            profileField.setAccessible(true);

            // 获取字段类型
            Class<?> fieldType = profileField.getType();

            if (fieldType.isAssignableFrom(GameProfile.class)) {
                // 直接设置 GameProfile
                profileField.set(meta, profile);
            } else {
                // 处理新版 ResolvableProfile
                try {
                    Class<?> resolvableProfileClass = Class.forName("net.minecraft.world.item.component.ResolvableProfile");
                    Constructor<?> constructor = resolvableProfileClass.getConstructor(GameProfile.class);
                    Object resolvableProfile = constructor.newInstance(profile);
                    profileField.set(meta, resolvableProfile);
                } catch (ClassNotFoundException e) {
                    // 回退方案：使用 Bukkit API
                    plugin.getLogger().warning("ResolvableProfile 类未找到，使用备用方法");
                    meta.setOwningPlayer(plugin.getServer().getOfflinePlayer("FwindEmi"));
                }
            }
        } catch (ReflectiveOperationException e) {
            // 最终回退方案
            plugin.getLogger().log(java.util.logging.Level.WARNING, "设置GUI头颅时出错，使用备用方法", e);
            try {
                meta.setOwningPlayer(plugin.getServer().getOfflinePlayer("FwindEmi"));
            } catch (Exception ex) {
                plugin.getLogger().log(java.util.logging.Level.SEVERE, "无法设置备用玩家头颅", ex);
            }
        }

        skull.setItemMeta(meta);
        return skull;
    }
}