package com.dst.abacustrainner.Model;

import java.util.List;

public class SubscribedLevelsResponse {
    private String status;

    private String errorCode;

    private List<String> result;

    private String message;

    // ✅ Getters

    public String getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public List<String> getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

}
