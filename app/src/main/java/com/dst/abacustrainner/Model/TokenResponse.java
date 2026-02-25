package com.dst.abacustrainner.Model;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {

    @SerializedName("access_token")
    private String accessToken;


    @SerializedName("encrypted_access_token")
    private String encryptedAccessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEncryptedAccessToken() {
        return encryptedAccessToken;
    }

    public void setEncryptedAccessToken(String encryptedAccessToken) {
        this.encryptedAccessToken = encryptedAccessToken;
    }



}
