package com.app.ordermunch.UI.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Auth.LoginRequest;
import com.app.ordermunch.API.Models.Auth.LoginResponse;
import com.app.ordermunch.API.Models.Profile.ProfileResponse;
import com.app.ordermunch.API.Models.Profile.UpdatePasswordRequest;
import com.app.ordermunch.API.Models.Profile.UpdateProfileRequest;
import com.app.ordermunch.API.Models.Restaurant.RestaurantsResponse;
import com.app.ordermunch.Models.Profile;
import com.app.ordermunch.Models.Restaurant;
import com.app.ordermunch.R;
import com.app.ordermunch.UI.LoginActivity;
import com.app.ordermunch.UI.RestaurantsActivity;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;
import com.orhanobut.hawk.Hawk;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;


public class ProfileFragment extends Fragment {
    ApiService apiService;
    CustomProgressDialog customProgressDialog;

    TextView name, phone, email, address;

    LinearLayout nameLayout, phoneLayout, emailLayout, addressLayout, passwordLayout, logoutLayout;

    String user_name, user_phone, user_email, user_address;

    Activity activity;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        activity = getActivity();

        apiService = ApiClient.getClient().create(ApiService.class);
        customProgressDialog = new CustomProgressDialog(getContext());

        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);

        nameLayout = view.findViewById(R.id.nameLayout);
        phoneLayout = view.findViewById(R.id.phoneLayout);
        emailLayout = view.findViewById(R.id.emailLayout);
        addressLayout = view.findViewById(R.id.addressLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        logoutLayout = view.findViewById(R.id.logoutLayout);

        nameLayout.setOnClickListener(v -> showUpdateDialog("Name", user_name, name));
        phoneLayout.setOnClickListener(v -> showUpdateDialog("Phone", user_phone, phone));
        emailLayout.setOnClickListener(v -> showUpdateDialog("Email", user_email, email));
        addressLayout.setOnClickListener(v -> showUpdateDialog("Address", user_address, address));
        passwordLayout.setOnClickListener(v-> showUpdatePasswordDialog());

        logoutLayout.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirm Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // User confirmed the logout

                        // Clear the token using Hawk Library
                        Hawk.put("jwtToken", "");

                        if (activity != null) {
                            activity.finish();
                        }

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // User canceled the logout, do nothing
                    })
                    .show();
        });


        getProfile();


        return view;
    }

    public void getProfile() {
        Call<ProfileResponse> profileResponseCall = apiService.getProfile();

        profileResponseCall.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {

                customProgressDialog.hide();

                // If request is successful
                if (response.isSuccessful()) {
                    ProfileResponse profileResponse = response.body();
                    Profile profile = profileResponse.getProfile();
                    user_name = profile.getName();
                    user_phone = profile.getContact();
                    user_address = profile.getAddress();
                    user_email = profile.getEmail();

                    name.setText(user_name);
                    email.setText(user_email);
                    address.setText(user_address);
                    phone.setText(user_phone);

                }

                // If request failed
                else {

                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);

                }
            }


            // If request failed due to a network error
            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {

                customProgressDialog.hide();

                // Parsing error
                ApiException apiException = ApiErrorUtils.parseError(t);

                // Getting error message
                String errorMessage = apiException.getErrorMessage();

                // Show error message
                CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);

            }
        });

    }

    private void showUpdateDialog(final String fieldName, String currentValue, TextView updatedTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update " + fieldName);

        // Create a custom layout for the dialog
        View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.custom_update_dialog, null);

        final EditText input = dialogLayout.findViewById(R.id.editText);
        final Button updateBtn = dialogLayout.findViewById(R.id.updateBtn);


        input.setText(currentValue);

        builder.setView(dialogLayout);


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();

        updateBtn.setOnClickListener(v->{
            String newValue = input.getText().toString().trim();
            if (newValue.isEmpty()) {
                input.setError("Field cannot be empty");
                input.requestFocus();
            }
            else {
                updatedTextView.setText(newValue);
                alertDialog.dismiss();
                updateProfile();
            }
        });

        // Add a custom layout for better spacing and appearance
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary)));

        alertDialog.show();
    }

    private void showUpdatePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Password");

        // Create a custom layout for the dialog
        View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.custom_update_password_layout, null);

        final EditText currentPassword = dialogLayout.findViewById(R.id.currentPassword);
        final EditText newPassword = dialogLayout.findViewById(R.id.newPassword);
        final EditText confirmPassword = dialogLayout.findViewById(R.id.confirmPassword);
        final Button updateBtn = dialogLayout.findViewById(R.id.updateBtn);


        builder.setView(dialogLayout);


        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();

        updateBtn.setOnClickListener(v->{
            String currentPass = currentPassword.getText().toString().trim();
            String newPass = newPassword.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();

            if (currentPass.isEmpty()) {
                currentPassword.setError("Field cannot be empty");
                currentPassword.requestFocus();
            }
            else if (newPass.isEmpty()) {
                newPassword.setError("Field cannot be empty");
                newPassword.requestFocus();
            }
            else if (confirmPass.isEmpty()) {
                confirmPassword.setError("Field cannot be empty");
                confirmPassword.requestFocus();
            }
            else if (!confirmPass.equals(newPass)) {
                confirmPassword.setError("Password do not match");
                confirmPassword.requestFocus();
            }
            else {
                updatePassword(currentPass, newPass, confirmPass);
                alertDialog.dismiss();
            }
        });

        // Add a custom layout for better spacing and appearance
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary)));

        alertDialog.show();
    }


    public void updateProfile() {
        customProgressDialog.showProgressDialog("Updating");

        String updatedName = name.getText().toString().trim();
        String updatedEmail = email.getText().toString().trim();
        String updatedAddress = address.getText().toString().trim();
        String updatedPhone = phone.getText().toString().trim();


        // Creating updateProfile object
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(updatedName, updatedEmail, updatedAddress, updatedPhone);

        // Calling API
        Call<ProfileResponse> profileResponseCall = apiService.updateProfile(updateProfileRequest);

        profileResponseCall.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                customProgressDialog.hide();

                // If request is successful
                if (response.isSuccessful()) {
                    ProfileResponse profileResponse = response.body();
                    Profile profile = profileResponse.getProfile();
                    user_name = profile.getName();
                    user_phone = profile.getContact();
                    user_address = profile.getAddress();
                    user_email = profile.getEmail();

                    name.setText(user_name);
                    email.setText(user_email);
                    address.setText(user_address);
                    phone.setText(user_phone);

                    CustomAlert.showCustomDialog(getContext(), R.drawable.em_happy, "Updated successfully");


                }

                // If request failed
                else {

                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);
                }
            }


            // If request failed due to a network error
            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {

                customProgressDialog.hide();
                // Parsing error
                ApiException apiException = ApiErrorUtils.parseError(t);

                // Getting error message
                String errorMessage = apiException.getErrorMessage();

                // Show error message
                CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);
            }
        });

    }

    public void updatePassword(String currentPass, String newPass, String confirmPass) {
        customProgressDialog.showProgressDialog("Updating");

        // Creating updatePassword object
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(currentPass, newPass, confirmPass);

        // Calling API (Using LoginResponse because its response will be similar to login)
        Call<LoginResponse> responseCall = apiService.updatePassword(updatePasswordRequest);

        responseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                customProgressDialog.hide();

                // If request is successful
                if (response.isSuccessful()) {
                    LoginResponse updateResponse = response.body();

                    assert updateResponse != null;
                    String token = updateResponse.getToken();

                    // Saving token using Hawk Library
                    Hawk.put("jwtToken", token);

                    Profile profile = updateResponse.getProfile();
                    user_name = profile.getName();
                    user_phone = profile.getContact();
                    user_address = profile.getAddress();
                    user_email = profile.getEmail();

                    name.setText(user_name);
                    email.setText(user_email);
                    address.setText(user_address);
                    phone.setText(user_phone);

                    CustomAlert.showCustomDialog(getContext(), R.drawable.em_happy, "Updated successfully");


                }

                // If request failed
                else {

                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    // Showing message in toast
                    CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);
                }
            }


            // If request failed due to a network error
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                customProgressDialog.hide();
                // Parsing error
                ApiException apiException = ApiErrorUtils.parseError(t);

                // Getting error message
                String errorMessage = apiException.getErrorMessage();

                // Show error message
                CustomAlert.showCustomDialog(getContext(), R.drawable.em_sad, errorMessage);
            }
        });

    }

}