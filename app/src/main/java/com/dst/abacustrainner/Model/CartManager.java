package com.dst.abacustrainner.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CartManager {
    private static CartManager instance;

    private LinkedHashMap<String, LinkedHashMap<String, List<String>>> typeCourseLevelMap = new LinkedHashMap<>();

    private final String PREF_NAME = "cart_prefs";
    private final String CART_KEY = "cart_data";
    private SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();
    private Context context;


    private CartManager(Context context) {
        this.context = context.getApplicationContext(); // avoid memory leak
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        loadCartFromStorage();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public void addLevel(String type, String courseName, String level) {
        if (!typeCourseLevelMap.containsKey(type)) {
            typeCourseLevelMap.put(type, new LinkedHashMap<>());
        }

        LinkedHashMap<String, List<String>> courseLevelMap = typeCourseLevelMap.get(type);

        if (!courseLevelMap.containsKey(courseName)) {
            courseLevelMap.put(courseName, new ArrayList<>());
        }

        List<String> levels = courseLevelMap.get(courseName);
        if (!levels.contains(level)) {
            levels.add(level);
            saveCartToStorage();
        }
    }

    public void removeLevel(String type, String courseName, String level) {
        if (typeCourseLevelMap.containsKey(type)) {
            LinkedHashMap<String, List<String>> courseLevelMap = typeCourseLevelMap.get(type);

            if (courseLevelMap.containsKey(courseName)) {
                courseLevelMap.get(courseName).remove(level);
                if (courseLevelMap.get(courseName).isEmpty()) {
                    courseLevelMap.remove(courseName);
                }
            }

            if (courseLevelMap.isEmpty()) {
                typeCourseLevelMap.remove(type);
            }

            saveCartToStorage();
        }
    }

    public boolean isSelected(String type, String level) {
        if (typeCourseLevelMap.containsKey(type)) {
            for (List<String> levels : typeCourseLevelMap.get(type).values()) {
                if (levels.contains(level)) return true;
            }
        }
        return false;
    }

    public int getCount(String type) {
        if (!typeCourseLevelMap.containsKey(type)) return 0;

        int count = 0;
        for (List<String> levels : typeCourseLevelMap.get(type).values()) {
            count += levels.size();
        }
        return count;
    }

    public List<String> getAllSelectedLevels(String type) {
        List<String> all = new ArrayList<>();
        if (typeCourseLevelMap.containsKey(type)) {
            for (List<String> levels : typeCourseLevelMap.get(type).values()) {
                all.addAll(levels);
            }
        }
        return all;
    }

    public Map<String, List<String>> getSelectedLevelsByCourse(String type) {
        return typeCourseLevelMap.getOrDefault(type, new LinkedHashMap<>());
    }

    public boolean isEmpty(String type) {
        return getCount(type) == 0;
    }

    public void clear(String type) {
        if (typeCourseLevelMap.containsKey(type)) {
            typeCourseLevelMap.remove(type);
            saveCartToStorage();
        }
    }

    private void saveCartToStorage() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(typeCourseLevelMap);
        editor.putString(CART_KEY, json);
        editor.apply();
    }

    private void loadCartFromStorage() {
        if (context == null) {
            throw new IllegalStateException("Context is null in CartManager");
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CART_KEY, null);
        if (json != null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, List<String>>>>() {}.getType();
                typeCourseLevelMap = gson.fromJson(json, type);
            } catch (Exception e) {
                e.printStackTrace();
                typeCourseLevelMap = new LinkedHashMap<>();
            }
        }
    }


    // Optional utility method if needed
    public String getSelectedCourse(String type) {
        if (typeCourseLevelMap.containsKey(type)) {
            Map<String, List<String>> courseMap = typeCourseLevelMap.get(type);
            if (courseMap.size() == 1) {
                return courseMap.keySet().iterator().next();
            }
        }
        return null;
    }

    public int getTotalCartCount() {
        int total = 0;
        for (String type : typeCourseLevelMap.keySet()) {
            total += getCount(type);
        }
        return total;
    }
    public Set<String> getAllTypes() {
        return typeCourseLevelMap.keySet();
    }

    public void clearAll() {
        typeCourseLevelMap.clear();
        saveCartToStorage();
    }
}