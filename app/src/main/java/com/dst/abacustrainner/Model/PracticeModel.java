package com.dst.abacustrainner.Model;

import java.util.List;

public class PracticeModel {
        private String practiceId;
        private String examRnm;
        private List<PracticeQuestionModel> questions;
        private String startedOn;
        private String submitedOn;

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

    public List<PracticeQuestionModel> getQuestions() {
        return questions;
    }

    public void setQuestions(List<PracticeQuestionModel> questions) {
        this.questions = questions;
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

    public static class PracticeQuestionModel{
        private String question;
        private String answer;
        private String given;
        private int isCurrect;
        private String timeTaken;
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

        public int getIsCurrect() {
            return isCurrect;
        }

        public void setIsCurrect(int isCurrect) {
            this.isCurrect = isCurrect;
        }

        public String getTimeTaken() {
            return timeTaken;
        }

        public void setTimeTaken(String timeTaken) {
            this.timeTaken = timeTaken;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
