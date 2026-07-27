package com.dst.abacustrainner.Model;

public class QuestionModel {
    private String courseType;
    private String subCourseType;
    private String courseLevel;
    private String topic;
    private String question;
    private String answer;

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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

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
}
