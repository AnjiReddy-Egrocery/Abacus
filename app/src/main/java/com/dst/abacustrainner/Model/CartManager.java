package com.dst.abacustrainner.Model;

import java.util.HashSet;

public class CartManager {
    private static CartManager instance;
    private final HashSet<String> selectedLevels = new HashSet<>();

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addLevel(String level) {
        selectedLevels.add(level);
    }

    public void removeLevel(String level) {
        selectedLevels.remove(level);
    }

    public boolean isSelected(String level) {
        return selectedLevels.contains(level);
    }

    public int getCount() {
        return selectedLevels.size();
    }

    public HashSet<String> getSelectedLevels() {
        return selectedLevels;
    }

    public void clear() {
        selectedLevels.clear();
    }
}
