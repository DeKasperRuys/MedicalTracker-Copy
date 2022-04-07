package com.carlv.medicaldeliverydoctor.Helpers;

import com.carlv.medicaldeliverydoctor.Models.Delivery;
import com.carlv.medicaldeliverydoctor.Models.Hospital;
import com.carlv.medicaldeliverydoctor.Models.Medicine;
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
    @GET("hospital")
    Call<JsonArray> readJsonArray();
    @GET("delivery/getbyhospital/{id}")
    Call<JsonArray> readJsonArrayDeliveries(@Path("id") String id);
    @GET("medicine")
    Call<JsonArray> readJsonArrayMedicine();
    @GET("update/{id}")
    Call<JsonArray> readJsonArrayUpdates(@Path("id") String id);
    @POST("hospital")
    Call<Hospital> createHospital(@Body Hospital hospital);
    @POST("medicine")
    Call<Medicine> createMedicine(@Body Medicine medicine);
    @PUT("delivery/notification/doctor/{id}")
    Call<Delivery> updateNotificationDoctor(@Path("id") String id, @Body Delivery delivery);
    @PUT("delivery/response/{id}")
    Call<Delivery> updateNotificationResponse(@Path("id") String id, @Body Delivery delivery);
    @POST("delivery/{hospitalID}/{medicineID}")
    Call<Delivery> createDelivery(@Path("hospitalID")String hospitalID, @Path("medicineID") String medicineID, @Body Delivery delivery);
    @PUT("hospital/{id}")
    Call<Hospital> updateHospital(@Path("id") String id, @Body Hospital hospital);
    @PUT("medicine/{id}")
    Call<Medicine> updateMedicine(@Path("id") String id, @Body Medicine medicine);
    @PUT("delivery/{id}")
    Call<Delivery> updateDelivery(@Path("id") String id, @Body Delivery delivery);
}
