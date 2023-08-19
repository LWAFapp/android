package com.Zakovskiy.lwaf.api;

import com.Zakovskiy.lwaf.models.ApiResponse;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface VKApi {

    @Headers("User-Agent: VKAndroidApp/7.2.10076 (Android 9; SDK 28; A; F; zakovskiy; ru)")
    @GET("audio.get?v=5.102&count=20")
    Call<ResponseBody> audioGet (@Query("offset") int offset,@Query("access_token") String access_token, @Query("owner_id") Integer owner_id, @Query("sig") String sig);
    @Headers("User-Agent: VKAndroidApp/7.2.10076 (Android 9; SDK 28; A; F; zakovskiy; ru)")
    @GET("audio.search?v=5.102&count=20")
    Call<ResponseBody> audioSearch (@Query("offset") int offset, @Query("access_token") String access_token, @Query("q") String search_text, @Query("sig") String sig);

    @Headers("User-Agent: VKAndroidApp/7.2.10076 (Android 9; SDK 28; A; F; zakovskiy; ru)")
    @GET("audio.getById?v=5.102")
    Call<ResponseBody> audioGetById (@Query("access_token") String access_token, @Query("audios") String audios, @Query("sig") String sig);

    @Headers("User-Agent: VKAndroidApp/7.2.10076 (Android 9; SDK 28; A; F; zakovskiy; ru)")
    @GET("token?scope=nohttps,audio,offline,stats&client_id=2274003&client_secret=hHbZxrka2uZ6jB1inYsH&lang=ru&grant_type=password&2fa_supported=1")
    Call<ResponseBody> logIn(@Query("username") String username, @Query("password") String password, @Query("captcha_sid") String captcha_sid, @Query("captcha_key") String captcha_key);
}