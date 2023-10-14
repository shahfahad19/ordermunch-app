package com.app.ordermunch.Models;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.Date;
import java.util.List;

public class Order {

    @SerializedName("_id")
    private String id;

    private List<CartItem> items;

    private int items_count;
    private String status;

    private int amount;

    private boolean rated;

    private String date;

    public String getId() {
        return id;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public String getStatus() {
        return status;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isRated() {
        return rated;
    }

    public int getItemsCount() { return items_count; }

    public String getDate() {
        return date;
    }
}
