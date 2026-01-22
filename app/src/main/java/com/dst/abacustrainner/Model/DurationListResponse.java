package com.dst.abacustrainner.Model;

import java.util.List;

public class DurationListResponse {

    private String status;
    private String errorCode;
    private List<DurationResult> result;

    public List<DurationResult> getResult() {
        return result;
    }

    public void setResult(List<DurationResult> result) {
        this.result = result;
    }

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


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
