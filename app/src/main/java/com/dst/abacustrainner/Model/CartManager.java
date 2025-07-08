package com.dst.abacustrainner.Model;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class CartManager {
    private static CartManager instance;
    private final LinkedHashSet<String> selectedLevels = new LinkedHashSet<>(); // maintains insertion order

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addLevel(String level) {
        if (!selectedLevels.contains(level)) { // ✅ prevent duplicate, preserve original order
            selectedLevels.add(level);
        }
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

    public LinkedHashSet<String> getSelectedLevels() {
        return selectedLevels;
    }

    public void clear() {
        selectedLevels.clear();
    }
}
