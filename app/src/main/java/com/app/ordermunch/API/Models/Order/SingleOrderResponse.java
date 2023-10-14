package com.app.ordermunch.API.Models.Order;

import com.app.ordermunch.Models.ItemReview;
import com.app.ordermunch.Models.Order;

import java.util.List;

public class SingleOrderResponse {

    private Order order;

    List<ItemReview> reviews;

    public Order getOrder() {
        return order;
    }

    public List<ItemReview> getReviews() {
        return reviews;
    }
}
