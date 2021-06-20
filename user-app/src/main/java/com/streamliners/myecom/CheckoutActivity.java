package com.streamliners.myecom;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.streamliners.models.Cart;
import com.streamliners.models.CartItem;
import com.streamliners.myecom.databinding.ActivityCheckoutBinding;
import com.streamliners.myecom.databinding.DialogCompleteCheckoutBinding;
import com.streamliners.myecom.databinding.ItemCartSummaryBinding;

public class CheckoutActivity extends AppCompatActivity {
    // Key for transferring the cart across activity
    public static final String KEY_CART = "Cart";
    // Request Code for fetching address
    public static final int RC_ADDRESS = 1;
    // Main binding of the activity
    private ActivityCheckoutBinding mainBinding;
    // Inflater
    private LayoutInflater inflater;
    // Cart of items
    private Cart cart;

    private String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();

        // Initializing the binding and set to the activity
        mainBinding = ActivityCheckoutBinding.inflate(inflater);
        setContentView(mainBinding.getRoot());

        // Getting cart from intent
        cart = (Cart) getIntent().getExtras().getSerializable(KEY_CART);

        // Setup summary
        setupSummary();
        setupHideError();

        // Adding listeners to the buttons
        mainBinding.btnEditAddress.setOnClickListener(view -> fetchAddress());
        mainBinding.btnPlaceOrder.setOnClickListener(view -> placeOrder());
    }

    /**
     * To hide error when text changed
     */
    private void setupHideError() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mainBinding.tvName.setError(null);
                mainBinding.tvContactNo.setError(null);
            }
        };

        mainBinding.tvName.getEditText().addTextChangedListener(textWatcher);
        mainBinding.tvContactNo.getEditText().addTextChangedListener(textWatcher);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_ADDRESS && resultCode == RESULT_OK) {
            address = data.getStringExtra("ADDRESS");
            mainBinding.tvAddress.setText("Address : " + address);
        }
    }

    /**
     * Places the order with the selected items
     */
    private void placeOrder() {
        String name = mainBinding.tvName.getEditText().getText().toString();
        String number = mainBinding.tvContactNo.getEditText().getText().toString();

        // Guard code
        if (address.isEmpty()) {
            Toast.makeText(this, "Address not selected", Toast.LENGTH_SHORT).show();
            return;
        } else if (name.isEmpty()) {
            mainBinding.tvName.setError("Enter name");
            return;
        } else if (number.isEmpty()) {
            mainBinding.tvContactNo.setError("Enter number");
            return;
        } else if (number.length() < 10) {
            mainBinding.tvContactNo.setError("Enter valid number");
            return;
        }

        DialogCompleteCheckoutBinding binding = DialogCompleteCheckoutBinding.inflate(inflater);
        new MaterialAlertDialogBuilder(this)
                .setView(binding.getRoot())
                .show();
    }

    /**
     * Fetching address from the google maps
     */
    private void fetchAddress() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, RC_ADDRESS);
    }

    /**
     * Setup summary of the cart
     */
    private void setupSummary() {
        for (String key : cart.cartItems.keySet()) {
            // Get item from the hash map
            CartItem item = cart.cartItems.get(key);
            ItemCartSummaryBinding binding = ItemCartSummaryBinding.inflate(inflater);

            // Binding the data
            binding.titleTotal.setText(item.name);
            binding.tvItemDetails.setText(String.format("%.2fkg X ₹ %.2f / kg", item.qty, item.unitPrice));
            binding.tvTotalAmount.setText("₹ " + item.cost());

            // Adding view to the list
            mainBinding.listItems.addView(binding.getRoot());
        }

        // Displaying the information
        mainBinding.titleTotalItems.setText( cart.numberOfItems + " Items");
        mainBinding.tvTotalAmount.setText("₹ " + cart.totalAmount);
    }
}