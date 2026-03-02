package com.dst.abacustrainner.Model;

import java.util.List;

public class CourseType {
    private String courseTypeId;

    private String courseType;
    private String courseCode;

    private List<CourseTypeLevel> courseLevels;

    public List<CourseTypeLevel> getCourseLevels() {
        return courseLevels;
    }

    // Getters
    public String getCourseTypeId() {
        return courseTypeId;
    }

    public String getCourseType() {
        return courseType;
    }

    public String getCourseCode() {
        return courseCode;
    }


}
