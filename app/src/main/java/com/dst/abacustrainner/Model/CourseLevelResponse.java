package com.dst.abacustrainner.Model;

public class CourseLevelResponse {

    private String status;
    private String errorCode;
    private CourseLevelResult result;
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

    public CourseLevelResult getResult() {
        return result;
    }

    public void setResult(CourseLevelResult result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
