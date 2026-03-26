package com.dst.abacustrainner.Model;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("state")
    private String state;

    @SerializedName("expireAt")
    private long expireAt;

    @SerializedName("token")
    private String token;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
