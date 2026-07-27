package com.dst.abacustrainner.Model;

import java.util.List;

public class PaperListResponse {
    private String status;
    private String errorCode;
    private List<Result> result;
    private String message;
    private String emptyTopicsessage;

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

    public String getEmptyTopicsessage() {
        return emptyTopicsessage;
    }

    public void setEmptyTopicsessage(String emptyTopicsessage) {
        this.emptyTopicsessage = emptyTopicsessage;
    }

    public static class Result{
        private String paperId;
        private String instructorId;
        private String paperTitle;
        private List<QuestionModel> questions;
        private List<PracticeModel> practicesList;

        public String getPaperId() {
            return paperId;
        }

        public void setPaperId(String paperId) {
            this.paperId = paperId;
        }

        public String getInstructorId() {
            return instructorId;
        }

        public void setInstructorId(String instructorId) {
            this.instructorId = instructorId;
        }

        public String getPaperTitle() {
            return paperTitle;
        }

        public void setPaperTitle(String paperTitle) {
            this.paperTitle = paperTitle;
        }

        public List<QuestionModel> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionModel> questions) {
            this.questions = questions;
        }

        public List<PracticeModel> getPracticesList() {
            return practicesList;
        }

        public void setPracticesList(List<PracticeModel> practicesList) {
            this.practicesList = practicesList;
        }


    }
}
