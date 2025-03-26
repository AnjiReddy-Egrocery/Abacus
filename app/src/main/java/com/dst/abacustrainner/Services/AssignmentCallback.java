package com.dst.abacustrainner.Services;

import com.dst.abacustrainner.Model.AssignmentListResponse;
import com.dst.abacustrainner.Model.TopicListResponse;

import java.util.List;

public interface AssignmentCallback {

    void onAssignmentReceived(List<AssignmentListResponse.Result.AssignmentTopics> assignmentTopicsList);
}
