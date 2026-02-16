package com.dst.abacustrainner.Model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class TokenUtils {
    public static String generateToken(String clientId, String clientSecret, String orderPayload) {
        try {
            String data = clientId + "|" + orderPayload + "|" + clientSecret;
            MessageDigest md = MessageDigest.getInstance("SHA‑256");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
