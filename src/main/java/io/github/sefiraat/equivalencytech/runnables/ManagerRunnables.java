package io.github.sefiraat.equivalencytech.runnables;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;

public class ManagerRunnables {

    private final EMCShopMiragEdge plugin;

    private RunnableSave runnableSave;
    private RunnableEQTick runnableEQTick;

    public RunnableSave getRunnableSave() {
        return runnableSave;
    }

    public RunnableEQTick getRunnableEQTick() {
        return runnableEQTick;
    }

    public ManagerRunnables(EMCShopMiragEdge plugin) {
        this.plugin = plugin;
        setupRunnables();
    }

    private void setupRunnables() {

        runnableSave = new RunnableSave(plugin);
        runnableSave.runTaskTimer(plugin, 0, 100L);

        runnableEQTick = new RunnableEQTick(plugin);
        runnableEQTick.runTaskTimer(plugin, 0, 20L);

    }


}
