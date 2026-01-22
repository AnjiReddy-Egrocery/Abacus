package com.dst.abacustrainner.Model;

public class CourseLevel {
    private String courseLevelId;
    private String courseLevel;

    private String price;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCourseLevelId() {
        return courseLevelId;
    }

    public void setCourseLevelId(String courseLevelId) {
        this.courseLevelId = courseLevelId;
    }

    public String getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(String courseLevel) {
        this.courseLevel = courseLevel;
    }
}
