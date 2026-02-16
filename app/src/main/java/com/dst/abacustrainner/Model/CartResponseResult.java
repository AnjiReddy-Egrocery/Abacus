package com.dst.abacustrainner.Model;

import java.util.List;

public class CartResponseResult {

    private String courseTypeId;
    private String courseType;
    private List<CourseLevelCart> courseLevels;

    public String getCourseTypeId() {
        return courseTypeId;
    }

    public void setCourseTypeId(String courseTypeId) {
        this.courseTypeId = courseTypeId;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public List<CourseLevelCart> getCourseLevels() {
        return courseLevels;
    }

    public void setCourseLevels(List<CourseLevelCart> courseLevels) {
        this.courseLevels = courseLevels;
    }
}
