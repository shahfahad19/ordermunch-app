package com.app.ordermunch.API.Models.Review;

public class UpdateReviewRequest  {
    private float stars;
    private String review;

    public UpdateReviewRequest(float stars, String review) {
        this.stars = stars;
        this.review = review;
    }
}
