package io.github.sefiraat.equivalencytech.misc;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    private double emcValue;
    private int version;
    private Map<String, Integer> items = new HashMap<>();

    public double getEmcValue() {
        return emcValue;
    }

    public void setEmcValue(double emcValue) {
        this.emcValue = emcValue;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }
}