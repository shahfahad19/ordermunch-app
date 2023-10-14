package com.app.ordermunch.API;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import retrofit2.HttpException;
import retrofit2.Response;

public class ApiErrorUtils {
    public static ApiException parseError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            Response<?> response = httpException.response();
            if (response != null) {
                if (response.errorBody() != null) {
                    try {
                        // Trying to parse the error response body as JSON
                        String errorBody = response.errorBody().string();
                        Log.e("E", errorBody);


                        // Parsing the JSON to extract the error message
                        JSONObject jsonObject = new JSONObject(errorBody);
                        String errorMessage = jsonObject.optString("message", "Something went wrong");
                        return new ApiException(response.code(), errorMessage);
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }
        }

        throwable.printStackTrace();
        // If parsing the error response fails or it's not an HttpException, create a generic error
        return new ApiException(-1, "Something went wrong");
    }
}
