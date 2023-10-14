package com.app.ordermunch.API.Models.Profile;

public class UpdatePasswordRequest {
    private final String passwordCurrent;
    private final String password;
    private final String passwordConfirm;

    public UpdatePasswordRequest(String passwordCurrent, String password, String passwordConfirm) {
        this.passwordCurrent = passwordCurrent;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
