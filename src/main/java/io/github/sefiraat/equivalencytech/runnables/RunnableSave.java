package io.github.sefiraat.equivalencytech.runnables;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import org.bukkit.scheduler.BukkitRunnable;

public class RunnableSave extends BukkitRunnable {

    public final EMCShopMiragEdge plugin;

    public RunnableSave(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getConfigMainClass().saveAdditionalConfigs();
    }
}