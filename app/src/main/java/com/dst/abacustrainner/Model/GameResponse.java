package com.dst.abacustrainner.Model;

public class GameResponse {
    private String status;
    private String errorCode;
    private Result result;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public class Result {

        private String numberGameId;
        private String studentId;
        private String gameInfo;
        private String createdOn;
        private String submitedOn;
        private String gameStatus;


        public String getNumberGameId() {
            return numberGameId;
        }

        public void setNumberGameId(String numberGameId) {
            this.numberGameId = numberGameId;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getGameInfo() {
            return gameInfo;
        }

        public void setGameInfo(String gameInfo) {
            this.gameInfo = gameInfo;
        }

        public String getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(String createdOn) {
            this.createdOn = createdOn;
        }

        public String getSubmitedOn() {
            return submitedOn;
        }

        public void setSubmitedOn(String submitedOn) {
            this.submitedOn = submitedOn;
        }

        public String getGameStatus() {
            return gameStatus;
        }

        public void setGameStatus(String gameStatus) {
            this.gameStatus = gameStatus;
        }



    }
}
