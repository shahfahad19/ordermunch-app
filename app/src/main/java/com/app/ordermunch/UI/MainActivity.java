package com.app.ordermunch.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;


import com.app.ordermunch.R;
import com.app.ordermunch.UI.Fragments.CartFragment;
import com.app.ordermunch.UI.Fragments.DashboardFragment;
import com.app.ordermunch.UI.Fragments.OrdersFragment;
import com.app.ordermunch.UI.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the initial fragment
        loadFragment(new DashboardFragment());

        // Set listener for bottom navigation view
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                loadFragment(new DashboardFragment());
                return true;
            } else if (item.getItemId() == R.id.menu_orders) {
                loadFragment(new OrdersFragment());
                return true;
            } else if (item.getItemId() == R.id.menu_cart) {
                loadFragment(new CartFragment());
                return true;
            } else if (item.getItemId() == R.id.menu_profile) {
                loadFragment(new ProfileFragment());
                return true;
            } else {
                return false;
            }
        });
    }

    // Load fragment into the container
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}

