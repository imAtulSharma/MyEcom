package com.streamliners.myecom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.streamliners.myecom.databinding.ActivityMainBinding;
import com.streamliners.myecom.databinding.ItemVbProductBinding;
import com.streamliners.myecom.databinding.ItemWbProductBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        for (int i = 1; i <= 10; i++) {
                ItemWbProductBinding wbCardBinding = ItemWbProductBinding.inflate(getLayoutInflater());

                ItemVbProductBinding vbCardBinding = ItemVbProductBinding.inflate(getLayoutInflater());
                vbCardBinding.btnVariants.setOnClickListener(view -> {
                    if (vbCardBinding.btnVariants.getRotation() == 180) {
                        vbCardBinding.btnVariants.setRotation(0);
                        vbCardBinding.variants.setVisibility(View.GONE);
                        vbCardBinding.cl.setPadding(0, 0, 0, 56);
                        return;
                    }
                    vbCardBinding.btnVariants.setRotation(180);
                    vbCardBinding.variants.setVisibility(View.VISIBLE);
                    vbCardBinding.cl.setPadding(0, 0, 0, 8);
                });

                mainBinding.list.addView(vbCardBinding.getRoot());
                mainBinding.list.addView(wbCardBinding.getRoot());
        }
    }
}