package com.app.ordermunch.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Order.OrderRequest;
import com.app.ordermunch.API.Models.Order.SingleOrderResponse;
import com.app.ordermunch.API.Models.Review.AddReviewRequest;
import com.app.ordermunch.API.Models.Review.UpdateReviewRequest;
import com.app.ordermunch.Adapters.OrderItemsAdapter;
import com.app.ordermunch.Models.ItemReview;
import com.app.ordermunch.Models.Order;
import com.app.ordermunch.R;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;


public class ViewOrderActivity extends AppCompatActivity implements OrderItemsAdapter.ItemClickListener {



    ApiService apiService;
    CustomProgressDialog customProgressDialog;

    Context context;

    ScrollView mainLayout;

    TextView orderId, orderStatus, orderDate, orderAmount;

    RecyclerView itemsRecyclerView;

    OrderItemsAdapter orderItemsAdapter;

    String order = "";

    Button cancelBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        context = this;

        apiService = ApiClient.getClient().create(ApiService.class);
        customProgressDialog = new CustomProgressDialog(this);
        orderItemsAdapter = new OrderItemsAdapter(this, this);

        mainLayout = findViewById(R.id.mainLayout);
        orderId = findViewById(R.id.orderId);
        orderDate = findViewById(R.id.orderDate);
        orderStatus = findViewById(R.id.orderStatus);
        orderAmount = findViewById(R.id.orderAmount);
        itemsRecyclerView = findViewById(R.id.recyclerView);
        cancelBtn = findViewById(R.id.cancelOrderBtn);

        cancelBtn.setVisibility(View.GONE);


        itemsRecyclerView.setAdapter(orderItemsAdapter);

        cancelBtn.setOnClickListener(v-> cancelOrder());



        Intent intent = getIntent();
        order = intent.getStringExtra("id");

        mainLayout.setVisibility(View.INVISIBLE);

