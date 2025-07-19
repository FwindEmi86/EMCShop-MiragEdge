package io.github.sefiraat.equivalencytech.listeners;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;

public class ManagerEvents {

    private final OrbOpenListener orbOpenListener;
    private final CraftListener craftListener;
    private final PlayerJoinListener playerJoinListener;
    private final ChestPlaceListener chestPlaceListener;
    private final BlockPlaceListener blockPlaceListener;

    public OrbOpenListener getOrbOpenListener() {
        return orbOpenListener;
    }

    public CraftListener getCraftListener() {
        return craftListener;
    }

    public ChestPlaceListener getChestPlaceListener() {
        return chestPlaceListener;
    }

    public PlayerJoinListener getPlayerJoinListener() {
        return playerJoinListener;
    }

    public BlockPlaceListener getBlockPlaceListener() {
        return blockPlaceListener;
    }

    public ManagerEvents(EMCShopMiragEdge plugin) {
        orbOpenListener = new OrbOpenListener(plugin);
        craftListener = new CraftListener(plugin);
        playerJoinListener = new PlayerJoinListener(plugin);
        chestPlaceListener = new ChestPlaceListener(plugin);
        blockPlaceListener = new BlockPlaceListener(plugin);
    }

}
