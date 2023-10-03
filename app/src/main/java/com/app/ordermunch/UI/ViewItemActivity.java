package com.app.ordermunch.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Cart.CartRequest;
import com.app.ordermunch.API.Models.Cart.CartResponse;
import com.app.ordermunch.API.Models.Item.SingleItemResponse;
import com.app.ordermunch.Models.Item;
import com.app.ordermunch.R;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ViewItemActivity extends AppCompatActivity {

    RelativeLayout mainLayout;
    TextView itemName, restaurantName, itemDescription, itemPrice, itemRating, itemRatedBy;

    ImageView itemImage, restaurantImage;

    Button buyNowBtn, addToCartBtn;

    ApiService apiService;
    CustomProgressDialog customProgressDialog;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        context = this;

        apiService = ApiClient.getClient().create(ApiService.class);
        customProgressDialog = new CustomProgressDialog(this);

        mainLayout = findViewById(R.id.viewItemLayout);

        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        itemPrice = findViewById(R.id.itemPrice);
        itemRating = findViewById(R.id.itemRating);
        itemRatedBy = findViewById(R.id.itemRatedBy);
        restaurantName= findViewById(R.id.restaurantName);
        restaurantImage = findViewById(R.id.restaurantImage);
        itemImage = findViewById(R.id.itemImage);

        buyNowBtn = findViewById(R.id.buyNowBtn);
        addToCartBtn = findViewById(R.id.addToCartBtn);

        mainLayout.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        String itemId = intent.getStringExtra("id");


        customProgressDialog.showProgressDialog("Loading item");
        Call<SingleItemResponse> itemCall = apiService.getItemById(itemId);

        itemCall.enqueue(new Callback<SingleItemResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<SingleItemResponse> call, Response<SingleItemResponse> response) {

                customProgressDialog.hide();
                mainLayout.setVisibility(View.VISIBLE);


                // If login is successful
                if (response.isSuccessful()) {
                    SingleItemResponse itemsResponse = response.body();

                    assert itemsResponse != null;
                    Item item = itemsResponse.getItem();

                    itemName.setText(item.getName());
                    itemPrice.setText("MYR. "+ item.getPrice());
                    itemRating.setText(item.getRating() + "/5");
                    itemRatedBy.setText("("+ item.getRatedBy() +")");
                    restaurantName.setText(item.getRestaurant().getName());

                    if (item.getDescription().isEmpty()) {
                        itemDescription.setText("No description");
                    }
                    else {
                        itemDescription.setText(item.getDescription());

                    }
                    try {
                        Picasso.get().load(item.getImage()).into(itemImage);
                    }
                    catch (Exception e) {}

                    try {
                        Picasso.get().load(item.getRestaurant().getImage()).into(restaurantImage);
                    }
                    catch (Exception e) {}
                }

                // If login failed
                else {
                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(ViewItemActivity.this, R.drawable.em_sad, errorMessage);

                }
            }


            // If request failed due to a network error
            @Override
            public void onFailure(Call<SingleItemResponse> call, Throwable t) {

                customProgressDialog.hide();
                Toast.makeText(ViewItemActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                // Parsing error
                ApiException apiException = ApiErrorUtils.parseError(t);

                // Getting error message
                String errorMessage = apiException.getErrorMessage();

                // Show error message
                CustomAlert.showCustomDialog(ViewItemActivity.this, R.drawable.em_sad, errorMessage);

            }
        });

        addToCartBtn.setOnClickListener(v-> {
            customProgressDialog.showProgressDialog("Please wait");

            // Calling API
            CartRequest cartRequest = new CartRequest(itemId);
            Call<CartResponse> loginCall = apiService.addToCart(cartRequest);

            loginCall.enqueue(new Callback<CartResponse>() {
                @Override
                public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                    customProgressDialog.hide();
                    // If request is successful
                    if (response.isSuccessful()) {

                        CustomAlert.showCustomDialog(context, R.drawable.em_happy, "Item added to cart!");

                    }

                    // If request failed
                    else {
                        // Parsing Error
                        ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                        // Getting error message
                        String errorMessage = apiException.getErrorMessage();

                        // Showing message in toast
                        CustomAlert.showCustomDialog(context, R.drawable.em_sad, errorMessage);
                    }
                }


                // If request failed due to a network error
                @Override
                public void onFailure(Call<CartResponse> call, Throwable t) {
                    customProgressDialog.hide();

                    // Getting error message
                    String errorMessage = "Network error";

                    // Show error message
                    CustomAlert.showCustomDialog(context, R.drawable.em_sad, errorMessage);
                }
            });
        });
    }
}