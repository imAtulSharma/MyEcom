package com.streamliners.admin_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.streamliners.admin_app.databinding.ActivityOrdersBinding;

public class OrdersActivity extends AppCompatActivity {

    ActivityOrdersBinding ordersBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ordersBinding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(ordersBinding.getRoot());
    }
}