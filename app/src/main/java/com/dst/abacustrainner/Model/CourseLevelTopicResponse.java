package com.dst.abacustrainner.Model;

import java.util.List;

public class CourseLevelTopicResponse {
    private String status;
    private String errorCode;
    private Result result;
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
    public static class Result {
        private String courseLevelId;

        private String courseLevel;
        private List<courseLevelTopics> courseLevelTopics;

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

        public List<courseLevelTopics> getCourseLevelTopics() {
            return courseLevelTopics;
        }

        public void setCourseLevelTopics(List<courseLevelTopics> courseLevelTopics) {
            this.courseLevelTopics = courseLevelTopics;
        }
    }
    public static class courseLevelTopics{
        private String topicId;

        private String topic;

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
    }
}
