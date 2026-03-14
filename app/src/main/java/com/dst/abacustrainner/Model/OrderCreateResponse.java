package com.dst.abacustrainner.Model;

import java.util.List;

public class OrderCreateResponse {
    private String status;
    private String errorCode;
    private Result result;
    private String orderId;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Result {

        private String orderId;
        private String studentId;
        private String price;
        private String state;
        private String currency;
        private String orderedOn;
        private String MerchantRefNo;
        private String PaymentID;
        private String PaymentMethod;
        private String TransactionID;
        private String Amount;
        private String paymentThrough;
        private String DateCreated;
        private String status;

        private List<CourseType> courseTypes;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getOrderedOn() {
            return orderedOn;
        }

        public void setOrderedOn(String orderedOn) {
            this.orderedOn = orderedOn;
        }

        public String getMerchantRefNo() {
            return MerchantRefNo;
        }

        public void setMerchantRefNo(String merchantRefNo) {
            MerchantRefNo = merchantRefNo;
        }

        public String getPaymentID() {
            return PaymentID;
        }

        public void setPaymentID(String paymentID) {
            PaymentID = paymentID;
        }

        public String getPaymentMethod() {
            return PaymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            PaymentMethod = paymentMethod;
        }

        public String getTransactionID() {
            return TransactionID;
        }

        public void setTransactionID(String transactionID) {
            TransactionID = transactionID;
        }

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            Amount = amount;
        }

        public String getPaymentThrough() {
            return paymentThrough;
        }

        public void setPaymentThrough(String paymentThrough) {
            this.paymentThrough = paymentThrough;
        }

        public String getDateCreated() {
            return DateCreated;
        }

        public void setDateCreated(String dateCreated) {
            DateCreated = dateCreated;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<CourseType> getCourseTypes() {
            return courseTypes;
        }

        public void setCourseTypes(List<CourseType> courseTypes) {
            this.courseTypes = courseTypes;
        }
    }
    public class CourseType {

        private String subscribedOn;
        private String noDays;
        private String courseTypeId;
        private String courseType;

        public String getSubscribedOn() {
            return subscribedOn;
        }

        public void setSubscribedOn(String subscribedOn) {
            this.subscribedOn = subscribedOn;
        }

        public String getNoDays() {
            return noDays;
        }

        public void setNoDays(String noDays) {
            this.noDays = noDays;
        }

        public String getCourseTypeId() {
            return courseTypeId;
        }

        public void setCourseTypeId(String courseTypeId) {
            this.courseTypeId = courseTypeId;
        }

        public String getCourseType() {
            return courseType;
        }

        public void setCourseType(String courseType) {
            this.courseType = courseType;
        }
    }
}
