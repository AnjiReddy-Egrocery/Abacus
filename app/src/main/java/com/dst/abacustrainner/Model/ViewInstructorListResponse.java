package com.dst.abacustrainner.Model;

import java.util.List;

public class ViewInstructorListResponse {

    private String status;
    private String errorCode;
    private List<Result> result;
    private String message;
    private String emptyTopicsessage;

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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmptyTopicsessage() {
        return emptyTopicsessage;
    }

    public void setEmptyTopicsessage(String emptyTopicsessage) {
        this.emptyTopicsessage = emptyTopicsessage;
    }
    public static class Result {
        private String paperId;
        private String instructorId;
        private String paperTitle;
        private List<QuestionModel> questions;
        private List<PracticeModel> practicesList;

        public String getPaperId() {
            return paperId;
        }

        public void setPaperId(String paperId) {
            this.paperId = paperId;
        }

        public String getInstructorId() {
            return instructorId;
        }

        public void setInstructorId(String instructorId) {
            this.instructorId = instructorId;
        }

        public String getPaperTitle() {
            return paperTitle;
        }

        public void setPaperTitle(String paperTitle) {
            this.paperTitle = paperTitle;
        }

        public List<QuestionModel> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionModel> questions) {
            this.questions = questions;
        }

        public List<PracticeModel> getPracticesList() {
            return practicesList;
        }

        public void setPracticesList(List<PracticeModel> practicesList) {
            this.practicesList = practicesList;
        }

        public class PracticeModel {
            private String practiceId;
            private String examRnm;
            private List<PracticeQuestionModel> questions;
            private String startedOn;
            private String submitedOn;

            public String getPracticeId() {
                return practiceId;
            }

            public void setPracticeId(String practiceId) {
                this.practiceId = practiceId;
            }

            public String getExamRnm() {
                return examRnm;
            }

            public void setExamRnm(String examRnm) {
                this.examRnm = examRnm;
            }

            public List<PracticeQuestionModel> getQuestions() {
                return questions;
            }

            public void setQuestions(List<PracticeQuestionModel> questions) {
                this.questions = questions;
            }

            public String getStartedOn() {
                return startedOn;
            }

            public void setStartedOn(String startedOn) {
                this.startedOn = startedOn;
            }

            public String getSubmitedOn() {
                return submitedOn;
            }

            public void setSubmitedOn(String submitedOn) {
                this.submitedOn = submitedOn;
            }

            public  class PracticeQuestionModel{
                private String question;
                private String answer;
                private String given;
                private int isCurrect;
                private String timeTaken;
                private int status;

                public String getQuestion() {
                    return question;
                }

                public void setQuestion(String question) {
                    this.question = question;
                }

                public String getAnswer() {
                    return answer;
                }

                public void setAnswer(String answer) {
                    this.answer = answer;
                }

                public String getGiven() {
                    return given;
                }

                public void setGiven(String given) {
                    this.given = given;
                }

                public int getIsCurrect() {
                    return isCurrect;
                }

                public void setIsCurrect(int isCurrect) {
                    this.isCurrect = isCurrect;
                }

                public String getTimeTaken() {
                    return timeTaken;
                }

                public void setTimeTaken(String timeTaken) {
                    this.timeTaken = timeTaken;
                }

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }
            }
        }

        public class QuestionModel {
            private String courseType;
            private String subCourseType;
            private String courseLevel;
            private String topic;
            private String question;
            private String answer;

            public String getCourseType() {
                return courseType;
            }

            public void setCourseType(String courseType) {
                this.courseType = courseType;
            }

            public String getSubCourseType() {
                return subCourseType;
            }

            public void setSubCourseType(String subCourseType) {
                this.subCourseType = subCourseType;
            }

            public String getCourseLevel() {
                return courseLevel;
            }

            public void setCourseLevel(String courseLevel) {
                this.courseLevel = courseLevel;
            }

            public String getTopic() {
                return topic;
            }

            public void setTopic(String topic) {
                this.topic = topic;
            }

            public String getQuestion() {
                return question;
            }

            public void setQuestion(String question) {
                this.question = question;
            }

            public String getAnswer() {
                return answer;
            }

            public void setAnswer(String answer) {
                this.answer = answer;
            }
        }
    }
}
