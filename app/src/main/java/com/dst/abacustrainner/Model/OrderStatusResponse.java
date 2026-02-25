package com.dst.abacustrainner.Model;

import java.util.List;

public class OrderStatusResponse {

    private String orderId;
    private String state;
    private long amount;
    private long payableAmount;
    private long feeAmount;
    private long expireAt;
    private MetaInfo metaInfo;
    private List<PaymentDetail> paymentDetails;
    private ErrorContext errorContext;

    public String getOrderId() { return orderId; }
    public String getState() { return state; }
    public long getAmount() { return amount; }
    public long getPayableAmount() { return payableAmount; }
    public long getFeeAmount() { return feeAmount; }
    public long getExpireAt() { return expireAt; }
    public MetaInfo getMetaInfo() { return metaInfo; }
    public List<PaymentDetail> getPaymentDetails() { return paymentDetails; }
    public ErrorContext getErrorContext() { return errorContext; }

    // ----- Nested Classes -----

    public static class MetaInfo {
        private String udf1;
        private String udf2;
        // Add udf3 ... udf15 if needed

        public String getUdf1() { return udf1; }
        public String getUdf2() { return udf2; }
    }

    public static class PaymentDetail {
        private String transactionId;
        private String paymentMode;
        private long timestamp;
        private long amount;
        private long payableAmount;
        private long feeAmount;
        private String state;
        private Rail rail;
        private Instrument instrument;
        private List<SplitInstrument> splitInstruments;

        public String getTransactionId() { return transactionId; }
        public String getPaymentMode() { return paymentMode; }
        public long getTimestamp() { return timestamp; }
        public long getAmount() { return amount; }
        public long getPayableAmount() { return payableAmount; }
        public long getFeeAmount() { return feeAmount; }
        public String getState() { return state; }
        public Rail getRail() { return rail; }
        public Instrument getInstrument() { return instrument; }
        public List<SplitInstrument> getSplitInstruments() { return splitInstruments; }
    }

    public static class Rail {
        private String type;
        private String utr;
        private String upiTransactionId;
        private String vpa;

        public String getType() { return type; }
        public String getUtr() { return utr; }
        public String getUpiTransactionId() { return upiTransactionId; }
        public String getVpa() { return vpa; }
    }

    public static class Instrument {
        private String type;
        private String maskedAccountNumber;
        private String accountType;
        private String accountHolderName;

        public String getType() { return type; }
        public String getMaskedAccountNumber() { return maskedAccountNumber; }
        public String getAccountType() { return accountType; }
        public String getAccountHolderName() { return accountHolderName; }
    }

    public static class SplitInstrument {
        private long amount;
        private Rail rail;
        private Instrument instrument;

        public long getAmount() { return amount; }
        public Rail getRail() { return rail; }
        public Instrument getInstrument() { return instrument; }
    }

    public static class ErrorContext {
        private String errorCode;
        private String detailedErrorCode;
        private String source;
        private String stage;
        private String description;

        public String getErrorCode() { return errorCode; }
        public String getDetailedErrorCode() { return detailedErrorCode; }
        public String getSource() { return source; }
        public String getStage() { return stage; }
        public String getDescription() { return description; }
    }
}
