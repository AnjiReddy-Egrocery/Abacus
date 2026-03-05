package com.dst.abacustrainner.Model;

import java.util.List;

public class OrderInfoResponse {
    private String status;
    private Result result;

    public Result getResult() {
        return result;
    }
    public class Result {

        private List<WorksheetOrderInfo> worksheetOrderInfo;

        public List<WorksheetOrderInfo> getWorksheetOrderInfo() {
            return worksheetOrderInfo;
        }
    }
    public class WorksheetOrderInfo {

        private String orderedOn;
        private String state;
        private String paymentThrough;
        private String PaymentMethod;
        private String currency;
        private String Amount;

        private List<Subscription> subscriptions;

        public String getOrderedOn() { return orderedOn; }
        public String getState() { return state; }
        public String getPaymentThrough() { return paymentThrough; }
        public String getPaymentMethod() { return PaymentMethod; }
        public String getCurrency() { return currency; }
        public String getAmount() { return Amount; }

        public List<Subscription> getSubscriptions() {
            return subscriptions;
        }
    }

    public class Subscription {

        private String courseType;
        private String courseLevel;
        private String amount;

        public String getCourseType() { return courseType; }
        public String getCourseLevel() { return courseLevel; }
        public String getAmount() { return amount; }
    }
}
