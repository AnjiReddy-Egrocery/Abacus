package com.dst.abacustrainner.Model;

public class StudentScheduleInfo {

    private String status;
    private String errorCode;
    private Result result;
    private String message;
    private String emptyTopicsessage;
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

    public String getEmptyTopicsessage() {
        return emptyTopicsessage;
    }

    public void setEmptyTopicsessage(String emptyTopicsessage) {
        this.emptyTopicsessage = emptyTopicsessage;
    }

    public String getEmptyAssignmentTopicsessage() {
        return emptyAssignmentTopicsessage;
    }

    public void setEmptyAssignmentTopicsessage(String emptyAssignmentTopicsessage) {
        this.emptyAssignmentTopicsessage = emptyAssignmentTopicsessage;
    }

    public static class Result{

        private String dateId;
        private String batchId;
        private String scheduleDate;
        private Object scheduleTopics;
        private Object scheduleAssignmentTopics;
        private String startDate;
        private String scheduleStartTime;
        private String scheduleEndTime;
        private String batchName;
        private String courseTypeId;
        private String subCourseTypeId;
        private String courseLevelId;
        private Integer topicsCount;
        private Integer assignmentTopicsCount;

        public String getDateId() {
            return dateId;
        }

        public void setDateId(String dateId) {
            this.dateId = dateId;
        }

        public String getBatchId() {
            return batchId;
        }

        public void setBatchId(String batchId) {
            this.batchId = batchId;
        }

        public String getScheduleDate() {
            return scheduleDate;
        }

        public void setScheduleDate(String scheduleDate) {
            this.scheduleDate = scheduleDate;
        }

        public Object getScheduleTopics() {
            return scheduleTopics;
        }

        public void setScheduleTopics(Object scheduleTopics) {
            this.scheduleTopics = scheduleTopics;
        }

        public Object getScheduleAssignmentTopics() {
            return scheduleAssignmentTopics;
        }

        public void setScheduleAssignmentTopics(Object scheduleAssignmentTopics) {
            this.scheduleAssignmentTopics = scheduleAssignmentTopics;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getScheduleStartTime() {
            return scheduleStartTime;
        }

        public void setScheduleStartTime(String scheduleStartTime) {
            this.scheduleStartTime = scheduleStartTime;
        }

        public String getScheduleEndTime() {
            return scheduleEndTime;
        }

        public void setScheduleEndTime(String scheduleEndTime) {
            this.scheduleEndTime = scheduleEndTime;
        }

        public String getBatchName() {
            return batchName;
        }

        public void setBatchName(String batchName) {
            this.batchName = batchName;
        }

        public String getCourseTypeId() {
            return courseTypeId;
        }

        public void setCourseTypeId(String courseTypeId) {
            this.courseTypeId = courseTypeId;
        }

        public String getSubCourseTypeId() {
            return subCourseTypeId;
        }

        public void setSubCourseTypeId(String subCourseTypeId) {
            this.subCourseTypeId = subCourseTypeId;
        }

        public String getCourseLevelId() {
            return courseLevelId;
        }

        public void setCourseLevelId(String courseLevelId) {
            this.courseLevelId = courseLevelId;
        }

        public Integer getTopicsCount() {
            return topicsCount;
        }

        public void setTopicsCount(Integer topicsCount) {
            this.topicsCount = topicsCount;
        }

        public Integer getAssignmentTopicsCount() {
            return assignmentTopicsCount;
        }

        public void setAssignmentTopicsCount(Integer assignmentTopicsCount) {
            this.assignmentTopicsCount = assignmentTopicsCount;
        }

    }
}
