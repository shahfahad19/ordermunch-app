package com.app.ordermunch.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;


import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Cart.CartResponse;
import com.app.ordermunch.Models.CartItem;
import com.app.ordermunch.R;
import com.app.ordermunch.UI.Fragments.CartFragment;
import com.app.ordermunch.UI.Fragments.DashboardFragment;
import com.app.ordermunch.UI.Fragments.OrdersFragment;
import com.app.ordermunch.UI.Fragments.ProfileFragment;
import com.app.ordermunch.Utils.CustomAlert;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ApiService apiService;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        apiService = ApiClient.getClient().create(ApiService.class);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the initial fragment
        loadFragment(new DashboardFragment());

        // Set listener for bottom navigation view
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                loadFragment(new DashboardFragment());
                return true;
            } else if (item.getItemId() == R.id.menu_orders) {
                loadFragment(new OrdersFragment());
                return true;
            } else if (item.getItemId() == R.id.menu_cart) {
                loadFragment(new CartFragment());
                return true;
            } else if (item.getItemId() == R.id.menu_profile) {
                loadFragment(new ProfileFragment());
                return true;
            } else {
                return false;
            }
        });



    }

    public void updateCartNumber() {
        Call<CartResponse> requestCall = apiService.getCart();

        requestCall.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {

                // if request is successful
                if (response.isSuccessful()) {
                    CartResponse cartResponse = response.body();
                    BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.menu_cart);
                    badge.setNumber(cartResponse.getCart().size());
                }

                // if request failed
                else {

                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(MainActivity.this, R.drawable.em_sad, errorMessage);

                }
            }


            // If request failed due to a network error
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                // Parsing error
                ApiException apiException = ApiErrorUtils.parseError(t);

                // Getting error message
                String errorMessage = apiException.getErrorMessage();

                // Show error message
                CustomAlert.showCustomDialog(MainActivity.this, R.drawable.em_sad, errorMessage);

            }
        });
    }

    // Load fragment into the container
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void onResume() {
        super.onResume();
        updateCartNumber();
    }
}

