package com.Zakovskiy.lwaf.api;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LWAFApi {

    @Multipart
    @POST("upload/photo")
    Call<ResponseBody> uploadPhoto(@Query("token") String token, @Part MultipartBody.Part file);

    @Multipart
    @POST("upload/preview")
    Call<ResponseBody> uploadPreview(@Query("token") String token, @Part MultipartBody.Part file);

    @GET("{resource}")
    Call<ResponseBody> getResource(@Path("resource") String resource);
}
