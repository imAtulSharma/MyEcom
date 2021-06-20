package com.streamliners.myecom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.streamliners.models.Cart;
import com.streamliners.models.CartItem;
import com.streamliners.myecom.databinding.ActivityCheckoutBinding;
import com.streamliners.myecom.databinding.ItemCartSummaryBinding;

public class CheckoutActivity extends AppCompatActivity {
    // Key for transferring the cart across activity
    public static final String KEY_CART = "Cart";
    // Main binding of the activity
    private ActivityCheckoutBinding mainBinding;
    // Inflater
    private LayoutInflater inflater;
    // Cart of items
    private Cart cart;

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

        // Adding listeners to the buttons
        mainBinding.btnEditAddress.setOnClickListener(view -> fetchAddress());
        mainBinding.btnPlaceOrder.setOnClickListener(view -> placeOrder());
    }

    /**
     * Places the order with the selected items
     */
    private void placeOrder() {

    }

    /**
     * Fetching address from the google maps
     */
    private void fetchAddress() {

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