package com.dst.abacustrainner.Model;

public class WorksheetOrder {
    private String orderId;
    private String orderedOn;
    private String Amount;
    private String state;

    public String getOrderedOn() {
        return orderedOn;
    }

    public String getAmount() {
        return Amount;
    }

    public String getState() {
        return state;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setOrderedOn(String orderedOn) {
        this.orderedOn = orderedOn;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public void setState(String state) {
        this.state = state;
    }
}
