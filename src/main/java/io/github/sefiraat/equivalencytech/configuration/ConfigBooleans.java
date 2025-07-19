package io.github.sefiraat.equivalencytech.configuration;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigBooleans {

    FileConfiguration configuration;

    public boolean getDebuggingLogs() {
        return configuration.getBoolean("DEBUG.SHOW_DEBUGGING_LOGS");
    }
    public boolean getEMC2DisableWarning() {return configuration.getBoolean("DISABLE_EMC2_WARNING");}

    public ConfigBooleans(EMCShopMiragEdge plugin) {
        configuration = plugin.getConfig();
    }

}
