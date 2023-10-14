package com.app.ordermunch.API.Models.Item;

import com.app.ordermunch.Models.Item;
import com.app.ordermunch.Models.ItemReview;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemResponse {
    private List<Item> items;


    public List<Item> getItemList() {
        return items;
    }


}
