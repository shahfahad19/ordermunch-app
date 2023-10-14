package com.app.ordermunch.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Item.ItemResponse;
import com.app.ordermunch.Adapters.ItemAdapter;
import com.app.ordermunch.Models.Item;
import com.app.ordermunch.R;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ItemsActivity extends AppCompatActivity implements ItemAdapter.ItemClickListener{

    ApiService apiService;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;

    EditText searchEditText;
    List<Item> itemList;

    LinearLayout notFound;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        itemList = new ArrayList<>();

        CustomProgressDialog customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.showProgressDialog("Loading Items");

        apiService = ApiClient.getClient().create(ApiService.class);

        recyclerView = findViewById(R.id.recyclerView);
        searchEditText = findViewById(R.id.itemSearchBox);
        notFound = findViewById(R.id.noFoundView);

        itemAdapter = new ItemAdapter(this, this);
        recyclerView.setAdapter(itemAdapter);

        Call<ItemResponse> requestCall = apiService.getItems(999);

        requestCall.enqueue(new Callback<ItemResponse>() {
            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {

                customProgressDialog.hide();

                // if request is successful
                if (response.isSuccessful()) {
                    ItemResponse itemsResponse = response.body();
                    itemList = itemsResponse.getItemList();
                    showItems();

                }

                // if request failed
                else {

                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(ItemsActivity.this, R.drawable.em_sad, errorMessage);

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
                CustomAlert.showCustomDialog(ItemsActivity.this, R.drawable.em_sad, errorMessage);

            }
        });

        // Search box
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the recipe list based on the search text
                String searchText = s.toString().toLowerCase().trim();

                              List<Item> filteredList = new ArrayList<>();
                for (Item item : itemList) {
                    if (item.getName().toLowerCase().contains(searchText)) {
                        filteredList.add(item);
                    }
                }

                // Check if any items found
                if (filteredList.size() == 0) {
                    notFound.setVisibility(View.VISIBLE);
                }
                else {
                    notFound.setVisibility(View.INVISIBLE);

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

    public void showItems() {
        if (itemList.isEmpty()) {

            notFound.setVisibility(View.VISIBLE);
        }
        else {
            itemAdapter.updateData(itemList);
        }

    }

    @Override
    public void onItemClickListener(String id) {
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}