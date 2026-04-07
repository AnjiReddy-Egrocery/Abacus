package com.dst.abacustrainner.Model;

import com.google.gson.annotations.SerializedName;

public class OrderRequest {

    @SerializedName("merchantOrderId")
    private String merchantOrderId;

    @SerializedName("amount")
    private long amount;

    @SerializedName("expireAfter")
    private long expireAfter;

    @SerializedName("metaInfo")
    private MetaInfo metaInfo;

    @SerializedName("paymentFlow")
    private PaymentFlow paymentFlow;

    public OrderRequest(String merchantOrderId, long amount, long expireAfter,
                        MetaInfo metaInfo, PaymentFlow paymentFlow) {

        this.merchantOrderId = merchantOrderId;
        this.amount = amount;
        this.expireAfter = expireAfter; // 👈 SET
        this.metaInfo = metaInfo;
        this.paymentFlow = paymentFlow;
    }

}
