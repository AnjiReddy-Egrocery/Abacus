package com.dst.abacustrainner.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;

    private LinkedHashMap<String, List<String>> courseLevelMap = new LinkedHashMap<>();

    private final String PREF_NAME = "cart_prefs";
    private final String CART_KEY = "cart_data";
    private SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    private CartManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadCartFromStorage();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public void addLevel(String courseName, String level) {
        if (!courseLevelMap.containsKey(courseName)) {
            courseLevelMap.put(courseName, new ArrayList<>());
        }
        List<String> levels = courseLevelMap.get(courseName);
        if (!levels.contains(level)) {
            levels.add(level);
            saveCartToStorage();
        }
    }

    public void removeLevel(String course, String level) {
        if (courseLevelMap.containsKey(course)) {
            courseLevelMap.get(course).remove(level);
            if (courseLevelMap.get(course).isEmpty()) {
                courseLevelMap.remove(course);
            }
            saveCartToStorage();
        }
    }
    public boolean isSelected(String level) {
        for (List<String> levels : courseLevelMap.values()) {
            if (levels.contains(level)) return true;
        }
        return false;
    }

    public int getCount() {
        int count = 0;
        for (List<String> levels : courseLevelMap.values()) {
            count += levels.size();
        }
        return count;
    }

    public Map<String, List<String>> getSelectedLevelsByCourse() {
        return courseLevelMap;
    }

    public List<String> getAllSelectedLevels() {
        List<String> all = new ArrayList<>();
        for (List<String> levels : courseLevelMap.values()) {
            all.addAll(levels);
        }
        return all;
    }

    public void clear() {
        courseLevelMap.clear();
        saveCartToStorage();
    }

    private void saveCartToStorage() {
        String json = gson.toJson(courseLevelMap);
        sharedPreferences.edit().putString(CART_KEY, json).apply();
    }

    private void loadCartFromStorage() {
        String json = sharedPreferences.getString(CART_KEY, null);
        if (json != null) {
            Type type = new TypeToken<LinkedHashMap<String, List<String>>>() {}.getType();
            courseLevelMap = gson.fromJson(json, type);
        } else {
            courseLevelMap = new LinkedHashMap<>();
        }
    }

    public String getSelectedCourse() {
        if (courseLevelMap.size() == 1) {
            return courseLevelMap.keySet().iterator().next(); // only if one course
        }
        return null; // or handle multiple
    }

    public ArrayList<String> getSelectedLevels() {
        ArrayList<String> all = new ArrayList<>();
        for (List<String> levels : courseLevelMap.values()) {
            all.addAll(levels);
        }
        return all;
    }
}
