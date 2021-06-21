package com.streamliners.myecom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    public static final String KEY_LOGIN = "Is logged in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("MainActivity", MODE_PRIVATE);
        int isLoggedIn = preferences.getInt(KEY_LOGIN, 0);

        if (isLoggedIn == 1) startActivity(new Intent(this, MainActivity.class));
        else startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}