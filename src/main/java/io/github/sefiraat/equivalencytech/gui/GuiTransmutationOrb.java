package io.github.sefiraat.equivalencytech.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.configuration.ConfigMain;
import io.github.sefiraat.equivalencytech.misc.Utils;
import io.github.sefiraat.equivalencytech.statics.ContainerStorage;
import io.github.sefiraat.equivalencytech.statics.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GuiTransmutationOrb extends PaginatedGui {

    public final EMCShopMiragEdge plugin;
    public final Player player;

    protected static final List<Integer> ARRAY_FILLER_SLOTS = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8, 45, 47, 48, 50, 51, 53);
    protected static final Integer INFO_SLOT = 4;
    protected static final Integer INPUT_SLOT = 49;
    protected static final Integer PAGE_SIZE = 36;

    public GuiTransmutationOrb(int rows, int pageSize, @NotNull String title, EMCShopMiragEdge plugin, Player player) {
        super(rows, pageSize, title);
        this.plugin = plugin;
        this.player = player;
    }

    /**
     * 构建转换球GUI
     *
     * @param plugin 插件实例
     * @param player 打开GUI的玩家
     * @return 构建好的GUI实例
     */
    public static GuiTransmutationOrb buildGui(EMCShopMiragEdge plugin, Player player) {
        int backSlot = 46;
        int forwardSlot = 52;

        GuiTransmutationOrb gui = new GuiTransmutationOrb(
                6,
                PAGE_SIZE,
                Messages.THEME_EMC_PURPLE + "等价交换商店",
                plugin,
                player
        );

        gui.setItem(ARRAY_FILLER_SLOTS, GUIItems.guiOrbBorder(plugin));
        gui.setItem(INFO_SLOT, GUIItems.guiOrbInfo(plugin, player));

        List<String> learnedItems = ConfigMain.getLearnedItems(plugin, player.getUniqueId().toString());
        int leftOverSlots = PAGE_SIZE - (learnedItems.size() % PAGE_SIZE);

        // 添加已学习物品到GUI
        for (String itemId : learnedItems) {
            ItemStack itemStack;
            GuiItem guiItem;
            boolean isVanilla = !plugin.getEqItems().getEqItemMap().containsKey(itemId);

            if (isVanilla) {
                itemStack = new ItemStack(Material.valueOf(itemId));
            } else {
                itemStack = plugin.getEqItems().getEqItemMap().get(itemId).clone();
            }

            // 跳过无效的EMC物品
            if (Utils.getEMC(plugin, itemStack) == null) {
                leftOverSlots++;
                continue;
            }

            // 本地化物品显示名称
            itemStack = localizeItem(plugin, itemStack, isVanilla);

            guiItem = new GuiItem(itemStack);
            guiItem.setAction(event -> emcItemClicked(event, plugin));
            gui.addItem(guiItem);
        }

        // 填充剩余空格
        for (int i = 0; i < leftOverSlots; i++) {
            gui.addItem(GUIItems.guiOrbFiller(plugin));
        }

        // 设置输入槽
        setInputSlot(plugin, gui);

        // 设置翻页按钮
        gui.setItem(backSlot, ItemBuilder.from(Material.PAPER)
                .name(Component.text("§e上一页"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    gui.previous();
                }));

        gui.setItem(forwardSlot, ItemBuilder.from(Material.PAPER)
                .name(Component.text("§e下一页"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    gui.next();
                }));

        // 设置拖拽和点击行为
        gui.setDragAction(event -> event.setCancelled(true));
        gui.setDefaultClickAction(event -> {
            if (event.isShiftClick() &&
                    event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY &&
                    !event.getClickedInventory().equals(event.getInventory())) {
                inputItemAction(event, plugin, gui, true);
            }
            event.setCancelled(true);
        });

        return gui;
    }

    /**
     * 本地化物品显示名称（中文）
     *
     * @param plugin 插件实例
     * @param itemStack 原始物品堆
     * @param isVanilla 是否原版物品
     * @return 本地化后的物品
     */
    private static ItemStack localizeItem(EMCShopMiragEdge plugin, ItemStack itemStack, boolean isVanilla) {
        ItemStack displayStack = itemStack.clone();
        ItemMeta meta = displayStack.getItemMeta();
        if (meta == null) return displayStack;

        double emc = Utils.getEMC(plugin, itemStack);
        TextComponent emcLine = Component.text("EMC: " + emc)
                .color(NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false);
        TextComponent leftLine = Component.text("左键兑换一个")
                .color(NamedTextColor.BLUE)
                .decoration(TextDecoration.ITALIC, false);
        TextComponent rightLine = Component.text("右键兑换一组")
                .color(NamedTextColor.BLUE)
                .decoration(TextDecoration.ITALIC, false);

        // 保留原有Lore或创建新列表
        List<Component> lore = meta.hasLore() ?
                new ArrayList<>(meta.lore()) :
                new ArrayList<>();
        lore.add(emcLine);
        lore.add(leftLine);
        lore.add(rightLine);

        // 处理物品名称
        if (isVanilla) {
            // 为原版物品创建中文翻译组件
            TranslatableComponent translatedName = Component.translatable(itemStack.translationKey());
            meta.displayName(translatedName);
        } else if (!meta.hasDisplayName()) {
            // 自定义物品若无名称则使用材质翻译键
            meta.displayName(Component.translatable(itemStack.translationKey()));
        }

        meta.lore(lore);
        displayStack.setItemMeta(meta);
        return displayStack;
    }

    /**
     * 打开GUI给玩家
     */
    public void openToPlayer() {
        this.open(player);
    }

    private static void setInputSlot(EMCShopMiragEdge plugin, GuiTransmutationOrb gui) {
        gui.addSlotAction(INPUT_SLOT, event -> {
            inputItemAction(event, plugin, gui, false);
            event.setCancelled(true);
        });
    }

    private static void inputItemAction(InventoryClickEvent e, EMCShopMiragEdge plugin, PaginatedGui gui, boolean shifted) {
        Player player = (Player) e.getWhoClicked();
        ItemStack itemStack = shifted ? e.getCurrentItem() : player.getItemOnCursor();

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        boolean isEQ = ContainerStorage.isCraftable(itemStack, plugin);

        // 检查物品是否合法
        if (itemStack.hasItemMeta() && !isEQ) {
            player.sendMessage(Messages.messageGuiItemMeta(plugin));
            return;
        }

        Double emcValue = Utils.getEMC(plugin, itemStack);
        if (emcValue == null) {
            player.sendMessage(Messages.msgCmdEmcNone(plugin));
            return;
        }

        Material material = itemStack.getType();
        double totalEmc = emcValue * itemStack.getAmount();
        String entryName = isEQ ? Utils.eqNameConfig(itemStack.getItemMeta().getDisplayName()) : material.toString();
        boolean learnedNew = false;

        // 检查是否新物品
        if (!ConfigMain.getLearnedItems(plugin, player.getUniqueId().toString()).contains(entryName)) {
            ConfigMain.addLearnedItem(plugin, player.getUniqueId().toString(), entryName);
            player.sendMessage(Messages.messageGuiItemLearned(plugin));
            learnedNew = true;
        }

        // 添加EMC
        ConfigMain.addPlayerEmc(plugin, player, emcValue, totalEmc, itemStack.getAmount());
        itemStack.setAmount(0);

        // 如果学习了新物品，关闭并重新打开GUI
        if (learnedNew) {
            gui.close(player);
            GuiTransmutationOrb newGui = buildGui(plugin, player);
            newGui.openToPlayer();
        }
    }

    private static void emcItemClicked(InventoryClickEvent e, EMCShopMiragEdge plugin) {
        e.setCancelled(true);
        switch (e.getClick()) {
            case LEFT:
                emcWithdrawOne(e, plugin);
                break;
            case RIGHT:
                emcWithdrawStack(e, plugin);
                break;
            default:
                break;
        }
    }

    private static void emcWithdrawOne(InventoryClickEvent e, EMCShopMiragEdge plugin) {
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Messages.messageGuiNoSpace(plugin));
            return;
        }

        boolean isEQ = ContainerStorage.isCraftable(clickedItem, plugin);
        double playerEmc = ConfigMain.getPlayerEmc(plugin, player);
        Double emcValue = Utils.getEMC(plugin, clickedItem);

        if (emcValue == null) return;

        String itemName = isEQ ? Utils.eqNameConfig(clickedItem.getItemMeta().getDisplayName()) : clickedItem.getType().toString();

        if (playerEmc >= emcValue) {
            ItemStack itemStack = isEQ ?
                    plugin.getEqItems().getEqItemMap().get(itemName).clone() :
                    new ItemStack(clickedItem.getType());

            player.getInventory().addItem(itemStack);
            ConfigMain.removePlayerEmc(plugin, player, emcValue);
            player.sendMessage(Messages.messageGuiEmcRemoved(plugin, player, emcValue, emcValue, 1));
        } else {
            player.sendMessage(Messages.messageGuiEmcNotEnough(plugin, player));
        }
    }

    private static void emcWithdrawStack(InventoryClickEvent e, EMCShopMiragEdge plugin) {
        Player player = (Player) e.getWhoClicked();
        double playerEmc = ConfigMain.getPlayerEmc(plugin, player);
        ItemStack clickedItem = e.getCurrentItem();
        Material material = clickedItem.getType();

        boolean isEQ = ContainerStorage.isCraftable(clickedItem, plugin);
        Double emcValue = Utils.getEMC(plugin, clickedItem);

        if (emcValue == null) return;

        String itemName = isEQ ?
                Utils.eqNameConfig(clickedItem.getItemMeta().getDisplayName()) :
                material.toString();

        int maxStack = clickedItem.getMaxStackSize();
        int amount = maxStack;
        double emcValueStack = emcValue * amount;

        // 检查背包空间
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Messages.messageGuiNoSpace(plugin));
            return;
        }

        // 检查EMC是否足够
        if (playerEmc < emcValueStack) {
            double maxPossible = Math.floor(playerEmc / emcValue);
            amount = (int) maxPossible;
            emcValueStack = emcValue * amount;
        }

        if (amount == 0) {
            player.sendMessage(Messages.messageGuiEmcNotEnough(plugin, player));
            return;
        }

        // 创建物品
        ItemStack itemStack = isEQ ?
                plugin.getEqItems().getEqItemMap().get(itemName).clone() :
                new ItemStack(material);

        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        ConfigMain.removePlayerEmc(plugin, player, emcValueStack);
        player.sendMessage(Messages.messageGuiEmcRemoved(plugin, player, emcValue, emcValueStack, amount));
    }
}