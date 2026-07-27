package com.dst.abacustrainner.Model;

import java.util.List;

public class PaperSubmitDataResponse {
    private String status;
    private String errorCode;
    private PaperExamResponse.Result result;
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

    public PaperExamResponse.Result getResult() {
        return result;
    }

    public void setResult(PaperExamResponse.Result result) {
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
        private String paperId;
        private List<QuestionItem> questionsList;

        private String startedOn;
        private String submitedOn;
        private String practiceStatus;
        private String paperName;

        private String fullName;



        public String getPracticeId() {
            return practiceId;
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

        public String getPaperName() {
            return paperName;
        }

        public void setPaperName(String paperName) {
            this.paperName = paperName;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
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

        public String getPaperId() {
            return paperId;
        }

        public void setPaperId(String paperId) {
            this.paperId = paperId;
        }

        public List<QuestionItem> getQuestionsList() {
            return questionsList;
        }

        public void setQuestionsList(List<QuestionItem> questionsList) {
            this.questionsList = questionsList;
        }
    }
    public class QuestionItem {
        private String question;
        private String answer;
        private String given;
        private int is_currect;
        private int time_taken;
        private int status;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getGiven() {
            return given;
        }

        public void setGiven(String given) {
            this.given = given;
        }

        public int getIs_currect() {
            return is_currect;
        }

        public void setIs_currect(int is_currect) {
            this.is_currect = is_currect;
        }

        public int getTime_taken() {
            return time_taken;
        }

        public void setTime_taken(int time_taken) {
            this.time_taken = time_taken;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

}
