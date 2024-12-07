package com.dst.abacustrainner.Model;

import java.util.List;

public class StudentDetails {
    private String status;
    private String errorCode;
    private List<Result> result;
    private String message;

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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Result{
        private String dateId;
        private String batchId;
        private String scheduleDate;
        private String scheduleTopics;
        private String scheduleAssignmentTopics;
        private String startDate;
        private String startTime;
        private String endTime;
        private String batchName;
        private String studentId;
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

        public String getScheduleTopics() {
            return scheduleTopics;
        }

        public void setScheduleTopics(String scheduleTopics) {
            this.scheduleTopics = scheduleTopics;
        }

        public String getScheduleAssignmentTopics() {
            return scheduleAssignmentTopics;
        }

        public void setScheduleAssignmentTopics(String scheduleAssignmentTopics) {
            this.scheduleAssignmentTopics = scheduleAssignmentTopics;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getBatchName() {
            return batchName;
        }

        public void setBatchName(String batchName) {
            this.batchName = batchName;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
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
