package com.app.ordermunch.UI.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Order.OrdersResponse;
import com.app.ordermunch.Adapters.OrderAdapter;
import com.app.ordermunch.Models.Order;
import com.app.ordermunch.R;
import com.app.ordermunch.UI.ViewOrderActivity;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class OrdersFragment extends Fragment implements OrderAdapter.OrderClickListener {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    ApiService apiService;
    CustomProgressDialog customProgressDialog;

    LinearLayout notFound;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        apiService = ApiClient.getClient().create(ApiService.class);
        customProgressDialog = new CustomProgressDialog(getContext());
        orderAdapter = new OrderAdapter(getContext(), this);


        recyclerView = view.findViewById(R.id.recyclerView);
        notFound = view.findViewById(R.id.noFoundView);


        recyclerView.setAdapter(orderAdapter);

        getCart();

        return view;
    }

    private void getCart() {
        customProgressDialog.showProgressDialog("Loading orders");
        Call<OrdersResponse> ordersCall = apiService.getOrders(999);

        ordersCall.enqueue(new Callback<OrdersResponse>() {
            @Override
            public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {

                customProgressDialog.hide();

                // If request is successful
                if (response.isSuccessful()) {
                    OrdersResponse orderResponse = response.body();
                    showCart(orderResponse.getOrders());


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
            public void onFailure(Call<OrdersResponse> call, Throwable t) {

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

    public void showCart(List<Order> orderList) {
        if (orderList.isEmpty()) {
            notFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            if (recyclerView.getVisibility() == View.INVISIBLE) {
                recyclerView.setVisibility(View.VISIBLE);

            }
            orderAdapter.updateData(orderList);
        }
    }


    @Override
    public void onOrderClickListener(String id) {
        Intent intent = new Intent(getContext(), ViewOrderActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}