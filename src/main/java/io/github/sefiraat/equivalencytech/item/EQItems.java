package io.github.sefiraat.equivalencytech.item;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.item.builders.AeternalisFuel;
import io.github.sefiraat.equivalencytech.item.builders.AlchemicalCoal;
import io.github.sefiraat.equivalencytech.item.builders.CondensatorChest;
import io.github.sefiraat.equivalencytech.item.builders.DarkMatter;
import io.github.sefiraat.equivalencytech.item.builders.DissolutionChest;
import io.github.sefiraat.equivalencytech.item.builders.MobiusFuel;
import io.github.sefiraat.equivalencytech.item.builders.RedMatter;
import io.github.sefiraat.equivalencytech.item.builders.TransmutationOrb;
import io.github.sefiraat.equivalencytech.misc.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EQItems {

    private final EMCShopMiragEdge plugin;
    private final TransmutationOrb transmutationOrb;
    private final AlchemicalCoal alchemicalCoal;
    private final MobiusFuel mobiusFuel;
    private final AeternalisFuel aeternalisFuel;
    private final DarkMatter darkMatter;
    private final RedMatter redMatter;
    private final DissolutionChest dissolutionChest;
    private final CondensatorChest condensatorChest;
    private final Map<String, ItemStack> eqItemMap;

    public EMCShopMiragEdge getPlugin() {
        return plugin;
    }

    public TransmutationOrb getTransmutationOrb() {
        return transmutationOrb;
    }

    public AlchemicalCoal getAlchemicalCoal() {
        return alchemicalCoal;
    }

    public MobiusFuel getMobiusFuel() {
        return mobiusFuel;
    }

    public AeternalisFuel getAeternalisFuel() {
        return aeternalisFuel;
    }

    public DarkMatter getDarkMatter() {
        return darkMatter;
    }

    public RedMatter getRedMatter() {
        return redMatter;
    }

    public DissolutionChest getDissolutionChest() {
        return dissolutionChest;
    }

    public CondensatorChest getCondensatorChest() {
        return condensatorChest;
    }

    public Map<String, ItemStack> getEqItemMap() {
        return eqItemMap;
    }

    public EQItems(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
        transmutationOrb = new TransmutationOrb(plugin);
        alchemicalCoal = new AlchemicalCoal(plugin);
        mobiusFuel = new MobiusFuel(plugin);
        aeternalisFuel = new AeternalisFuel(plugin);
        darkMatter = new DarkMatter(plugin);
        redMatter = new RedMatter(plugin);
        dissolutionChest = new DissolutionChest(plugin);
        condensatorChest = new CondensatorChest(plugin);

        eqItemMap = new HashMap<>();
        eqItemMap.put(Utils.eqNameConfig(transmutationOrb.getItem().getItemMeta().getDisplayName()), transmutationOrb.getItemClone());
        eqItemMap.put(Utils.eqNameConfig(alchemicalCoal.getItem().getItemMeta().getDisplayName()), alchemicalCoal.getItemClone());
        eqItemMap.put(Utils.eqNameConfig(mobiusFuel.getItem().getItemMeta().getDisplayName()), mobiusFuel.getItemClone());
        eqItemMap.put(Utils.eqNameConfig(aeternalisFuel.getItem().getItemMeta().getDisplayName()), aeternalisFuel.getItemClone());
        eqItemMap.put(Utils.eqNameConfig(darkMatter.getItem().getItemMeta().getDisplayName()), darkMatter.getItemClone());
        eqItemMap.put(Utils.eqNameConfig(redMatter.getItem().getItemMeta().getDisplayName()), redMatter.getItemClone());
        eqItemMap.put(Utils.eqNameConfig(dissolutionChest.getItem().getItemMeta().getDisplayName()), dissolutionChest.getItemClone());
        eqItemMap.put(Utils.eqNameConfig(condensatorChest.getItem().getItemMeta().getDisplayName()), condensatorChest.getItemClone());

    }


}
