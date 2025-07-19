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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AeternalisFuel {

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

    public AeternalisFuel(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
        ConfigStrings c = plugin.getConfigMainClass().getStrings();

        // 手动创建头颅
        item = createCustomSkull(SkullTextures.ITEM_AETERNALIS_FUEL);
        ItemMeta im = item.getItemMeta();

        im.setDisplayName(Messages.THEME_ITEM_NAME_GENERAL + c.getItemAeternalisFuelName());
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + c.getGeneralCraftingItem());
        im.setLore(lore);
        item.setItemMeta(im);

        ContainerStorage.setItemID(item, plugin, "COAL3");
        ContainerStorage.makeAeternalisFuel(item, plugin);
        ContainerStorage.makeCraftable(item, plugin);
    }

    /**
     * 手动创建自定义头颅
     * @param base64 头颅的Base64纹理
     * @return 自定义头颅物品
     */
    private ItemStack createCustomSkull(String base64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        skull.setItemMeta(meta);
        return skull;
    }
}