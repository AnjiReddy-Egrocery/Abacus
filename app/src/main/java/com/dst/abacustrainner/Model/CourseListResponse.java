package com.dst.abacustrainner.Model;

import java.util.List;

public class CourseListResponse {
    private String status;
    private String errorCode;
    private Result result;
    private String message;
    private String emptyAssignmentTopicsessage;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmptyAssignmentTopicsessage() {
        return emptyAssignmentTopicsessage;
    }

    public void setEmptyAssignmentTopicsessage(String emptyAssignmentTopicsessage) {
        this.emptyAssignmentTopicsessage = emptyAssignmentTopicsessage;
    }
    public static class Result {
        private String courseTypeId;

        private String courseType;
        private List<CourseLevels> courseLevels;

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

        public List<CourseLevels> getCourseLevels() {
            return courseLevels;
        }

        public void setCourseLevels(List<CourseLevels> courseLevels) {
            this.courseLevels = courseLevels;
        }
    }

    public static class CourseLevels{
        private String courseLevelId;

        private String courseLevel;

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
}
