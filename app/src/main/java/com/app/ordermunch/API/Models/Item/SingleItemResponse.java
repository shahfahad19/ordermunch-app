package com.app.ordermunch.API.Models.Item;

import com.app.ordermunch.Models.Item;
import com.app.ordermunch.Models.ItemReview;

import java.util.List;

public class SingleItemResponse {

    private Item item;

    private List<ItemReview> reviews;


    public Item getItem() {
        return item;
    }

    public List<ItemReview> getItemReviews() {
        return reviews;
    }
}
