package com.app.ordermunch.API;
import com.orhanobut.hawk.Hawk;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://ordermunch.vercel.app/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


            httpClient.addInterceptor(chain -> {
                Request originalRequest = chain.request();
                // Creating request and adding user jwt token as header
                Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + Hawk.get("jwtToken", ""))
                        .build();
                return chain.proceed(newRequest);
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build()) // Set the custom OkHttpClient
                    .build();
        }
        return retrofit;
    }
}

