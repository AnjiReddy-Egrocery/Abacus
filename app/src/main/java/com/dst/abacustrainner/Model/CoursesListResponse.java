package com.dst.abacustrainner.Model;

import java.util.List;

public class CoursesListResponse {
    private String status;
    private String errorCode;
    private List<CourseResult> result;
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

    public List<CourseResult> getResult() {
        return result;
    }

    public void setResult(List<CourseResult> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
