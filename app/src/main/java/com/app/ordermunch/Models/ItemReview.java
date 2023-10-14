package com.app.ordermunch.Models;

import com.google.gson.annotations.SerializedName;

public class ItemReview {

    @SerializedName("_id")
    private String id;
    private String stars;
    private String review;

    private String item;

    private Reviewer posted_by;

    private String date;

    public String getId() {
        return id;
    }

    public String getStars() {
        return stars;
    }

    public String getReview() {
        return review;
    }

    public Reviewer getPosted_by() {
        return posted_by;
    }

    public String getDate() {
        return date;
    }

    public String getItem() {
        return item;
    }
}
