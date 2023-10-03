package com.app.ordermunch.API.Models.Restaurant;

import com.app.ordermunch.Models.Restaurant;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RestaurantsResponse {
    private String status;
    private int pages;
    private int count;
    private int results;
    private List<Restaurant> restaurants;

    public String getStatus() {
        return status;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public int getPages() {
        return pages;
    }

    public int getCount() {
        return count;
    }



    public int getResults() {
        return results;
    }

}
