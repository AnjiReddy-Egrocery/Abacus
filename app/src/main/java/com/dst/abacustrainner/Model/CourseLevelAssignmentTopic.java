package com.dst.abacustrainner.Model;

import java.util.List;

public class CourseLevelAssignmentTopic {

    private String topicId;

    private String topic;
    private List<TopicPractice> topicPractices;

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<TopicPractice> getTopicPractices() {
        return topicPractices;
    }

    public void setTopicPractices(List<TopicPractice> topicPractices) {
        this.topicPractices = topicPractices;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getTopic() {
        return topic;
    }

}
