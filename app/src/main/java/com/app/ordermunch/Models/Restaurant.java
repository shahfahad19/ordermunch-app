package com.app.ordermunch.Models;

import com.google.gson.annotations.SerializedName;

public class Restaurant
{
    @SerializedName("id")
    private String id;
    private String name;
    private String image;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }


}
