package com.app.ordermunch.API.Models.Auth;

public class LoginResponse {
    private String token;

    // Default constructor (required for Gson)
    public LoginResponse() {
    }

    public LoginResponse(String token, String userId, String username, String email, boolean success, String message) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
