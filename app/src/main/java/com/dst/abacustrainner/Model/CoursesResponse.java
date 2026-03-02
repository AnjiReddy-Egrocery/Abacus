package com.dst.abacustrainner.Model;

import java.util.List;

public class CoursesResponse {
   private String status;
    private String errorCode;

    private List<CourseTypeLevel> result;
    private String message;


    public List<CourseTypeLevel> getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
