package com.dst.abacustrainner.Services;

import com.dst.abacustrainner.Model.OrderRequest;
import com.dst.abacustrainner.Model.OrderResponse;
import com.dst.abacustrainner.Model.OrderStatusResponse;
import com.dst.abacustrainner.Model.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PhonePeApi {

    @FormUrlEncoded
    @POST("apis/identity-manager/v1/oauth/token")
    Call<TokenResponse> generateToken(
            @Field("client_id") String clientId,
            @Field("client_version") String clientVersion,
            @Field("client_secret") String clientSecret,
            @Field("grant_type") String grantType
    );

    @POST("apis/pg/checkout/v2/sdk/order")
    Call<OrderResponse> createOrder(
            @Header("Authorization") String authToken,
            @Body OrderRequest request
    );

    @GET("apis/pg/checkout/v2/order/{merchantOrderId}/status")
    Call<OrderStatusResponse> checkOrderStatus(
            @Header("Authorization") String authToken,
            @Path("merchantOrderId") String merchantOrderId,
            @Query("details") boolean details,
            @Query("errorContext") boolean errorContext
    );

}