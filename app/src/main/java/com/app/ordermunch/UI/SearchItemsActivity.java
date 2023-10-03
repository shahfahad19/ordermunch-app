package com.app.ordermunch.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.app.ordermunch.R;

public class SearchItemsActivity extends AppCompatActivity {

    EditText searchBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_items);

        searchBox = findViewById(R.id.itemSearchBox);


        // Request focus for the EditText
        searchBox.requestFocus();




    }
}