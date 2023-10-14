package com.app.ordermunch.UI.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Cart.CartRequest;
import com.app.ordermunch.API.Models.Cart.CartResponse;
import com.app.ordermunch.API.Models.Item.ItemResponse;
import com.app.ordermunch.API.Models.Restaurant.RestaurantsResponse;
import com.app.ordermunch.Adapters.CartAdapter;
import com.app.ordermunch.Adapters.ItemAdapter;
import com.app.ordermunch.Adapters.RestaurantAdapter;
import com.app.ordermunch.Models.CartItem;
import com.app.ordermunch.Models.Item;
import com.app.ordermunch.Models.Restaurant;
import com.app.ordermunch.R;
import com.app.ordermunch.UI.ItemsActivity;
import com.app.ordermunch.UI.LoginActivity;
import com.app.ordermunch.UI.RestaurantsActivity;
import com.app.ordermunch.UI.SearchItemsActivity;
import com.app.ordermunch.UI.ViewItemActivity;
import com.app.ordermunch.UI.ViewRestaurantActivity;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;
import com.orhanobut.hawk.Hawk;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class DashboardFragment extends Fragment implements ItemAdapter.ItemClickListener, RestaurantAdapter.RestaurantClickListener {


    RecyclerView restaurantRecyclerView, itemRecyclerView;

    TextView seeAllRestaurants, seeAllItems;

    RestaurantAdapter restaurantAdapter;
    ItemAdapter itemAdapter;

    ApiService apiService;

    CustomProgressDialog customProgressDialog;

    ScrollView dashboardMainView;

    Button seeAllItemsButton;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        apiService = ApiClient.getClient().create(ApiService.class);

        restaurantRecyclerView = view.findViewById(R.id.restaurantRecyclerView);
        itemRecyclerView = view.findViewById(R.id.itemRecyclerView);
        seeAllRestaurants = view.findViewById(R.id.seeAllRestaurantsText);
        seeAllItemsButton = view.findViewById(R.id.seeAllItemsBtn);
        dashboardMainView = view.findViewById(R.id.dashboardMainView);

        restaurantAdapter = new RestaurantAdapter(getContext(), this, false);
        itemAdapter = new ItemAdapter(getContext(), this);

        restaurantRecyclerView.setAdapter(restaurantAdapter);
        itemRecyclerView.setAdapter(itemAdapter);

        seeAllRestaurants.setOnClickListener(v->startActivity(new Intent(getContext(), RestaurantsActivity.class)));
        seeAllItemsButton.setOnClickListener(v->startActivity(new Intent(getContext(), ItemsActivity.class)));


        customProgressDialog = new CustomProgressDialog(getContext());

        dashboardMainView.setVisibility(View.GONE);
        fetchRestaurants();

        return view;
    }

    public void fetchRestaurants() {
        customProgressDialog.showProgressDialog("Loading restaurants");
        Call<RestaurantsResponse> restaurantCall = apiService.getRestaurants();

        restaurantCall.enqueue(new Callback<RestaurantsResponse>() {
            @Override
            public void onResponse(Call<RestaurantsResponse> call, Response<RestaurantsResponse> response) {

                customProgressDialog.hide();

                // If login is successful
                if (response.isSuccessful()) {
                    RestaurantsResponse restaurantsResponse = response.body();
                    List<Restaurant> restaurantList = restaurantsResponse.getRestaurants();
                    restaurantAdapter.updateData(restaurantList);

                    customProgressDialog.showProgressDialog("Loading items");
                    fetchItems();
                }

                // If login failed
                else {

                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);

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
                CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);

            }
        });

    }

    public void fetchItems() {
        Call<ItemResponse> itemCall = apiService.getItems(6);

        itemCall.enqueue(new Callback<ItemResponse>() {
            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {

                customProgressDialog.hide();

                // If login is successful
                if (response.isSuccessful()) {
                    ItemResponse itemsResponse = response.body();
                    List<Item> itemList = itemsResponse.getItemList();
                    itemAdapter.updateData(itemList);
                    dashboardMainView.setVisibility(View.VISIBLE);
                }

                // If login failed
                else {

                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);

                }
            }


            // If request failed due to a network error
            @Override
            public void onFailure(Call<ItemResponse> call, Throwable t) {

                customProgressDialog.hide();

                // Parsing error
                ApiException apiException = ApiErrorUtils.parseError(t);

                // Getting error message
                String errorMessage = apiException.getErrorMessage();

                // Show error message
                CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);

            }
        });

    }

    @Override
    public void onItemClickListener(String id) {
        Intent intent = new Intent(getContext(), ViewItemActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onRestaurantClickListener(String id) {
        Intent intent = new Intent(getContext(), ViewRestaurantActivity.class);
        intent.putExtra("restaurantId", id);
        startActivity(intent);
    }
}