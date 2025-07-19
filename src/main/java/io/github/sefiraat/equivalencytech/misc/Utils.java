package io.github.sefiraat.equivalencytech.misc;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.item.builders.CondensatorChest;
import io.github.sefiraat.equivalencytech.item.builders.DissolutionChest;
import io.github.sefiraat.equivalencytech.item.builders.TransmutationOrb;
import io.github.sefiraat.equivalencytech.statics.ContainerStorage;
import io.github.sefiraat.equivalencytech.statics.Messages;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static Double getEMC(EMCShopMiragEdge plugin, ItemStack itemStack) {
        if (ContainerStorage.isCraftable(itemStack, plugin)) {
            ItemStack eqStack = plugin.getEqItems().getEqItemMap().get(eqNameConfig(itemStack.getItemMeta().getDisplayName()));
            return plugin.getEmcDefinitions().getEmcEQ().get(eqStack.getItemMeta().getDisplayName());
        } else {
            return plugin.getEmcDefinitions().getEmcExtended().get(itemStack.getType());
        }
    }

    public static String eqNameConfig(String name) {
        return ChatColor.stripColor(name.replace(" ","_"));
    }

    public static String toTitleCase(String string) {
        final char[] delimiters = { ' ', '_' };
        return WordUtils.capitalizeFully(string, delimiters);
    }

    public static String materialFriendlyName(Material m) {
        return toTitleCase(m.name().replace("_", " "));
    }

    public static void givePlayerOrb(EMCShopMiragEdge plugin, Player player) {
        TransmutationOrb i = plugin.getEqItems().getTransmutationOrb();
        player.getInventory().addItem(i.getItemClone());
        player.sendMessage(Messages.messageCommandItemGiven(plugin, i.getItem().getItemMeta().getDisplayName()));
    }

    public static void givePlayerDChest(EMCShopMiragEdge plugin, Player player) {
        DissolutionChest i = plugin.getEqItems().getDissolutionChest();
        player.getInventory().addItem(i.getItemClone());
        player.sendMessage(Messages.messageCommandItemGiven(plugin, i.getItem().getItemMeta().getDisplayName()));
    }

    public static void givePlayerCChest(EMCShopMiragEdge plugin, Player player) {
        CondensatorChest i = plugin.getEqItems().getCondensatorChest();
        player.getInventory().addItem(i.getItemClone());
        player.sendMessage(Messages.messageCommandItemGiven(plugin, i.getItem().getItemMeta().getDisplayName()));
    }

    public static double roundDown(double number, int places) {
        BigDecimal value = BigDecimal.valueOf(number);
        value = value.setScale(places, RoundingMode.DOWN);
        return value.doubleValue();
    }

    public static int totalRecipes(EMCShopMiragEdge plugin) {
        int recExtended = plugin.getEmcDefinitions().getEmcExtended().size();
        int recEQ = plugin.getEmcDefinitions().getEmcEQ().size();
        return recExtended + recEQ;
    }

    public static boolean canBeSynth(EMCShopMiragEdge plugin, ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            return ContainerStorage.isCraftable(itemStack, plugin);
        } else {
            return true;
        }
    }
}