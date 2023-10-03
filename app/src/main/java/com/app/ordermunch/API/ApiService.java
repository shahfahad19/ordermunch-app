package com.app.ordermunch.API;

import com.app.ordermunch.API.Models.Auth.LoginRequest;
import com.app.ordermunch.API.Models.Auth.LoginResponse;
import com.app.ordermunch.API.Models.Cart.CartRequest;
import com.app.ordermunch.API.Models.Cart.CartResponse;
import com.app.ordermunch.API.Models.Item.ItemResponse;
import com.app.ordermunch.API.Models.Item.SingleItemResponse;
import com.app.ordermunch.API.Models.Restaurant.RestaurantsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("restaurants")
    Call<RestaurantsResponse> getRestaurants();

    @GET("items")
    Call<ItemResponse> getItems(@Query("limit") int limit);

    @GET("items/{id}")
    Call<SingleItemResponse> getItemById(@Path("id") String itemId);

    @GET("cart")
    Call<CartResponse> getCart();

    @PATCH("cart/add")
    Call<CartResponse> addToCart(@Body CartRequest cartRequest);

    @PATCH("cart/remove")
    Call<CartResponse> removeFromCart(@Body CartRequest cartRequest);

    @PATCH("cart/delete")
    Call<CartResponse> deleteFromCart(@Body CartRequest cartRequest);

    @DELETE("cart/clear")
    Call<CartResponse> clearCart();





}