        customProgressDialog.showProgressDialog("Loading order");
        getOrder();

    }

    public void getOrder() {
        Call<SingleOrderResponse> orderCall = apiService.getOrderById(order);

        orderCall.enqueue(new Callback<SingleOrderResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<SingleOrderResponse> call, Response<SingleOrderResponse> response) {

                customProgressDialog.hide();
                mainLayout.setVisibility(View.VISIBLE);


                // If request is successful
                if (response.isSuccessful()) {
                    SingleOrderResponse orderResponse = response.body();
                    Order order = orderResponse.getOrder();
                    List<ItemReview> itemReviews = orderResponse.getReviews();


                    orderId.setText("Order# "+order.getId());
                    orderStatus.setText(order.getStatus());
                    orderAmount.setText("MYR. "+order.getAmount());
                    // Defining input and output date formats
                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a dd MMM, yyyy", Locale.ENGLISH);

                    // Setting the input date's time zone to UTC
                    isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    String formattedDate = "";
                    try {
                        Date date = isoFormat.parse(order.getDate());
                        formattedDate = outputFormat.format(date);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    if (order.getStatus().equals("Pending")) {
                        cancelBtn.setVisibility(View.VISIBLE);
                    }

                    orderDate.setText("Placed on: "+ formattedDate);

                    orderItemsAdapter.updateData(order.getItems(), itemReviews, order.getStatus());



                }

                // If request failed
                else {
                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(ViewOrderActivity.this, R.drawable.em_sad, errorMessage);

                }
            }


            // If request failed due to a network error
            @Override
            public void onFailure(Call<SingleOrderResponse> call, Throwable t) {

                customProgressDialog.hide();
                Toast.makeText(ViewOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                // Parsing error
                ApiException apiException = ApiErrorUtils.parseError(t);

                // Getting error message
                String errorMessage = apiException.getErrorMessage();

                // Show error message
                CustomAlert.showCustomDialog(ViewOrderActivity.this, R.drawable.em_sad, errorMessage);

            }
        });
    }

    @Override
    public void onItemClickListener(String id) {
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void addReviewClickListener(String itemId) {

        // In your activity or fragment

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Write a Review");

        View dialogLayout = getLayoutInflater().inflate(R.layout.custom_review_alert, null);
        builder.setView(dialogLayout);

        final RatingBar ratingBar = dialogLayout.findViewById(R.id.ratingBar);
        final EditText reviewEditText = dialogLayout.findViewById(R.id.reviewEditText);

        builder.setPositiveButton("Submit", (dialog, which) -> {

            customProgressDialog.showProgressDialog("Submitting review");
            // Get the rating and review text
            float rating = ratingBar.getRating();
            String review = reviewEditText.getText().toString();

            AddReviewRequest addReviewRequest = new AddReviewRequest(itemId, order, rating, review);


            Call<Void> addReviewCall = apiService.postReview(addReviewRequest);


            addReviewCall.enqueue(new Callback<Void>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    customProgressDialog.hide();


                    // If request is successful
                    if (response.isSuccessful()) {
                        getOrder();
                        CustomAlert.showCustomDialog(ViewOrderActivity.this, R.drawable.em_happy, "Review submitted");

                    }

                    // If request failed
                    else {
                        // Parsing Error
                        ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                        // Getting error message
                        String errorMessage = apiException.getErrorMessage();

                        // Showing message in toast
                        CustomAlert.showCustomDialog(ViewOrderActivity.this, R.drawable.em_sad, errorMessage);

                    }
                }


                // If request failed due to a network error
                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    customProgressDialog.hide();
                    Toast.makeText(ViewOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    // Parsing error
                    ApiException apiException = ApiErrorUtils.parseError(t);

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Show error message
                    CustomAlert.showCustomDialog(ViewOrderActivity.this, R.drawable.em_sad, errorMessage);

                }
            });

            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Dismiss the dialog
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void updateReviewClickListener(ItemReview itemReview) {
        // In your activity or fragment

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Review");

        View dialogLayout = getLayoutInflater().inflate(R.layout.custom_review_alert, null);
        builder.setView(dialogLayout);

        final RatingBar ratingBar = dialogLayout.findViewById(R.id.ratingBar);
        final EditText reviewEditText = dialogLayout.findViewById(R.id.reviewEditText);

        ratingBar.setRating(Float.parseFloat(itemReview.getStars()));
        reviewEditText.setText(itemReview.getReview());

        builder.setPositiveButton("Submit", (dialog, which) -> {
            customProgressDialog.showProgressDialog("Updating review");
            // Get the rating and review text
            float rating = ratingBar.getRating();
            String review = reviewEditText.getText().toString();

            UpdateReviewRequest updateReviewRequest = new UpdateReviewRequest(rating, review);


            Call<Void> updateReview = apiService.updateReview(itemReview.getId(), updateReviewRequest);


            updateReview.enqueue(new Callback<Void>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    customProgressDialog.hide();


                    // If request is successful
                    if (response.isSuccessful()) {
                        getOrder();
                        CustomAlert.showCustomDialog(ViewOrderActivity.this, R.drawable.em_happy, "Review updated");

                    }

                    // If request failed
                    else {
                        // Parsing Error
                        ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                        // Getting error message
                        String errorMessage = apiException.getErrorMessage();

                        // Showing message in toast
                        CustomAlert.showCustomDialog(ViewOrderActivity.this, R.drawable.em_sad, errorMessage);

                    }
                }


                // If request failed due to a network error
                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    customProgressDialog.hide();
                    Toast.makeText(ViewOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    // Parsing error
                    ApiException apiException = ApiErrorUtils.parseError(t);

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Show error message
                    CustomAlert.showCustomDialog(ViewOrderActivity.this, R.drawable.em_sad, errorMessage);

                }
            });
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Dismiss the dialog
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void cancelOrder() {

        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    customProgressDialog.showProgressDialog("Cancelling");

                    OrderRequest orderRequest = new OrderRequest("Cancelled");

                    Call<SingleOrderResponse> orderCall = apiService.updateOrder(order, orderRequest);

                    orderCall.enqueue(new Callback<SingleOrderResponse>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(Call<SingleOrderResponse> call, Response<SingleOrderResponse> response) {

                            customProgressDialog.hide();
                            mainLayout.setVisibility(View.VISIBLE);


                            // If request is successful
                            if (response.isSuccessful()) {
                                SingleOrderResponse orderResponse = response.body();
                                Order order = orderResponse.getOrder();

                                orderId.setText("Order# "+order.getId());
                                orderStatus.setText(order.getStatus());
                                orderAmount.setText("MYR. "+order.getAmount());
                                // Defining input and output date formats
                                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH);

                                // Setting the input date's time zone to UTC
                                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                                cancelBtn.setVisibility(View.GONE);

                                String formattedDate = "";
                                try {
                                    Date date = isoFormat.parse(order.getDate());
                                    formattedDate = outputFormat.format(date);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                orderDate.setText("Placed on: "+ formattedDate);

                                orderItemsAdapter.updateData(order.getItems(), orderResponse.getReviews(), order.getStatus());

                                CustomAlert.showCustomDialog(ViewOrderActivity.this, R.drawable.em_happy, "Cancelled");


                            }

                            // If request failed
                            else {
                                // Parsing Error
                                ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                                // Getting error message
                                String errorMessage = apiException.getErrorMessage();

                                // Showing message in toast
                                CustomAlert.showCustomDialog(ViewOrderActivity.this, R.drawable.em_sad, errorMessage);

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
                            CustomAlert.showCustomDialog(ViewOrderActivity.this, R.drawable.em_sad, errorMessage);

                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // User canceled the logout, do nothing
                })
                .show();

    }
}