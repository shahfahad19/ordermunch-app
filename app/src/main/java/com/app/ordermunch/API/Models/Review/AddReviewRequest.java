package com.app.ordermunch.API.Models.Review;

public class AddReviewRequest {
    private String item;
    private String order;
    private float stars;
    private String review;

    public AddReviewRequest(String item, String order, float stars, String review) {
        this.item = item;
        this.order = order;
        this.stars = stars;
        this.review = review;
    }
}
