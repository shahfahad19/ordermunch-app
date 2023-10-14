package com.app.ordermunch.API.Models.Profile;

public class UpdateProfileRequest {
    private final String name;
    private final String email;
    private final String address;

    private final String contact;

    public UpdateProfileRequest(String name, String email, String address, String contact) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.contact = contact;
    }
}
