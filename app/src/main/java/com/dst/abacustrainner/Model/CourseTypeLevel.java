package com.dst.abacustrainner.Model;

import java.util.List;

public class CourseTypeLevel {
    private String courseLevelId;

    private String courseLevel;
    private String courseType;

    private List<CourseLevelTopic> courseLevelTopics;
   private List<CourseLevelAssignmentTopic> courseLevelAssignmentTopics;

    public void setCourseLevelId(String courseLevelId) {
        this.courseLevelId = courseLevelId;
    }

    public void setCourseLevel(String courseLevel) {
        this.courseLevel = courseLevel;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public List<CourseLevelTopic> getCourseLevelTopics() {
        return courseLevelTopics;
    }

    public void setCourseLevelTopics(List<CourseLevelTopic> courseLevelTopics) {
        this.courseLevelTopics = courseLevelTopics;
    }

    public List<CourseLevelAssignmentTopic> getCourseLevelAssignmentTopics() {
        return courseLevelAssignmentTopics;
    }

    public void setCourseLevelAssignmentTopics(List<CourseLevelAssignmentTopic> courseLevelAssignmentTopics) {
        this.courseLevelAssignmentTopics = courseLevelAssignmentTopics;
    }

    // getters
    public String getCourseLevelId() {
        return courseLevelId;
    }

    public String getCourseLevel() {
        return courseLevel;

    }
}
