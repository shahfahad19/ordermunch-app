package com.app.ordermunch.API.Models.Auth;

import com.app.ordermunch.Models.Profile;

public class LoginResponse {
    private String token;
    private Profile user;

    public String getToken() {
        return token;
    }

    public Profile getProfile() {
        return user;
    }
}
