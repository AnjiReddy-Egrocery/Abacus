package com.dst.abacustrainner.Services;

import static java.security.AccessController.getContext;

import android.widget.Toast;

import com.dst.abacustrainner.Model.TopicListResponse;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface TopicsCallback {

    void onTopicsReceived(List<TopicListResponse.Result.Topics> topicsList);
}




