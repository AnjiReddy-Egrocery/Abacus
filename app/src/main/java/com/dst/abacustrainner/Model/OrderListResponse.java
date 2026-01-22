package com.dst.abacustrainner.Model;

import java.util.List;

public class OrderListResponse {
    private String status;
    private String errorCode;
    private Result result;
    private String message;
    private String emptyAssignmentTopicsessage;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmptyAssignmentTopicsessage() {
        return emptyAssignmentTopicsessage;
    }

    public void setEmptyAssignmentTopicsessage(String emptyAssignmentTopicsessage) {
        this.emptyAssignmentTopicsessage = emptyAssignmentTopicsessage;
    }

    public static class Result {

        private String orderId;
        private String studentId;
        private String price;
        private String orderedOn;
        private String merchantRefNo;
        private String paymentID;
        private String paymentMethod;
        private String transactionID;
        private String amount;
        private String paymentThrough;
        private String dateCreated;
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

        public String getOrderedOn() {
            return orderedOn;
        }

        public void setOrderedOn(String orderedOn) {
            this.orderedOn = orderedOn;
        }

        public String getMerchantRefNo() {
            return merchantRefNo;
        }

        public void setMerchantRefNo(String merchantRefNo) {
            this.merchantRefNo = merchantRefNo;
        }

        public String getPaymentID() {
            return paymentID;
        }

        public void setPaymentID(String paymentID) {
            this.paymentID = paymentID;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getTransactionID() {
            return transactionID;
        }

        public void setTransactionID(String transactionID) {
            this.transactionID = transactionID;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getPaymentThrough() {
            return paymentThrough;
        }

        public void setPaymentThrough(String paymentThrough) {
            this.paymentThrough = paymentThrough;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
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

    public static class CourseType{
        private String courseTypeId;
        private String courseType;

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
