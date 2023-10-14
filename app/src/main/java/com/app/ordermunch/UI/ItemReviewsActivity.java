package com.app.ordermunch.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Item.SingleItemResponse;
import com.app.ordermunch.Adapters.ReviewsAdapter;
import com.app.ordermunch.Models.ItemReview;
import com.app.ordermunch.R;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ItemReviewsActivity extends AppCompatActivity {

    RelativeLayout mainLayout;

    ApiService apiService;
    CustomProgressDialog customProgressDialog;

    Context context;

    RecyclerView recyclerView;

    ReviewsAdapter reviewsAdapter;

    TextView seeAllReviewsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_reviews);

        context = this;

        apiService = ApiClient.getClient().create(ApiService.class);
        customProgressDialog = new CustomProgressDialog(this);
        reviewsAdapter = new ReviewsAdapter(this);

        recyclerView = findViewById(R.id.recyclerView);
        mainLayout = findViewById(R.id.mainLayout);

        mainLayout.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(reviewsAdapter);

        Intent intent = getIntent();
        String itemId = intent.getStringExtra("id");


        customProgressDialog.showProgressDialog("Loading reviews");
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
                    CustomAlert.showCustomDialog(ItemReviewsActivity.this, R.drawable.em_sad, errorMessage);

                }
            }


            // If request failed due to a network error
            @Override
            public void onFailure(Call<SingleItemResponse> call, Throwable t) {

                customProgressDialog.hide();
                Toast.makeText(ItemReviewsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                // Parsing error
                ApiException apiException = ApiErrorUtils.parseError(t);

                // Getting error message
                String errorMessage = apiException.getErrorMessage();

                // Show error message
                CustomAlert.showCustomDialog(ItemReviewsActivity.this, R.drawable.em_sad, errorMessage);

            }
        });

    }

    public void showReviews(List<ItemReview> reviewList) {

            reviewsAdapter.updateData(reviewList );
    }
}