package io.github.sefiraat.equivalencytech.misc;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;


public class ManagerSupportedPlugins {

    private final EMCShopMiragEdge plugin;

    private boolean isInstalledSlimefun;
    private boolean isInstalledEMC2;
    private SlimefunEQAddon slimefunAddon;

    public boolean isInstalledSlimefun() {
        return isInstalledSlimefun;
    }

    public boolean isInstalledEMC2() {
        return isInstalledEMC2;
    }

    public SlimefunEQAddon getSlimefunAddon() {
        return slimefunAddon;
    }

    public ManagerSupportedPlugins(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
        slimefun();
        emc2();
    }

    private void slimefun() {
        isInstalledSlimefun = plugin.getServer().getPluginManager().isPluginEnabled("Slimefun");
        if (isInstalledSlimefun) {
            slimefunAddon = new SlimefunEQAddon(plugin);
        }
    }

    private void emc2() {
        isInstalledEMC2 = plugin.getServer().getPluginManager().isPluginEnabled("EMC2");
    }
}
