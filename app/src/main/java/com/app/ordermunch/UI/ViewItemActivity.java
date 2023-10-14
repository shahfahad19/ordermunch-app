package com.app.ordermunch.UI;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.app.ordermunch.API.Models.Order.QuickOrderRequest;
import com.app.ordermunch.API.Models.Order.SingleOrderResponse;
import com.app.ordermunch.Adapters.ReviewsAdapter;
import com.app.ordermunch.Models.Item;
import com.app.ordermunch.Models.ItemReview;
import com.app.ordermunch.Models.Order;
import com.app.ordermunch.R;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ViewItemActivity extends AppCompatActivity {

    RelativeLayout mainLayout;
    TextView itemName, restaurantName, itemDescription, itemPrice, itemRating, itemRatedBy, noReviewsText;

    ImageView itemImage, restaurantImage;

    Button buyNowBtn, addToCartBtn;

    ApiService apiService;
    CustomProgressDialog customProgressDialog;

    Context context;

    RecyclerView reviewsRecyclerView;
    ReviewsAdapter reviewsAdapter;

    TextView seeAllReviewsBtn;

    Item item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        context = this;

        apiService = ApiClient.getClient().create(ApiService.class);
        customProgressDialog = new CustomProgressDialog(this);
        reviewsAdapter = new ReviewsAdapter(this);

        mainLayout = findViewById(R.id.viewItemLayout);

        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        itemPrice = findViewById(R.id.itemPrice);
        itemRating = findViewById(R.id.itemRating);
        itemRatedBy = findViewById(R.id.itemRatedBy);
        restaurantName= findViewById(R.id.restaurantName);
        restaurantImage = findViewById(R.id.restaurantImage);
        itemImage = findViewById(R.id.itemImage);
        noReviewsText = findViewById(R.id.noReviewsText);
        reviewsRecyclerView = findViewById(R.id.recyclerView);
        seeAllReviewsBtn = findViewById(R.id.seeAllReviewsBtn);


        buyNowBtn = findViewById(R.id.buyNowBtn);
        addToCartBtn = findViewById(R.id.addToCartBtn);

        mainLayout.setVisibility(View.INVISIBLE);
        reviewsRecyclerView.setAdapter(reviewsAdapter);

        Intent intent = getIntent();
        String itemId = intent.getStringExtra("id");

        seeAllReviewsBtn.setVisibility(View.GONE);
        seeAllReviewsBtn.setOnClickListener(v->{
            Intent reviewsIntent = new Intent(getApplicationContext(), ItemReviewsActivity.class);
            reviewsIntent.putExtra("id", itemId);
            startActivity(reviewsIntent);
        });

        buyNowBtn.setOnClickListener(v-> buyNow());

        customProgressDialog.showProgressDialog("Loading item");
        Call<SingleItemResponse> requestCall = apiService.getItemById(itemId);

        requestCall.enqueue(new Callback<SingleItemResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<SingleItemResponse> call, Response<SingleItemResponse> response) {

                customProgressDialog.hide();
                mainLayout.setVisibility(View.VISIBLE);


                // if request is successful
                if (response.isSuccessful()) {
                    SingleItemResponse itemsResponse = response.body();

                    assert itemsResponse != null;
                    item = itemsResponse.getItem();

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

                    // show reviews

                    List<ItemReview> itemReviews = itemsResponse.getItemReviews();

                    showReviews(itemReviews);
                }

                // If request failed
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
            Call<CartResponse> cartResponseCall = apiService.addToCart(cartRequest);

            cartResponseCall.enqueue(new Callback<CartResponse>() {
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

    public void showReviews(List<ItemReview> reviewList) {
        if (reviewList.size() == 0) {
            noReviewsText.setVisibility(View.VISIBLE);
        }
        else {
            List<ItemReview> reviews = new ArrayList<>();
            if (reviewList.size()>3) {
                reviews.add(reviewList.get(0));
                reviews.add(reviewList.get(1));
                reviews.add(reviewList.get(2));
                seeAllReviewsBtn.setVisibility(View.VISIBLE);
            }
            else {
                reviews = reviewList;
            }
            reviewsAdapter.updateData(reviews);
        }
    }

    public void buyNow() {
            // Create a custom alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            View dialogView = getLayoutInflater().inflate(R.layout.custom_item_order_alert, null);
            builder.setView(dialogView);

            ImageView itemImage = dialogView.findViewById(R.id.itemImage);
            TextView itemName = dialogView.findViewById(R.id.itemName);
            TextView orderQuantity = dialogView.findViewById(R.id.itemCount);
            ImageView addBtn = dialogView.findViewById(R.id.addBtn);
            ImageView subtractBtn = dialogView.findViewById(R.id.subtractBtn);

            // Set item details
            try {
                Picasso.get().load(item.getImage()).into(itemImage);
            }
            catch (Exception e) {}
            itemName.setText(item.getName());

            orderQuantity.setText("1");

            addBtn.setOnClickListener(v-> {
                int count = Integer.parseInt(orderQuantity.getText().toString());
                count += 1;
                orderQuantity.setText(String.valueOf(count));
                if (count == 1) {
                    subtractBtn.setImageResource(R.drawable.ic_remove_inactive);
                }
                else {
                    subtractBtn.setImageResource(R.drawable.ic_remove);

                }
            });

            subtractBtn.setOnClickListener(v-> {
                int count = Integer.parseInt(orderQuantity.getText().toString());

                if (count == 1) return;

                count -= 1;
                orderQuantity.setText(String.valueOf(count));

                if (count == 1) {
                    subtractBtn.setImageResource(R.drawable.ic_remove_inactive);
                }
                else {
                    subtractBtn.setImageResource(R.drawable.ic_remove);

                }
            });


            builder.setPositiveButton("Place Order", (dialog, which) -> {
                int quantity = Integer.parseInt(orderQuantity.getText().toString());
                QuickOrderRequest quickOrderRequest = new QuickOrderRequest(item.getId(), quantity);


                    customProgressDialog.showProgressDialog("Placing order...");
                    Call<SingleOrderResponse> checkoutCall = apiService.buyNow(quickOrderRequest);

                    checkoutCall.enqueue(new Callback<SingleOrderResponse>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(Call<SingleOrderResponse> call, Response<SingleOrderResponse> response) {

                            customProgressDialog.hide();

                            // If request is successful
                            if (response.isSuccessful()) {
                                SingleOrderResponse orderResponse = response.body();
                                Order order = orderResponse.getOrder();

                                Intent intent = new Intent(getApplicationContext(), ViewOrderActivity.class);
                                intent.putExtra("id", order.getId());
                                startActivity(intent);
                            }

                            // If request failed
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
                        public void onFailure(Call<SingleOrderResponse> call, Throwable t) {

                            customProgressDialog.hide();

                            // Parsing error
                            ApiException apiException = ApiErrorUtils.parseError(t);

                            // Getting error message
                            String errorMessage = apiException.getErrorMessage();

                            // Show error message
                            CustomAlert.showCustomDialog(ViewItemActivity.this, R.drawable.em_sad, errorMessage);

                        }
                    });
            });

            // Define negative button action (e.g., to cancel)
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });

            // Show the dialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

    }
}