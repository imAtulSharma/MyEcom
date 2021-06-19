package com.streamliners.myecom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.streamliners.models.Cart;
import com.streamliners.models.Product;
import com.streamliners.models.Variant;
import com.streamliners.myecom.databinding.ActivityMainBinding;
import com.streamliners.myecom.databinding.ItemVbProductBinding;
import com.streamliners.myecom.databinding.ItemWbProductBinding;
import com.streamliners.myecom.tmp.ProductsHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreferences mPrefs;
    ActivityMainBinding mainBinding;
    private ProductsAdapter adapter;
    private Cart cart = new Cart();
    List<Product> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        mPrefs = getPreferences(MODE_PRIVATE);

        if (mPrefs.contains(Constants.cart)) getDataFromSharedPrefs();

        products = new ProductsHelper().getProducts();

        if (cart.isEmpty()) mainBinding.cartSummary.setVisibility(View.GONE);
        else mainBinding.cartSummary.setVisibility(View.VISIBLE);

        setupAdapter();
    }

    private void setupAdapter() {
        AdapterCallbacksListener listener = new AdapterCallbacksListener() {
            @Override
            public void onCartUpdated() {
                updateCartSummary();
            }
        };

        adapter = new ProductsAdapter(this
                , products
                , cart
                , listener);

        mainBinding.list.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.list.setAdapter(adapter);
    }

    public void updateCartSummary(){
        if (cart.isEmpty()) mainBinding.cartSummary.setVisibility(View.GONE);
        else mainBinding.cartSummary.setVisibility(View.VISIBLE);
        mainBinding.tvTotalAmount.setText("₹" + cart.totalAmount);
        mainBinding.tvTotalItems.setText(cart.numberOfItems + " items");
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cart);
        prefsEditor.putString(Constants.cart, json);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public void getDataFromSharedPrefs(){
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.cart, "");
        cart = gson.fromJson(json, Cart.class);
        updateCartSummary();
    }
}