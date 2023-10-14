package com.app.ordermunch.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ordermunch.API.ApiClient;
import com.app.ordermunch.API.ApiErrorUtils;
import com.app.ordermunch.API.ApiException;
import com.app.ordermunch.API.ApiService;
import com.app.ordermunch.API.Models.Auth.LoginRequest;
import com.app.ordermunch.API.Models.Auth.LoginResponse;
import com.app.ordermunch.R;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;
import com.orhanobut.hawk.Hawk;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // Class for API Service
    private ApiService apiService;

    // Edit Texts for email and password
    EditText emailEditText, passwordEditText;

    // Login Btn
    Button loginBtn;

    TextView signupText;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = this;

        // Creating API Service object
        apiService = ApiClient.getClient().create(ApiService.class);

        // Creating objects for editTexts and button
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.buttonLogin);
        signupText = findViewById(R.id.signupText);

        signupText.setOnClickListener(v->startActivity(new Intent(this, SignupActivity.class)));

        // When login button is clicked
        loginBtn.setOnClickListener(v -> {

            // Getting text from email and password fields
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validating
            // Showing error if a field is empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show();
            }

            // If both the fields are filled, send login request
            else {
                CustomProgressDialog progressDialog = new CustomProgressDialog(this);
                progressDialog.showProgressDialog("Please wait..."); // Set a custom message
                // Creating loginRequest object
                LoginRequest loginRequest = new LoginRequest(email, password);

                // Calling API
                Call<LoginResponse> loginCall = apiService.login(loginRequest);

                loginCall.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        progressDialog.hide();

                        // if request is successful
                        if (response.isSuccessful()) {

                            // Get response
                            LoginResponse loginResponse = response.body();

                            // Get token from response
                            String token = loginResponse.getToken();

                            // Saving token using Hawk Library
                            Hawk.put("jwtToken", token);

                            // Open Main Activity
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();



                        }

                        // if request failed
                        else {

                            // Parsing Error
                            ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                            // Getting error message
                            String errorMessage = apiException.getErrorMessage();

                            // Showing message in toast
                            CustomAlert.showCustomDialog(LoginActivity.this, R.drawable.em_sad, errorMessage);
                        }
                    }


                    // If request failed due to a network error
                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {

                        // Parsing error
                        ApiException apiException = ApiErrorUtils.parseError(t);

                        // Getting error message
                        String errorMessage = apiException.getErrorMessage();

                        // Show error message
                        CustomAlert.showCustomDialog(LoginActivity.this, R.drawable.em_sad, errorMessage);
                    }
                });
            }
        });

    }
}