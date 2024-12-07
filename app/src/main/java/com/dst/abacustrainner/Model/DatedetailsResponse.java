package com.dst.abacustrainner.Model;

import java.util.List;

public class DatedetailsResponse {
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
    public class Result {

        private String batchId;
        private String courseType;
        private String subCourseType;
        private String courseLevel;
        private String batchName;
        private String instructorName;
        private String startDate;
        private String startTime;
        private String endTime;
        private List<Date> dates;

        public String getBatchId() {
            return batchId;
        }

        public void setBatchId(String batchId) {
            this.batchId = batchId;
        }

        public String getCourseType() {
            return courseType;
        }

        public void setCourseType(String courseType) {
            this.courseType = courseType;
        }

        public String getSubCourseType() {
            return subCourseType;
        }

        public void setSubCourseType(String subCourseType) {
            this.subCourseType = subCourseType;
        }

        public String getCourseLevel() {
            return courseLevel;
        }

        public void setCourseLevel(String courseLevel) {
            this.courseLevel = courseLevel;
        }

        public String getBatchName() {
            return batchName;
        }

        public void setBatchName(String batchName) {
            this.batchName = batchName;
        }

        public String getInstructorName() {
            return instructorName;
        }

        public void setInstructorName(String instructorName) {
            this.instructorName = instructorName;
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

        public List<Date> getDates() {
            return dates;
        }

        public void setDates(List<Date> dates) {
            this.dates = dates;
        }

        public class Date {

            private String dateId;
            private String batchId;
            private String scheduleDate;
            private Object scheduleTopics;
            private Object scheduleAssignmentTopics;
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
}
