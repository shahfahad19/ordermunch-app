package com.app.ordermunch.API.Models.Order;

public class QuickOrderRequest {
    private String item;
    private int count;

    public QuickOrderRequest(String item, int count) {
        this.item = item;
        this.count = count;
    }
}
