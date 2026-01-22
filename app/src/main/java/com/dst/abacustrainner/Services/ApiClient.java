package com.dst.abacustrainner.Services;

import com.dst.abacustrainner.Model.AssignmentExamResponse;
import com.dst.abacustrainner.Model.AssignmentListResponse;
import com.dst.abacustrainner.Model.AssignmentSubmitDataResponse;
import com.dst.abacustrainner.Model.BachDetailsResponse;
import com.dst.abacustrainner.Model.CourseLevelResponse;
import com.dst.abacustrainner.Model.CourseLevelTopicResponse;
import com.dst.abacustrainner.Model.CourseListResponse;
import com.dst.abacustrainner.Model.CourseTopicExamResponse;
import com.dst.abacustrainner.Model.CoursesListResponse;
import com.dst.abacustrainner.Model.DatedetailsResponse;
import com.dst.abacustrainner.Model.DurationListResponse;
import com.dst.abacustrainner.Model.ForgotPassword;
import com.dst.abacustrainner.Model.GameResponse;
import com.dst.abacustrainner.Model.LevelPriceResponse;
import com.dst.abacustrainner.Model.OrderListResponse;
import com.dst.abacustrainner.Model.StudentDetails;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.Model.StudentScheduleInfo;
import com.dst.abacustrainner.Model.StudentTotalDetails;
import com.dst.abacustrainner.Model.StudentUpdateProfile;
import com.dst.abacustrainner.Model.SubmitDataResponse;
import com.dst.abacustrainner.Model.TopicExamResponse;
import com.dst.abacustrainner.Model.TopicListResponse;
import com.dst.abacustrainner.Model.ViewAssignmentListResponse;
import com.dst.abacustrainner.Model.ViewAssignmentResultResponse;
import com.dst.abacustrainner.Model.ViewTopicListResponse;
import com.dst.abacustrainner.Model.ViewTopicResultResponse;
import com.dst.abacustrainner.Model.WorkSheetSubmitDataResponse;


import okhttp3.MultipartBody;
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
                                                          @Part("gender") RequestBody gender,
                                                          @Part("dateOfBirth") RequestBody dateOfBirth,                                                           @Part("motherTongue") RequestBody motherTongue,
                                                          @Part("emailId") RequestBody emailId,
                                                          @Part("mobileNumber") RequestBody mobileNumber);

    @Multipart
    @POST("apicalls/Index/updateStudentProfile")
    Call<StudentUpdateProfile> studentUpdatePost(@Part("studentId") RequestBody studentId,
                                                 @Part("firstName") RequestBody firstName,
                                                 @Part("middleName") RequestBody middleName,
                                                 @Part("lastName") RequestBody lastName,
                                                 @Part("dateOfBirth") RequestBody dateOfBirth,
                                                 @Part("gender") RequestBody gender,
                                                 @Part("motherTongue") RequestBody motherTongue,
                                                 @Part("fatherName") RequestBody fatherName,
                                                 @Part("motherName") RequestBody motherName,
                                                 @Part MultipartBody.Part profilePic); // Image name as part);

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
    @POST("apicalls/Index/getStudentDetails")
    Call<StudentTotalDetails> studentData(@Part("studentId") RequestBody studentId );

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

    @Multipart
    @POST("apicalls/Index/studentForgotPassword")
    Call<ForgotPassword> forgot(@Part("userName") RequestBody userName);



    @POST("apicalls/Index/getCourseTypesList")
    Call<CoursesListResponse> getCoursesList();

    @POST("apicalls/Index/getDurationsList")
    Call<DurationListResponse> getDurationList();

    @Multipart
    @POST("apicalls/Index/getCourseTypeInfo")
    Call<CourseLevelResponse> updateLevelList(@Part("courseTypeId") RequestBody courseTypeId);

    @Multipart
    @POST("apicalls/Index/getWorksheetCourseLevelPrice")
    Call<LevelPriceResponse> getLevelPrice(
            @Part("courseLevelId") RequestBody courseLevelId,
            @Part("durationId") RequestBody durationId
    );

    @Multipart
    @POST("apicalls/Index/getStudentWorksheetOrdersList")
    Call<OrderListResponse> getOrderList(
            @Part("studentId") RequestBody studentId
    );
    @Multipart
    @POST("apicalls/Index/getStudentWorksheetCoursesList")
    Call<CourseListResponse> getCourseList(
            @Part("studentId") RequestBody studentId
    );

    @Multipart
    @POST("apicalls/Index/getStudentWorksheetCourseLevelInfo")
    Call<CourseLevelTopicResponse> getCourseLevelTopic(
            @Part("studentId") RequestBody studentId,
            @Part("courseLevelId") RequestBody courseLevelId
    );

    @Multipart
    @POST("apicalls/Index/startWorksheetTopicExam")
    Call<CourseTopicExamResponse> topicExamList(@Part("studentId") RequestBody studentId,
                                           @Part("topicId") RequestBody topicId );

    @Multipart
    @POST("apicalls/Index/submitWorksheetTopicExam")
    Call<WorkSheetSubmitDataResponse> worksheetDataResponse(@Part("examRnm") RequestBody examRnm,
                                                 @Part("questionsList") RequestBody questionsList);
}
