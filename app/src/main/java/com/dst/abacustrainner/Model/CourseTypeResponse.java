package com.dst.abacustrainner.Model;

import java.util.List;

public class CourseTypeResponse {
    private String status;
    private String errorCode;
   private List<CourseType> result;
   private  String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private String message;

    // Getters & Setters
    public String getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public List<CourseType> getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

}
