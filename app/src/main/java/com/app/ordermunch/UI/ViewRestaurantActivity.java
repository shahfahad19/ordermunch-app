package com.app.ordermunch.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Item.ItemResponse;
import com.app.ordermunch.API.Models.Restaurant.SingleRestaurantResponse;
import com.app.ordermunch.Adapters.ItemAdapter;
import com.app.ordermunch.Models.Item;
import com.app.ordermunch.Models.Restaurant;
import com.app.ordermunch.R;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ViewRestaurantActivity extends AppCompatActivity implements ItemAdapter.ItemClickListener {

    ApiService apiService;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;

    EditText searchEditText;
    List<Item> itemList;
    CustomProgressDialog customProgressDialog;

    String restaurantId = "";

    TextView restaurantName;

    ImageView restaurantLogo, restaurantBg;

    RelativeLayout mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);

        Intent intent = getIntent();
        restaurantId = intent.getStringExtra("restaurantId");

        itemList = new ArrayList<>();

        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.showProgressDialog("Loading restaurant");

        apiService = ApiClient.getClient().create(ApiService.class);

        mainView = findViewById(R.id.mainView);
        recyclerView = findViewById(R.id.recyclerView);
        searchEditText = findViewById(R.id.itemSearchBox);
        restaurantName = findViewById(R.id.restaurantName);
        restaurantLogo = findViewById(R.id.restaurantLogo);
        restaurantBg = findViewById(R.id.restaurantBg);

        itemAdapter = new ItemAdapter(this, this);
        recyclerView.setAdapter(itemAdapter);

        mainView.setVisibility(View.GONE);
        getRestaurant();
        getItems();



        // Search box
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the recipe list based on the search text
                String searchText = s.toString().toLowerCase().trim();

                if (searchText.isEmpty()) {

                }
                else {

                }

                List<Item> filteredList = new ArrayList<>();
                for (Item item : itemList) {
                    if (item.getName().toLowerCase().contains(searchText)) {
                        filteredList.add(item);
                    }
                }

                // Update the recyclerview adapter with filtered data
                itemAdapter.updateData(filteredList);
            }

            // These functions are necessary to avoid errors
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is intentionally left blank
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is intentionally left blank
            }
        });

    }

    public void getRestaurant() {
        Call<SingleRestaurantResponse> requestCall = apiService.getRestaurantById(restaurantId);

        requestCall.enqueue(new Callback<SingleRestaurantResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<SingleRestaurantResponse> call, Response<SingleRestaurantResponse> response) {


                // if request is successful
                if (response.isSuccessful()) {
                    SingleRestaurantResponse restaurantResponse = response.body();

                    assert restaurantResponse != null;
                    Restaurant restaurant = restaurantResponse.getRestaurant();
                    restaurantName.setText(restaurant.getName());


                    try {
                        Picasso.get().load(restaurant.getImage()).into(restaurantLogo);
                    }
                    catch (Exception e) {}

                    try {
                        Picasso.get().load(restaurant.getImage()).into(restaurantBg);
                    }
                    catch (Exception e) {}
                }

                // if request failed
                else {
                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(ViewRestaurantActivity.this, R.drawable.em_sad, errorMessage);

                }
            }


            // If request failed due to a network error
            @Override
            public void onFailure(Call<SingleRestaurantResponse> call, Throwable t) {

                customProgressDialog.hide();
                Toast.makeText(ViewRestaurantActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                // Parsing error
                ApiException apiException = ApiErrorUtils.parseError(t);

                // Getting error message
                String errorMessage = apiException.getErrorMessage();

                // Show error message
                CustomAlert.showCustomDialog(ViewRestaurantActivity.this, R.drawable.em_sad, errorMessage);

            }
        });

    }

    public void getItems() {

        Call<ItemResponse> requestCall = apiService.getItems(999, restaurantId);

        requestCall.enqueue(new Callback<ItemResponse>() {
            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {

                customProgressDialog.hide();
                mainView.setVisibility(View.VISIBLE);


                // if request is successful
                if (response.isSuccessful()) {

                    ItemResponse itemsResponse = response.body();
                    itemList = itemsResponse.getItemList();
                    if (itemList.isEmpty()) {
                        LinearLayout notFound = findViewById(R.id.noFoundView);
                        notFound.setVisibility(View.VISIBLE);
                    }
                    else {
                        itemAdapter.updateData(itemList);
                    }

                }

                // if request failed
                else {

                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(ViewRestaurantActivity.this, R.drawable.em_sad, errorMessage);

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
                CustomAlert.showCustomDialog(ViewRestaurantActivity.this, R.drawable.em_sad, errorMessage);

            }
        });


    }

    @Override
    public void onItemClickListener(String id) {
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}