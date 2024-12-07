package com.dst.abacustrainner.Model;

public class SendData {
    String question;
    String correctAnswer;
    String enterAnswer;
    String isCorrect;
    long timeTaken;
    String status;

    public SendData(String question, String correctAnswer, String enterAnswer, String isCorrect, String status,long timeTaken) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.enterAnswer = enterAnswer;
        this.isCorrect = isCorrect;
        this.timeTaken = timeTaken;
        this.status = status;
    }

    public SendData(String question, String enteredAnswer, String enterAnswer, int isCorrected, int status, long timeTaken) {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getEnterAnswer() {
        return enterAnswer;
    }

    public void setEnterAnswer(String enterAnswer) {
        this.enterAnswer = enterAnswer;
    }

    public String getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(String isCorrect) {
        this.isCorrect = isCorrect;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
