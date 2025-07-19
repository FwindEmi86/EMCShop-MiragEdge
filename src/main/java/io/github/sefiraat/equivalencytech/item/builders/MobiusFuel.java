package io.github.sefiraat.equivalencytech.item.builders;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.configuration.ConfigStrings;
import io.github.sefiraat.equivalencytech.statics.ContainerStorage;
import io.github.sefiraat.equivalencytech.statics.Messages;
import io.github.sefiraat.equivalencytech.statics.SkullTextures;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MobiusFuel {

    private final ItemStack item;
    private final EMCShopMiragEdge plugin;

    public ItemStack getItemClone() {
        return item.clone();
    }

    public ItemStack getItem() {
        return item;
    }

    public EMCShopMiragEdge getPlugin() {
        return plugin;
    }

    public MobiusFuel(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
        ConfigStrings c = plugin.getConfigMainClass().getStrings();

        // 使用兼容方法创建Mobius燃料头颅
        item = createCustomSkull(SkullTextures.ITEM_MOBIUS_FUEL);
        ItemMeta im = item.getItemMeta();

        im.setDisplayName(Messages.THEME_ITEM_NAME_GENERAL + c.getItemMobiusFuelName());
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + c.getGeneralCraftingItem());
        im.setLore(lore);
        item.setItemMeta(im);

        ContainerStorage.setItemID(item, plugin, "COAL2");
        ContainerStorage.makeMobiusFuel(item, plugin);
        ContainerStorage.makeCraftable(item, plugin);
    }

    /**
     * 手动创建自定义头颅 - 兼容 1.21.1 版本
     * @param base64 头颅的Base64纹理
     * @return 自定义头颅物品
     */
    private ItemStack createCustomSkull(String base64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        // 创建 GameProfile
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
            plugin.getLogger().log(java.util.logging.Level.WARNING, "设置Mobius燃料头颅时出错，使用备用方法", e);
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