package io.github.sefiraat.equivalencytech.misc;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;

public class ManagerSupportedPlugins {

    private final EMCShopMiragEdge plugin;
    private boolean isInstalledEMC2;

    public boolean isInstalledEMC2() {
        return isInstalledEMC2;
    }

    public ManagerSupportedPlugins(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
        emc2();
    }

    private void emc2() {
        isInstalledEMC2 = plugin.getServer().getPluginManager().isPluginEnabled("EMC2");
    }
}