package com.streamliners.admin_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.messaging.FirebaseMessaging;
import com.streamliners.admin_app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        FirebaseMessaging.getInstance().subscribeToTopic("admin");
    }

    public void openProductsActivity(View view) {
        Intent intent = new Intent(this, ProductsActivity.class);
        startActivity(intent);
    }

    public void openOrdersActivity(View view) {
        Intent intent = new Intent(this, OrdersActivity.class);
        startActivity(intent);
    }
}