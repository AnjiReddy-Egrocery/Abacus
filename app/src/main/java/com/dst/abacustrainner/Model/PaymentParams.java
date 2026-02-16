package com.dst.abacustrainner.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentParams {
    public static JSONObject getOrderData() {
        JSONObject order = new JSONObject();
        try {
            order.put("orderId", "ORDER_" + System.currentTimeMillis());
            order.put("amount", "100");  // Amount in paise (₹1.00)
            order.put("merchantId", "M23EB6GY8RWOK_2601021928");

            JSONObject customer = new JSONObject();
            customer.put("customerId", "CUST123");
            customer.put("email", "test@example.com");
            customer.put("mobile", "9000000000");
            order.put("customer", customer);

            JSONObject txnMeta = new JSONObject();
            txnMeta.put("checkoutMode", "SINGLE");
            order.put("txnMeta", txnMeta);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return order;
    }
}
