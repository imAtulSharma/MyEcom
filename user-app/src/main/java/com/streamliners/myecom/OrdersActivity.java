package com.streamliners.myecom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        setTitle("Orders");
    }
}