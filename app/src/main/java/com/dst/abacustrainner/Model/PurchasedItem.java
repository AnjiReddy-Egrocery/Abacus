package com.dst.abacustrainner.Model;

import java.util.List;

public class PurchasedItem {
    String courseName;
    List<String> levels;

    public PurchasedItem(String courseName, List<String> levels) {
        this.courseName = courseName;
        this.levels = levels;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<String> getLevels() {
        return levels;
    }
}
