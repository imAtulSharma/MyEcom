package com.streamliners.myecom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.streamliners.models.Cart;
import com.streamliners.models.Product;
import com.streamliners.myecom.controllers.AdapterCallbacksListener;
import com.streamliners.myecom.controllers.ProductsAdapter;
import com.streamliners.myecom.databinding.ActivityMainBinding;
import com.streamliners.myecom.tmp.ProductsHelper;

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
        mainBinding.cartSummary.setOnClickListener(view -> checkout());
        if (cart.cartItems.isEmpty()) mainBinding.cartSummary.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.my_menu, menu);

        // Get the search view
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.my_cart) {
            if (cart.cartItems.isEmpty()){
                Toast.makeText(this, "Add some items to cart first!", Toast.LENGTH_SHORT).show();
            }
            checkout();
            return true;
        } else if (item.getItemId() == R.id.my_orders) {
            myOrders();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * To display the previous orders
     */
    private void myOrders() {
        Toast.makeText(this, "Previous Orders", Toast.LENGTH_SHORT).show();
    }

    /**
     * To checkout the selected items
     */
    private void checkout() {
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(CheckoutActivity.KEY_CART, cart);

        startActivity(intent);
    }

    private void setupAdapter() {
        AdapterCallbacksListener listener = new AdapterCallbacksListener() {
            @Override
            public void onCartUpdated() {
                updateCartSummary();
            }

            @Override
            public void onSizeChanges(int size) {
                if (size == 0) {
                    mainBinding.tvNoProducts.setVisibility(View.VISIBLE);
                } else {
                    mainBinding.tvNoProducts.setVisibility(View.INVISIBLE);
                }
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
        mainBinding.titleTotalItems.setText(cart.numberOfItems + " items");
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cart);
        prefsEditor.putString(Constants.cart, json);
        prefsEditor.putInt(SplashActivity.KEY_LOGIN, 1);
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