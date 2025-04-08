package com.dst.abacustrainner.Model;

public class Question {
    private String question;
    private String answer;
    private String given;
    private int is_currect;
    private String time_taken;
    private int status;

    // Getters
    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getGiven() {
        return given;
    }

    public int getIs_currect() {
        return is_currect;
    }

    public String getTime_taken() {
        return time_taken;
    }

    public int getStatus() {
        return status;
    }
}
