package com.carlv.medicaldeliveryrider.Helpers;

import com.carlv.medicaldeliveryrider.Models.Delivery;
import com.carlv.medicaldeliveryrider.Models.Hospital;
import com.carlv.medicaldeliveryrider.Models.Medicine;
import com.carlv.medicaldeliveryrider.Models.Rider;
import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Service {
    @GET("delivery/{id}")
    Call<Delivery> getDelivery(@Path("id") String id);
    @GET("medicine/{id}")
    Call<Medicine> getMedicine(@Path("id") String id);
    @GET("hospital")
    Call<JsonArray> readJsonArray();
    @GET("hospital/{id}")
    Call<Hospital> getHospital(@Path("id") String id);
    @GET("delivery")
    Call<JsonArray> readJsonArrayDeliveries();
    @GET("delivery/getnewdeliveries")
    Call<JsonArray> readJsonArrayNewDeliveries();
    @GET("delivery/getbyrider/{id}")
    Call<JsonArray> readJsonArrayDeliveriesByRiderID(@Path("id") String id);
    @GET("rider")
    Call<JsonArray> readJsonArrayRiders();
    @GET("medicine")
    Call<JsonArray> readJsonArrayMedicine();
    @GET("update/{id}")
    Call<JsonArray> readJsonArrayUpdates(@Path("id") String id);
    @POST("hospital")
    Call<Hospital> createHospital(@Body Hospital hospital);
    @POST("rider")
    Call<Rider> createRider(@Body Rider rider);
    @POST("medicine")
    Call<Medicine> createMedicine(@Body Medicine medicine);
    @PUT("hospital/{id}")
    Call<Hospital> updateHospital(@Path("id") String id, @Body Hospital hospital);
    @PUT("medicine/{id}")
    Call<Medicine> updateMedicine(@Path("id") String id, @Body Medicine medicine);
    @PUT("delivery/{id}")
    Call<Delivery> updateDelivery(@Path("id") String id, @Body Delivery delivery);
    @PUT("delivery/notification/rider/{id}")
    Call<Delivery> updateNotificationRider(@Path("id") String id, @Body Delivery delivery);
    @PUT("delivery/notification/response/{id}")
    Call<Delivery> updateResponse(@Path("id") String id, @Body Delivery delivery);
}
