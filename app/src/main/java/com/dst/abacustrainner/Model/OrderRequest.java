package com.dst.abacustrainner.Model;

public class OrderRequest {

    private String merchantOrderId;
    private long amount;
    private long expireAfter;
    private MetaInfo metaInfo;
    private PaymentFlow paymentFlow;

    public OrderRequest(String merchantOrderId, long amount, long expireAfter,
                        MetaInfo metaInfo, PaymentFlow paymentFlow) {

        this.merchantOrderId = merchantOrderId;
        this.amount = amount;
        this.expireAfter = expireAfter;
        this.metaInfo = metaInfo;
        this.paymentFlow = paymentFlow;
    }

}
