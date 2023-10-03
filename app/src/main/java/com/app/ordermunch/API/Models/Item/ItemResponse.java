package com.app.ordermunch.API.Models.Item;

import com.app.ordermunch.Models.Item;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemResponse {
    private String status;
    private int pages;
    private int count;
    private int results;
    private List<Item> items;

    public String getStatus() {
        return status;
    }

    public List<Item> getItemList() {
        return items;
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
