package io.github.sefiraat.equivalencytech.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import io.github.sefiraat.equivalencytech.gui.GuiTransmutationOrb;
import io.github.sefiraat.equivalencytech.misc.Utils;
import io.github.sefiraat.equivalencytech.statics.ContainerStorage;
import io.github.sefiraat.equivalencytech.statics.Messages;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("emcshop")
@Description("EMC商店主命令")
public class Commands extends BaseCommand {

    private final EMCShopMiragEdge plugin;

    public EMCShopMiragEdge getPlugin() {
        return plugin;
    }

    public Commands(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onDefault(CommandSender sender) {
        if (sender instanceof Player) {
            sender.sendMessage(Messages.msgCmdSubcommand(plugin));
        }
    }

    @Subcommand("gui")
    @Description("打开EMC主菜单界面")
    public void onMain(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家才能执行此命令");
            return;
        }
        Player player = (Player) sender;
        openTransmutationOrbMenu(player);
    }

    /**
     * 打开转换球菜单给玩家
     * @param player 要打开菜单的玩家
     */
    private void openTransmutationOrbMenu(Player player) {
        // 构建并打开转换球菜单
        GuiTransmutationOrb gui = GuiTransmutationOrb.buildGui(plugin, player);
        gui.openToPlayer(); // 使用新方法打开
    }

    @Subcommand("itememc")
    @Description("查看手持物品的EMC值")
    public class ItemEmc extends BaseCommand {

        @Default
        public void onDefault(CommandSender sender) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ItemStack i = player.getInventory().getItemInMainHand();
                if (i.getType() != Material.AIR) {
                    if (ContainerStorage.isCraftable(i, plugin)) {
                        if (plugin.getEmcDefinitions().getEmcEQ().containsKey(i.getItemMeta().getDisplayName())) {
                            player.sendMessage(Messages.msgCmdEmcDisplay(i.getItemMeta().getDisplayName(), Utils.getEMC(plugin, i)));
                            player.sendMessage(Messages.msgCmdEmcDisplayStack(i.getItemMeta().getDisplayName(), i.getAmount(), Utils.getEMC(plugin, i) * i.getAmount()));
                        } else {
                            player.sendMessage(Messages.msgCmdEmcNone(plugin));
                        }
                        return;
                    }
                    if (plugin.getEmcDefinitions().getEmcExtended().containsKey(i.getType())) {
                        player.sendMessage(Messages.msgCmdEmcDisplay(i.getType(), Utils.getEMC(plugin, i)));
                        player.sendMessage(Messages.msgCmdEmcDisplayStack(i.getType(), i.getAmount(), Utils.getEMC(plugin, i) * i.getAmount()));
                    } else {
                        player.sendMessage(Messages.msgCmdEmcNone(plugin));
                    }
                } else {
                    player.sendMessage(Messages.msgCmdEmcMustHold(plugin));
                }
            }
        }
    }

    @Subcommand("emc")
    @Description("查看你的EMC值")
    public class Emc extends BaseCommand {

        @Default
        public void onDefault(CommandSender sender) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(Messages.messageCommandEmc(plugin, player));
            }
        }
    }

    @Subcommand("giveitem")
    @CommandPermission("emcshop.admin")
    @Description("获取EMC商店物品")
    public class GiveItem extends BaseCommand {

        @Default
        public void onDefault(CommandSender sender) {
            if (sender instanceof Player) {
                sender.sendMessage(Messages.messageCommandSelectItem(plugin));
            }
        }

        @Subcommand("transmutationorb")
        @CommandCompletion("@players")
        public void onGiveItemOrb(CommandSender sender, OnlinePlayer player) {
            if (sender instanceof Player) {
                Utils.givePlayerOrb(plugin, player.getPlayer());
                sender.sendMessage("§a已给予玩家 " + player.getPlayer().getName() + " 转化宝珠");
            }
        }

        @Subcommand("dissolutionchest")
        @CommandCompletion("@players")
        public void onGiveItemDChest(CommandSender sender, OnlinePlayer player) {
            if (sender instanceof Player) {
                Utils.givePlayerDChest(plugin, player.getPlayer());
                sender.sendMessage("§a已给予玩家 " + player.getPlayer().getName() + " 分解箱");
            }
        }

        @Subcommand("condensatechest")
        @CommandCompletion("@players")
        public void onGiveItemCChest(CommandSender sender, OnlinePlayer player) {
            if (sender instanceof Player) {
                Utils.givePlayerCChest(plugin, player.getPlayer());
                sender.sendMessage("§a已给予玩家 " + player.getPlayer().getName() + " 合成箱");
            }
        }
    }
}