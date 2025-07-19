package io.github.sefiraat.equivalencytech.item.builders;

import dev.dbassett.skullcreator.SkullCreator;
import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.configuration.ConfigStrings;
import io.github.sefiraat.equivalencytech.statics.ContainerStorage;
import io.github.sefiraat.equivalencytech.statics.Messages;
import io.github.sefiraat.equivalencytech.statics.SkullTextures;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TransmutationOrb {

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

    public TransmutationOrb(EMCShopMiragEdge plugin) {

        this.plugin = plugin;

        ConfigStrings c = plugin.getConfigMainClass().getStrings();

        item = SkullCreator. itemFromBase64(SkullTextures.ITEM_TRANSMUTATION_ORB);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(Messages.THEME_EMC_PURPLE + c.getItemTransmutationOrbName());
        List<String> lore = new ArrayList<>();
        for (String s : c.getItemTransmutationOrbLore()) {
            lore.add(Messages.THEME_PASSIVE_GRAY + s);
        }
        lore.add("");
        lore.add(Messages.THEME_CLICK_INSTRUCTION + c.getItemRightClickToOpen());
        im.setLore(lore);
        item.setItemMeta(im);

        ContainerStorage.setItemID(item, plugin,"TORB");
        ContainerStorage.makeTransmutationOrb(item, plugin);
        ContainerStorage.makeCraftable(item, plugin);

    }

}
