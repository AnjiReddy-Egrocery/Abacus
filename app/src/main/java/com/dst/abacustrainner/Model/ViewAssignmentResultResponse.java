package com.dst.abacustrainner.Model;

public class ViewAssignmentResultResponse {

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
        private String practiceId;
        private String examRnm;
        private String studentId;
        private String instructorId;
        private String topicId;
        private String questionsList;
        private String startedOn;
        private String submitedOn;
        private String practiceStatus;
        private String topicName;
        private String firstName;
        private String middleName;
        private String lastName;

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

        public String getQuestionsList() {
            return questionsList;
        }

        public void setQuestionsList(String questionsList) {
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

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

    }
}
