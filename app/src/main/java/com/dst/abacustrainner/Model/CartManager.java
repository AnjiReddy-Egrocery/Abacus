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
import java.util.Random;
import java.util.Set;

public class CartManager {
    private static final String PREF_NAME = "cart_pref";
    private static final String KEY_WORKSHEET_RNM = "worksheet_rnm";
    private static final String KEY_DURATION = "selected_duration_";
    private static final String KEY_LEVELS = "selected_levels_";

    private static CartManager instance;
    private SharedPreferences prefs;

    private CartManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context.getApplicationContext());
        }
        return instance;
    }

    // ===============================
    // 🔥 WORKSHEET RNM
    // ===============================

    public String getWorksheetRnm() {
        String rnm = prefs.getString(KEY_WORKSHEET_RNM, null);

        if (rnm == null) {
            rnm = generateSixDigitRnm();
            prefs.edit().putString(KEY_WORKSHEET_RNM, rnm).apply();
        }

        return rnm;
    }

    public void clearCart() {
        prefs.edit().clear().apply();   // 🔥 clear everything
    }

    private String generateSixDigitRnm() {
        int num = 100000 + new Random().nextInt(900000);
        return String.valueOf(num);
    }

    // ===============================
    // 🔥 SAVE DURATION (per worksheet + course)
    // ===============================

    public void saveSelectedDuration(String worksheetRnm, String courseId, String durationId) {
        prefs.edit()
                .putString(KEY_DURATION + worksheetRnm + "_" + courseId, durationId)
                .apply();
    }

    public String getSelectedDuration(String worksheetRnm, String courseId) {
        return prefs.getString(KEY_DURATION + worksheetRnm + "_" + courseId, null);
    }

    // ===============================
    // 🔥 SAVE LEVELS (store ids as comma string)
    // ===============================

    public void saveSelectedLevels(String worksheetRnm, String courseId, List<CourseLevel> levels) {

        StringBuilder builder = new StringBuilder();

        for (CourseLevel level : levels) {
            builder.append(level.getCourseLevelId()).append(",");
        }

        prefs.edit()
                .putString(KEY_LEVELS + worksheetRnm + "_" + courseId,
                        builder.toString())
                .apply();
    }

    public List<String> getSelectedLevelIds(String worksheetRnm, String courseId) {

        String saved = prefs.getString(
                KEY_LEVELS + worksheetRnm + "_" + courseId,
                null
        );

        List<String> ids = new ArrayList<>();

        if (saved != null && !saved.isEmpty()) {

            String[] split = saved.split(",");

            for (String id : split) {
                if (!id.isEmpty()) {
                    ids.add(id);
                }
            }
        }

        return ids;
    }
}