package com.dst.abacustrainner.Model;

public class LevelPriceResponse {
    private String status;
    private String errorCode;
    private PriceResult result;
    private String message;

    public PriceResult getResult() {
        return result;
    }

}
