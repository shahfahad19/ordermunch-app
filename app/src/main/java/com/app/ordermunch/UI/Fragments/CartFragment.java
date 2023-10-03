package com.app.ordermunch.UI.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Auth.LoginResponse;
import com.app.ordermunch.API.Models.Cart.CartRequest;
import com.app.ordermunch.API.Models.Cart.CartResponse;
import com.app.ordermunch.API.Models.Item.ItemResponse;
import com.app.ordermunch.Adapters.CartAdapter;
import com.app.ordermunch.Adapters.ItemAdapter;
import com.app.ordermunch.Models.CartItem;
import com.app.ordermunch.Models.Item;
import com.app.ordermunch.R;
import com.app.ordermunch.UI.ItemsActivity;
import com.app.ordermunch.UI.LoginActivity;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;
import com.orhanobut.hawk.Hawk;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class CartFragment extends Fragment implements CartAdapter.CartItemClickListener {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    ApiService apiService;
    CustomProgressDialog customProgressDialog;

    TextView totalAmount;

    LinearLayout notFound;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        apiService = ApiClient.getClient().create(ApiService.class);
        customProgressDialog = new CustomProgressDialog(getContext());
        cartAdapter = new CartAdapter(getContext(), this);


        recyclerView = view.findViewById(R.id.recyclerView);
        totalAmount = view.findViewById(R.id.totalCartAmount);
        notFound = view.findViewById(R.id.noFoundView);


        recyclerView.setAdapter(cartAdapter);


        getCart();

        return view;
    }

    private void getCart() {
        customProgressDialog.showProgressDialog("Loading cart");
        Call<CartResponse> itemCall = apiService.getCart();

        itemCall.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {

                customProgressDialog.hide();

                // If login is successful
                if (response.isSuccessful()) {
                    CartResponse cartResponse = response.body();
                    showCart(cartResponse.getCart(), cartResponse.getAmount());


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
            public void onFailure(Call<CartResponse> call, Throwable t) {

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

    public void showCart(List<CartItem> itemList, double amount) {
        if (itemList.isEmpty()) {
            notFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            totalAmount.setText("MYR. 0");
        }
        else {
            if (recyclerView.getVisibility() == View.INVISIBLE) {
                recyclerView.setVisibility(View.VISIBLE);

            }
            cartAdapter.updateData(itemList);
            totalAmount.setText("MYR. "+String.valueOf(amount));
        }
    }


    @Override
    public void onRemoveItemClicked(String id) {
        customProgressDialog.showProgressDialog("Please wait");

        // Calling API
        CartRequest cartRequest = new CartRequest(id);
        Call<CartResponse> loginCall = apiService.removeFromCart(cartRequest);

        loginCall.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                customProgressDialog.hide();
                // If request is successful
                if (response.isSuccessful()) {

                    CartResponse cartResponse = response.body();
                    showCart(cartResponse.getCart(), cartResponse.getAmount());

                }

                // If request failed
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
            public void onFailure(Call<CartResponse> call, Throwable t) {
                customProgressDialog.hide();

                // Getting error message
                String errorMessage = "Network error";

                // Show error message
                CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);
            }
        });
    }

    @Override
    public void onAddItemClicked(String id) {

        customProgressDialog.showProgressDialog("Please wait");

        // Calling API
        CartRequest cartRequest = new CartRequest(id);
        Call<CartResponse> loginCall = apiService.addToCart(cartRequest);

        loginCall.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                customProgressDialog.hide();
                // If request is successful
                if (response.isSuccessful()) {

                    CartResponse cartResponse = response.body();
                    showCart(cartResponse.getCart(), cartResponse.getAmount());


                }

                // If request failed
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
            public void onFailure(Call<CartResponse> call, Throwable t) {
                customProgressDialog.hide();

                // Getting error message
                String errorMessage = "Network error";

                // Show error message
                CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);
            }
        });
    }

    @Override
    public void onDeleteItemClicked(String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure?")
                .setMessage("This item will be deleted from cart?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteItem(id);
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());



        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

    }

    public void deleteItem(String id) {
        customProgressDialog.showProgressDialog("Please wait");

        // Calling API
        CartRequest cartRequest = new CartRequest(id);
        Call<CartResponse> loginCall = apiService.deleteFromCart(cartRequest);

        loginCall.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                customProgressDialog.hide();
                // If request is successful
                if (response.isSuccessful()) {

                    CartResponse cartResponse = response.body();
                    showCart(cartResponse.getCart(), cartResponse.getAmount());


                }

                // If request failed
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
            public void onFailure(Call<CartResponse> call, Throwable t) {
                customProgressDialog.hide();

                // Getting error message
                String errorMessage = "Network error";

                // Show error message
                CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);
            }
        });
    }


}