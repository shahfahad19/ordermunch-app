package com.app.ordermunch.API.Models.Cart;

import com.app.ordermunch.Models.CartItem;
import com.app.ordermunch.Models.Item;

import java.util.List;

public class CartResponse {
    private List<CartItem> cart;
    private double amount;

    public List<CartItem> getCart() {
        return cart;
    }

    public double getAmount() {
        return amount;
    }
}

