package com.streamliners.myecom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    public static final String KEY_LOGIN = "Is logged in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}