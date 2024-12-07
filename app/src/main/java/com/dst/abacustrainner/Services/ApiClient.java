package com.dst.abacustrainner.Services;

import com.dst.abacustrainner.Model.AssignmentExamResponse;
import com.dst.abacustrainner.Model.AssignmentListResponse;
import com.dst.abacustrainner.Model.AssignmentSubmitDataResponse;
import com.dst.abacustrainner.Model.BachDetailsResponse;
import com.dst.abacustrainner.Model.DatedetailsResponse;
import com.dst.abacustrainner.Model.GameResponse;
import com.dst.abacustrainner.Model.StudentDetails;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.Model.StudentScheduleInfo;
import com.dst.abacustrainner.Model.SubmitDataResponse;
import com.dst.abacustrainner.Model.TopicExamResponse;
import com.dst.abacustrainner.Model.TopicListResponse;
import com.dst.abacustrainner.Model.ViewAssignmentListResponse;
import com.dst.abacustrainner.Model.ViewAssignmentResultResponse;
import com.dst.abacustrainner.Model.ViewTopicListResponse;
import com.dst.abacustrainner.Model.ViewTopicResultResponse;


import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiClient {
    @Multipart
    @POST("apicalls/Index/studentRegistration")
    Call<StudentRegistationResponse> studentRegisterPost( @Part("firstName") RequestBody firstName,
                                                          @Part("lastName") RequestBody lastName,
                                                          @Part("emailId") RequestBody emailId,
                                                          @Part("mobileNumber") RequestBody mobileNumber,
                                                          @Part("gender") RequestBody gender,
                                                          @Part("motherTongue") RequestBody motherTongue,
                                                          @Part("dateOfBirth") RequestBody dateOfBirth);

    @Multipart
    @POST("apicalls/Index/verifyStudentAccount")
    Call<StudentRegistationResponse> VerifyPost(@Part("studentId") RequestBody studentId,
                                            @Part("otp") RequestBody otp,
                                            @Part("password") RequestBody password);

    @Multipart
    @POST("apicalls/Index/studentLogin")
    Call<StudentRegistationResponse> LoginPost(@Part("parentEmail") RequestBody parentEmail,
                                               @Part("password") RequestBody password);

    @Multipart
    @POST("apicalls/Index/getStudentSchedules")
    Call<StudentDetails> detailsPost(@Part("studentId") RequestBody studentId,
                                     @Part("currentDate") RequestBody currentDate );

    @Multipart
    @POST("apicalls/Index/getStudentScheduleInfo")
    Call<StudentScheduleInfo> scheduleInfo(@Part("studentId") RequestBody studentId,
                                           @Part("dateId") RequestBody dateId);

    @Multipart
    @POST("apicalls/Index/getStudentScheduleTopicsList")
    Call<TopicListResponse> topicList (@Part("studentId") RequestBody studentId,
                                       @Part("dateId") RequestBody dateId);

    @Multipart
    @POST("apicalls/Index/getStudentScheduleAssignmentTopicsList")
    Call<AssignmentListResponse> assignList (@Part("studentId") RequestBody studentId,
                                            @Part("dateId") RequestBody dateId);

    @Multipart
    @POST("apicalls/Index/getScheduleTopicPratices")
    Call<ViewTopicListResponse> viewTopicList(@Part("studentId") RequestBody studentId,
                                              @Part("topicId") RequestBody topicId);

    @Multipart
    @POST("apicalls/Index/getScheduleAssignmentTopicPratices")
    Call<ViewAssignmentListResponse> viewAssignmentList(@Part("studentId") RequestBody studentId,
                                                        @Part("topicId") RequestBody topicId);

    @Multipart
    @POST("apicalls/Index/getScheduleTopicPraticeResult")
    Call<ViewTopicResultResponse> viewResult(@Part("examRnm") RequestBody examRnm);

    @Multipart
    @POST("apicalls/Index/getScheduleAssignmentTopicPraticeResult")
    Call<ViewAssignmentResultResponse> viewAssignmentResult(@Part("examRnm") RequestBody examRnm);

    @Multipart
    @POST("apicalls/Index/startScheduleTopicExam")
    Call<TopicExamResponse> examList(@Part("studentId") RequestBody studentId,
                                     @Part("topicId") RequestBody topicId );

    @Multipart
    @POST("apicalls/Index/startScheduleAssignmentTopicExam")
    Call<AssignmentExamResponse> assignmentexam(@Part("studentId") RequestBody studentId,
                                                @Part("topicId") RequestBody topicId);

    @Multipart
    @POST("apicalls/Index/getStudentAllSchedules")
    Call<BachDetailsResponse> batchData(@Part("studentId") RequestBody studentId );

    @Multipart
    @POST("apicalls/Index/getStudentScheduleDates")
    Call<DatedetailsResponse> batchDateData(@Part("studentId") RequestBody studentId ,
                                            @Part("batchId") RequestBody batchId );


    @Multipart
    @POST("apicalls/Index/submitScheduleTopicExam")
    Call<SubmitDataResponse> submitData(@Part("examRnm") RequestBody examRnm,
                                        @Part("questionsList") RequestBody questionsList);

    @Multipart
    @POST("apicalls/Index/submitScheduleAssignmentTopicExam")
    Call<AssignmentSubmitDataResponse> assignmentSubmitData(@Part("examRnm") RequestBody examRnm,
                                                            @Part("questionsList") RequestBody questionsList);

    @Multipart
    @POST("apicalls/Index/submitNumberGameInfo")
    Call<GameResponse> gameData(@Part("studentId") RequestBody studentId,
                                @Part("questionsList") RequestBody questionsList ,
                                @Part("createdOn") RequestBody createdOn,
                                @Part("operation") RequestBody operation,
                                @Part("isVisualization") RequestBody isVisualization);
}
