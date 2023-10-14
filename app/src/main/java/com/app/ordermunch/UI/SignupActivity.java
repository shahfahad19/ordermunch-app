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
import com.app.ordermunch.API.Models.Auth.SignupRequest;
import com.app.ordermunch.API.Models.Auth.SignupResponse;
import com.app.ordermunch.R;
import com.app.ordermunch.Utils.CustomAlert;
import com.app.ordermunch.Utils.CustomProgressDialog;
import com.orhanobut.hawk.Hawk;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    // Class for API Service
    private ApiService apiService;

    // Edit Texts for email and password
    EditText nameEditText, emailEditText, passwordEditText, passwordConfirmEditText;

    // Signup Btn
    Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Creating API Service object
        apiService = ApiClient.getClient().create(ApiService.class);

        // Creating objects for editTexts and button
        nameEditText = findViewById(R.id.editTextName);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        passwordConfirmEditText = findViewById(R.id.editTextConfirmPassword);
        signupBtn = findViewById(R.id.buttonSignup);

        // When signup button is clicked
        signupBtn.setOnClickListener(v -> {

            // Getting text from fields
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String passwordConfirm = passwordEditText.getText().toString().trim();

            // Validating
            // Showing error if a field is empty
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show();
            }

            // If both the fields are filled, send signup request
            else {
                CustomProgressDialog progressDialog = new CustomProgressDialog(this);
                progressDialog.showProgressDialog("Please wait..."); // Set a custom message
                // Creating signupRequest object
                SignupRequest signupRequest = new SignupRequest(name, email, password, passwordConfirm);

                // Calling API
                Call<SignupResponse> signupCall = apiService.signup(signupRequest);

                signupCall.enqueue(new Callback<SignupResponse>() {
                    @Override
                    public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                        progressDialog.hide();

                        // If signup is successful
                        if (response.isSuccessful()) {

                            Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                            // Open Main Activity
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }

                        // If signup failed
                        else {

                            // Parsing Error
                            ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                            // Getting error message
                            String errorMessage = apiException.getErrorMessage();

                            // Showing message in toast
                            CustomAlert.showCustomDialog(SignupActivity.this, R.drawable.em_sad, errorMessage);
                        }
                    }


                    // If request failed due to a network error
                    @Override
                    public void onFailure(Call<SignupResponse> call, Throwable t) {

                        // Parsing error
                        ApiException apiException = ApiErrorUtils.parseError(t);

                        // Getting error message
                        String errorMessage = apiException.getErrorMessage();

                        // Show error message
                        CustomAlert.showCustomDialog(SignupActivity.this, R.drawable.em_sad, errorMessage);
                    }
                });
            }
        });

    }
}