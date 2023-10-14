package com.app.ordermunch.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Item {

    @SerializedName("_id")
    private String id;
    private String name;
    private String image;
    private String description;
    private String category;
    private Restaurant restaurant;
    private double price;

    private int item_count;

    private double rating;

    @SerializedName("rated_by")
    private int ratedBy;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public double getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public int getRatedBy() {
        return ratedBy;
    }

}
