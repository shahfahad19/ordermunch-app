package com.app.ordermunch.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Restaurant.RestaurantsResponse;
import com.app.ordermunch.Adapters.RestaurantAdapter;
import com.app.ordermunch.Models.Restaurant;
import com.app.ordermunch.R;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class RestaurantsActivity extends AppCompatActivity {

    ApiService apiService;
    RecyclerView recyclerView;
    RestaurantAdapter restaurantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        CustomProgressDialog customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.showProgressDialog("Loading Restaurants");

        apiService = ApiClient.getClient().create(ApiService.class);
        restaurantAdapter = new RestaurantAdapter(this, true);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(restaurantAdapter);

        Call<RestaurantsResponse> restaurantCall = apiService.getRestaurants();

        restaurantCall.enqueue(new Callback<RestaurantsResponse>() {
            @Override
            public void onResponse(Call<RestaurantsResponse> call, Response<RestaurantsResponse> response) {

                customProgressDialog.hide();

                // If login is successful
                if (response.isSuccessful()) {
                    RestaurantsResponse restaurantsResponse = response.body();
                    List<Restaurant> restaurantList = restaurantsResponse.getRestaurants();
                    showRestaurants(restaurantList);

                }

                // If login failed
                else {

                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(RestaurantsActivity.this, R.drawable.em_sad, errorMessage);

                }
            }


            // If request failed due to a network error
            @Override
            public void onFailure(Call<RestaurantsResponse> call, Throwable t) {

                customProgressDialog.hide();

                // Parsing error
                ApiException apiException = ApiErrorUtils.parseError(t);

                // Getting error message
                String errorMessage = apiException.getErrorMessage();

                // Show error message
                CustomAlert.showCustomDialog(RestaurantsActivity.this, R.drawable.em_sad, errorMessage);

            }
        });

    }

    public void showRestaurants(List<Restaurant> restaurantList) {
        if (restaurantList.isEmpty()) {
            LinearLayout notFound = findViewById(R.id.noFoundView);
            notFound.setVisibility(View.VISIBLE);
        }
        else {
            restaurantAdapter.updateData(restaurantList);        }

    }
}