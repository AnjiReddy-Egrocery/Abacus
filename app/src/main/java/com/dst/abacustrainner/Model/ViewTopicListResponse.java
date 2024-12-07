package com.dst.abacustrainner.Model;

import java.util.List;

public class ViewTopicListResponse {
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

    public static class Result{
        private String topicId;
        private String courseTypeId;
        private String subCourseTypeId;
        private String courseLevelId;
        private String topicName;
        private String formulaName;
        private List<Practices> practicesList;

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
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

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public String getFormulaName() {
            return formulaName;
        }

        public void setFormulaName(String formulaName) {
            this.formulaName = formulaName;
        }

        public List<Practices> getPracticesList() {
            return practicesList;
        }

        public void setPracticesList(List<Practices> practicesList) {
            this.practicesList = practicesList;
        }

        public static class Practices{
            private String practiceId;
            private String examRnm;
            private String studentId;
            private String instructorId;
            private String topicId;
            private Object questionsList;
            private String startedOn;
            private String submitedOn;
            private String practiceStatus;
            private String topicName;

            public String getPracticeId() {
                return practiceId;
            }

            public void setPracticeId(String practiceId) {
                this.practiceId = practiceId;
            }

            public String getExamRnm() {
                return examRnm;
            }

            public void setExamRnm(String examRnm) {
                this.examRnm = examRnm;
            }

            public String getStudentId() {
                return studentId;
            }

            public void setStudentId(String studentId) {
                this.studentId = studentId;
            }

            public String getInstructorId() {
                return instructorId;
            }

            public void setInstructorId(String instructorId) {
                this.instructorId = instructorId;
            }

            public String getTopicId() {
                return topicId;
            }

            public void setTopicId(String topicId) {
                this.topicId = topicId;
            }

            public Object getQuestionsList() {
                return questionsList;
            }

            public void setQuestionsList(Object questionsList) {
                this.questionsList = questionsList;
            }

            public String getStartedOn() {
                return startedOn;
            }

            public void setStartedOn(String startedOn) {
                this.startedOn = startedOn;
            }

            public String getSubmitedOn() {
                return submitedOn;
            }

            public void setSubmitedOn(String submitedOn) {
                this.submitedOn = submitedOn;
            }

            public String getPracticeStatus() {
                return practiceStatus;
            }

            public void setPracticeStatus(String practiceStatus) {
                this.practiceStatus = practiceStatus;
            }

            public String getTopicName() {
                return topicName;
            }

            public void setTopicName(String topicName) {
                this.topicName = topicName;
            }
        }

    }
}
