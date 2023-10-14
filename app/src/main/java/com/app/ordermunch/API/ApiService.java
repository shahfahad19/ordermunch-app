package com.app.ordermunch.API;

import com.app.ordermunch.API.Models.Auth.LoginRequest;
import com.app.ordermunch.API.Models.Auth.LoginResponse;
import com.app.ordermunch.API.Models.Auth.SignupRequest;
import com.app.ordermunch.API.Models.Auth.SignupResponse;
import com.app.ordermunch.API.Models.Cart.CartRequest;
import com.app.ordermunch.API.Models.Cart.CartResponse;
import com.app.ordermunch.API.Models.Item.ItemResponse;
import com.app.ordermunch.API.Models.Item.SingleItemResponse;
import com.app.ordermunch.API.Models.Order.OrderRequest;
import com.app.ordermunch.API.Models.Order.OrdersResponse;
import com.app.ordermunch.API.Models.Order.QuickOrderRequest;
import com.app.ordermunch.API.Models.Order.SingleOrderResponse;
import com.app.ordermunch.API.Models.Profile.ProfileResponse;
import com.app.ordermunch.API.Models.Profile.UpdatePasswordRequest;
import com.app.ordermunch.API.Models.Profile.UpdateProfileRequest;
import com.app.ordermunch.API.Models.Restaurant.RestaurantsResponse;
import com.app.ordermunch.API.Models.Restaurant.SingleRestaurantResponse;
import com.app.ordermunch.API.Models.Review.AddReviewRequest;
import com.app.ordermunch.API.Models.Review.UpdateReviewRequest;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/signup")
    Call<SignupResponse> signup(@Body SignupRequest request);
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("user")
    Call<ProfileResponse> getProfile();

    @PATCH("auth/updateProfile")
    Call<ProfileResponse> updateProfile(@Body UpdateProfileRequest updateProfileRequest);

    @PATCH("auth/updatePassword")
    Call<LoginResponse> updatePassword(@Body UpdatePasswordRequest updatePasswordRequest);

    @GET("restaurants")
    Call<RestaurantsResponse> getRestaurants();

    @GET("restaurants/{id}")
    Call<SingleRestaurantResponse> getRestaurantById(@Path("id") String restaurantId);

    @GET("items")
    Call<ItemResponse> getItems(@Query("limit") int limit);

    @GET("items")
    Call<ItemResponse> getItems(@Query("limit") int limit, @Query("restaurant") String restaurantId);

    @GET("items/{id}")
    Call<SingleItemResponse> getItemById(@Path("id") String itemId);

    @GET("orders")
    Call<OrdersResponse> getOrders(@Query("limit") int limit);

    @POST("orders")
    Call<SingleOrderResponse> buyNow(@Body QuickOrderRequest request);

    @GET("orders/{id}")
    Call<SingleOrderResponse> getOrderById(@Path("id") String orderId);

    @PATCH("orders/{id}")
    Call<SingleOrderResponse> updateOrder(@Path("id") String orderId, @Body OrderRequest orderRequest);

    @POST("reviews")
    Call<Void> postReview(@Body AddReviewRequest addReviewRequest);

    @PATCH("reviews/{id}")
    Call<Void> updateReview(@Path("id") String reviewId, @Body UpdateReviewRequest updateReviewRequest);

    @GET("cart")
    Call<CartResponse> getCart();

    @GET("cart/checkout")
    Call<SingleOrderResponse> checkout();

    @PATCH("cart/add")
    Call<CartResponse> addToCart(@Body CartRequest cartRequest);

    @PATCH("cart/remove")
    Call<CartResponse> removeFromCart(@Body CartRequest cartRequest);

    @PATCH("cart/delete")
    Call<CartResponse> deleteFromCart(@Body CartRequest cartRequest);

    @DELETE("cart/clear")
    Call<CartResponse> clearCart();





}
