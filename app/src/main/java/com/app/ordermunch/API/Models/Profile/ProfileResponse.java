package com.app.ordermunch.API.Models.Profile;

import com.app.ordermunch.Models.Profile;
import com.google.gson.annotations.SerializedName;

public class ProfileResponse {

    private Profile user;


    public Profile getProfile() {
        return user;
    }
}
