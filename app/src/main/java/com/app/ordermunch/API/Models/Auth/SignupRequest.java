package com.app.ordermunch.API.Models.Auth;

public class SignupRequest {

    private final String name;
    private final String email;
    private final String password;

    private final String passwordConfirm;

    public SignupRequest(String name, String email, String password, String passwordConfirm) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
