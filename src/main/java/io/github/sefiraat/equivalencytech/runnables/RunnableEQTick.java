package io.github.sefiraat.equivalencytech.runnables;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.configuration.ConfigMain;
import io.github.sefiraat.equivalencytech.misc.Utils;
import io.github.sefiraat.equivalencytech.statics.ContainerStorage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;
import java.util.HashMap;

public class RunnableEQTick extends BukkitRunnable {

    public final EMCShopMiragEdge plugin;

    public RunnableEQTick(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        processDChests();
        processCChests();
    }

    private void processDChests() {
        for (Location location : ConfigMain.getAllDChestLocations(plugin)) {
            if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                int chestId = ConfigMain.getDChestIdStore(plugin, location);
                String playerUUID = ConfigMain.getOwnerDChest(plugin, chestId);

                BlockState state = location.getBlock().getState();

                if (!(state instanceof Chest)) {
                    EMCShopMiragEdge.getInstance().getLogger().warning(getErrorDissolutionChest(chestId, location));
                    continue;
                }

                Chest chest = (Chest) location.getBlock().getState();
                Inventory inventory = chest.getBlockInventory();
                for (ItemStack itemStack : inventory.getContents()) {
                    if (itemStack != null && itemStack.getType() != Material.AIR) {
                        boolean isEQ = ContainerStorage.isCraftable(itemStack, plugin);
                        Material material = itemStack.getType();
                        Double emcValue = Utils.roundDown((Utils.getEMC(plugin, itemStack) / 100) * 150, 2);
                        if (emcValue != null && Utils.canBeSynth(plugin, itemStack)) {
                            String entryName;
                            if (isEQ) {
                                entryName = Utils.eqNameConfig(itemStack.getItemMeta().getDisplayName());
                            } else {
                                entryName = material.toString();
                            }
                            if (!ConfigMain.getLearnedItems(plugin, playerUUID).contains(entryName)) {
                                ConfigMain.addLearnedItem(plugin, playerUUID, entryName);
                            }
                            ConfigMain.addPlayerEmc(plugin, playerUUID, emcValue);
                            itemStack.setAmount(itemStack.getAmount() - 1);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void processCChests() {
        for (Location location : ConfigMain.getAllCChestLocations(plugin)) {
            if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                int chestId = ConfigMain.getCChestIdStore(plugin, location);
                String playerUUID = ConfigMain.getOwnerCChest(plugin, chestId);

                BlockState state = location.getBlock().getState();

                if (!(state instanceof Chest)) {
                    EMCShopMiragEdge.getInstance().getLogger().warning(getErrorCondensateChest(chestId, location));
                    continue;
                }

                Chest chest = (Chest) location.getBlock().getState();
                Inventory inventory = chest.getBlockInventory();
                ItemStack itemStack = ConfigMain.getCChestItem(plugin, chestId);
                if (itemStack != null) {
                    Double emcValue = Utils.getEMC(plugin, itemStack);
                    if (emcValue != null) {
                        Double playerEmc = ConfigMain.getPlayerEmc(plugin, playerUUID);
                        if (playerEmc >= emcValue) {
                            HashMap<Integer, ItemStack> failed = inventory.addItem(itemStack);
                            if (failed.isEmpty()) {
                                ConfigMain.removePlayerEmc(plugin, playerUUID, emcValue);
                            }
                        }
                    }
                }
            }
        }
    }

    public static String getErrorDissolutionChest(int chestId, Location location) {
        return MessageFormat.format(
                "A Dissolution chest (ID: {0}has been removed wrongly. " +
                        "Either replace with a vanilla chest (location : {1}) " +
                        "or remove from dissolution_chests.yml",
                chestId,
                location.toString()
        );
    }

    public static String getErrorCondensateChest(int chestId, Location location) {
        return MessageFormat.format(
                "A Condensate chest (ID: {0}has been removed wrongly. " +
                        "Either replace with a vanilla chest (location : {1})  " +
                        "or remove from condensate_chests.yml",
                chestId,
                location.toString()
        );
    }
}