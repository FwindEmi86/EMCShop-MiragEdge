package io.github.sefiraat.equivalencytech.listeners;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.configuration.ConfigMain;
import io.github.sefiraat.equivalencytech.misc.Utils;
import io.github.sefiraat.equivalencytech.statics.ContainerStorage;
import io.github.sefiraat.equivalencytech.statics.Messages;
import io.github.sefiraat.equivalencytech.misc.DatabaseManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChestPlaceListener implements Listener {

    private final EMCShopMiragEdge plugin;
    private final DatabaseManager databaseManager;
    private final boolean databaseEnabled;

    public ChestPlaceListener(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
        this.databaseEnabled = plugin.isUsingDatabase() && databaseManager.isConnected();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChestPlace(BlockPlaceEvent e) {
        if (e.getBlockPlaced().getType() == Material.CHEST) {
            boolean isDis = isDis(e);
            boolean isCon = isCon(e);
            if (e.isCancelled()) {
                return;
            }
            if (isDis) {
                placeDisChest(e);
                return;
            }
            if (isCon) {
                placeConChest(e);
                return;
            }
            if (nearbyEMCChest(e)) {
                e.getPlayer().sendMessage(Messages.messageEventEMCChestPlace(plugin));
                e.setCancelled(true);
            }
        }
    }

    private void placeConChest(BlockPlaceEvent e) {
        if (noNearbyChest(e.getBlockPlaced())) {
            Location location = e.getBlockPlaced().getLocation();
            Player player = e.getPlayer();
            UUID owner = player.getUniqueId();

            if (databaseEnabled) {
                // 使用数据库存储
                databaseManager.saveChest(location, DatabaseManager.ChestType.CONDENSATION, owner, null);
            } else {
                // 原配置文件存储
                ConfigMain.addCChestStore(plugin, location);
                ConfigMain.setupCChest(plugin, ConfigMain.getCChestIdStore(plugin, location), player);
            }
        } else {
            e.setCancelled(true);
        }
    }

    private void placeDisChest(BlockPlaceEvent e) {
        if (noNearbyChest(e.getBlockPlaced())) {
            Location location = e.getBlockPlaced().getLocation();
            Player player = e.getPlayer();
            UUID owner = player.getUniqueId();

            if (databaseEnabled) {
                // 使用数据库存储
                databaseManager.saveChest(location, DatabaseManager.ChestType.DISSOLUTION, owner, null);
            } else {
                // 原配置文件存储
                ConfigMain.addDChestStore(plugin, location);
                ConfigMain.setupDChest(plugin, ConfigMain.getDChestIdStore(plugin, location), player);
            }
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChestBreak(BlockBreakEvent e) {
        Location location = e.getBlock().getLocation();

        if (databaseEnabled) {
            // 数据库模式
            DatabaseManager.ChestData chestData = databaseManager.getChest(location);
            if (chestData != null) {
                handleChestBreak(e, location, chestData);
            }
        } else {
            // 原配置文件模式
            Integer disID = ConfigMain.getDChestIdStore(plugin, location);
            Integer conID = ConfigMain.getCChestIdStore(plugin, location);

            if (disID != null || conID != null) {
                handleChestBreak(e, location, disID, conID);
            }
        }
    }

    private void handleChestBreak(BlockBreakEvent e, Location location, DatabaseManager.ChestData chestData) {
        e.setCancelled(true);
        Chest chest = (Chest) e.getBlock().getState();
        Inventory inventory = chest.getBlockInventory();

        // 清空箱子内容并掉落物品
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                e.getBlock().getWorld().dropItemNaturally(location, itemStack);
                itemStack.setAmount(0);
            }
        }

        // 设置方块为空气
        e.getBlock().setType(Material.AIR);

        // 根据箱子类型掉落对应的EMC箱子物品
        switch (chestData.getType()) {
            case DISSOLUTION:
                e.getBlock().getWorld().dropItemNaturally(location, plugin.getEqItems().getDissolutionChest().getItemClone());
                break;
            case CONDENSATION:
                e.getBlock().getWorld().dropItemNaturally(location, plugin.getEqItems().getCondensatorChest().getItemClone());
                break;
        }

        // 从数据库删除箱子记录
        databaseManager.removeChest(location);
    }

    private void handleChestBreak(BlockBreakEvent e, Location location, Integer disID, Integer conID) {
        e.setCancelled(true);
        Chest chest = (Chest) e.getBlock().getState();
        Inventory inventory = chest.getBlockInventory();

        // 清空箱子内容并掉落物品
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                e.getBlock().getWorld().dropItemNaturally(location, itemStack);
                itemStack.setAmount(0);
            }
        }

        // 设置方块为空气
        e.getBlock().setType(Material.AIR);

        // 根据箱子类型处理
        if (disID != null) {
            e.getBlock().getWorld().dropItemNaturally(location, plugin.getEqItems().getDissolutionChest().getItemClone());
            ConfigMain.removeDChestStore(plugin, disID);
            ConfigMain.removeDChest(plugin, disID);
        }
        if (conID != null) {
            e.getBlock().getWorld().dropItemNaturally(location, plugin.getEqItems().getCondensatorChest().getItemClone());
            ConfigMain.removeCChestStore(plugin, conID);
            ConfigMain.removeCChest(plugin, conID);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChestInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null) {
            Location location = e.getClickedBlock().getLocation();
            Player player = e.getPlayer();

            if (databaseEnabled) {
                // 数据库模式
                DatabaseManager.ChestData chestData = databaseManager.getChest(location);
                if (chestData != null) {
                    handleChestInteract(e, chestData, player);
                }
            } else {
                // 原配置文件模式
                Integer disID = ConfigMain.getDChestIdStore(plugin, location);
                Integer conID = ConfigMain.getCChestIdStore(plugin, location);

                if (disID != null) {
                    handleDissolutionChestInteract(e, disID, player);
                }
                if (conID != null) {
                    handleCondensationChestInteract(e, conID, player);
                }
            }
        }
    }

    private void handleChestInteract(PlayerInteractEvent e, DatabaseManager.ChestData chestData, Player player) {
        switch (chestData.getType()) {
            case DISSOLUTION:
                if (isChestBeingOpened(e) && !hasPermission(chestData.getOwner(), player)) {
                    player.sendMessage(Messages.messageEventCantOpenNotOwner(plugin));
                    e.setCancelled(true);
                }
                break;
            case CONDENSATION:
                if (isChestBeingOpened(e)) {
                    // 打开凝聚箱子权限检查
                    if (!hasPermission(chestData.getOwner(), player)) {
                        player.sendMessage(Messages.messageEventCantOpenNotOwner(plugin));
                        e.setCancelled(true);
                    }
                } else if (isChestBeingSet(e)) {
                    setChest(e, chestData, player);
                }
                break;
        }
    }

    private void handleDissolutionChestInteract(PlayerInteractEvent e, Integer disID, Player player) {
        if (isChestBeingOpened(e) && !hasPermissionDChest(disID, player)) {
            player.sendMessage(Messages.messageEventCantOpenNotOwner(plugin));
            e.setCancelled(true);
        }
    }

    private void handleCondensationChestInteract(PlayerInteractEvent e, Integer conID, Player player) {
        if (isChestBeingOpened(e)) {
            openChest(e, conID, player);
        } else if (isChestBeingSet(e)) {
            setChest(e, conID, player);
        }
    }


    private boolean isDis(BlockPlaceEvent e) {
        return ContainerStorage.isDisChest(e.getItemInHand(), plugin);
    }

    private boolean isCon(BlockPlaceEvent e) {
        return ContainerStorage.isConChest(e.getItemInHand(), plugin);
    }

    private boolean nearbyEMCChest(BlockPlaceEvent e) {
        List<Block> blockList = new ArrayList<>();
        Block block = e.getBlockPlaced();
        blockList.add(block.getRelative(BlockFace.NORTH));
        blockList.add(block.getRelative(BlockFace.SOUTH));
        blockList.add(block.getRelative(BlockFace.EAST));
        blockList.add(block.getRelative(BlockFace.WEST));

        for (Block b : blockList) {
            if (b.getType() == Material.CHEST) {
                if (databaseEnabled) {
                    // 数据库检查
                    if (databaseManager.getChest(b.getLocation()) != null) {
                        return true;
                    }
                } else {
                    // 原配置文件检查
                    if (getDisId(b) != null || getConId(b) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean noNearbyChest(Block block) {
        List<Block> blockList = new ArrayList<>();
        blockList.add(block.getRelative(BlockFace.NORTH));
        blockList.add(block.getRelative(BlockFace.SOUTH));
        blockList.add(block.getRelative(BlockFace.EAST));
        blockList.add(block.getRelative(BlockFace.WEST));

        for (Block b : blockList) {
            if (b.getType() == Material.CHEST) {
                return false;
            }
        }
        return true;
    }

    private boolean hasPermission(UUID ownerUuid, Player player) {
        return ownerUuid.equals(player.getUniqueId()) ||
                player.isOp() ||
                player.hasPermission("equitech.bypass");
    }

    private boolean hasPermissionDChest(Integer disId, Player player) {
        return ConfigMain.isOwnerDChest(plugin, player, disId) ||
                player.isOp() ||
                player.hasPermission("equitech.bypass");
    }

    private boolean hasPermissionCChest(Integer conId, Player player) {
        return ConfigMain.isOwnerCChest(plugin, player, conId) ||
                player.isOp() ||
                player.hasPermission("equitech.bypass");
    }

    private Integer getDisId(Block block) {
        return ConfigMain.getDChestIdStore(plugin, block.getLocation());
    }

    private Integer getConId(Block block) {
        return ConfigMain.getCChestIdStore(plugin, block.getLocation());
    }

    private boolean isChestBeingOpened(PlayerInteractEvent e) {
        return e.getClickedBlock().getType() == Material.CHEST &&
                e.getAction() == Action.RIGHT_CLICK_BLOCK &&
                !e.getPlayer().isSneaking() &&
                e.useInteractedBlock() != Event.Result.DENY;
    }

    private boolean isChestBeingSet(PlayerInteractEvent e) {
        return e.getClickedBlock().getType() == Material.CHEST &&
                e.getAction() == Action.RIGHT_CLICK_BLOCK &&
                e.getPlayer().isSneaking() &&
                e.useInteractedBlock() != Event.Result.DENY;
    }

    private void openChest(PlayerInteractEvent e, Integer conId, Player player) {
        if (conId != null && !hasPermissionCChest(conId, player)) {
            player.sendMessage(Messages.messageEventCantOpenNotOwner(plugin));
            e.setCancelled(true);
        }
    }

    private void setChest(PlayerInteractEvent e, DatabaseManager.ChestData chestData, Player player) {
        Location location = e.getClickedBlock().getLocation();

        if (hasPermission(chestData.getOwner(), player)) {
            ItemStack itemStack = player.getInventory().getItemInMainHand().clone();

            if (itemStack.getType() == Material.AIR) {
                // 清除目标物品
                if (databaseEnabled) {
                    databaseManager.updateChestItem(location, null);
                } else {
                    ConfigMain.setCChestItem(plugin, getConId(e.getClickedBlock()), null);
                }
                player.sendMessage(Messages.messageEventItemUnset(plugin));
                return;
            }

            itemStack.setAmount(1);
            Double emcValue = Utils.getEMC(plugin, itemStack);

            if (Utils.canBeSynth(plugin, itemStack) && emcValue != null) {
                // 设置目标物品
                if (databaseEnabled) {
                    databaseManager.updateChestItem(location, itemStack);
                } else {
                    ConfigMain.setCChestItem(plugin, getConId(e.getClickedBlock()), itemStack);
                }
                player.sendMessage(Messages.messageEventItemSet(plugin));
            } else {
                player.sendMessage(Messages.msgCmdEmcNone(plugin));
            }
        } else {
            player.sendMessage(Messages.messageEventCantOpenNotOwner(plugin));
        }
        e.setCancelled(true);
    }

    private void setChest(PlayerInteractEvent e, Integer conId, Player player) {
        if (conId == null || hasPermissionCChest(conId, player)) {
            ItemStack itemStack = player.getInventory().getItemInMainHand().clone();
            if (itemStack.getType() == Material.AIR) {
                ConfigMain.setCChestItem(plugin, conId, null);
                player.sendMessage(Messages.messageEventItemUnset(plugin));
                return;
            }
            itemStack.setAmount(1);
            Double emcValue = Utils.getEMC(plugin, itemStack);
            if (Utils.canBeSynth(plugin, itemStack) && emcValue != null) {
                ConfigMain.setCChestItem(plugin, conId, itemStack);
                player.sendMessage(Messages.messageEventItemSet(plugin));
            } else {
                player.sendMessage(Messages.msgCmdEmcNone(plugin));
            }
        } else {
            player.sendMessage(Messages.messageEventCantOpenNotOwner(plugin));
        }
        e.setCancelled(true);
    }
}