package com.dst.abacustrainner.Services;

import com.dst.abacustrainner.Model.AssignmentListResponse;
import com.dst.abacustrainner.Model.TopicListResponse;

import java.util.List;

public interface TopicsCallback {

    void onTopicsReceived(List<TopicListResponse.Result.Topics> topicsList);


}




