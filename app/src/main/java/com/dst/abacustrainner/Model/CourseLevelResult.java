package com.dst.abacustrainner.Model;

import java.util.List;

public class CourseLevelResult {

    private String courseTypeId;
    private String courseType;
    private String courseCode;
    private List<CourseLevel> courseLevels;

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

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public List<CourseLevel> getCourseLevels() {
        return courseLevels;
    }

    public void setCourseLevels(List<CourseLevel> courseLevels) {
        this.courseLevels = courseLevels;
    }
}
