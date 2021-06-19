package com.streamliners.myecom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.streamliners.models.Cart;
import com.streamliners.models.CartItem;
import com.streamliners.myecom.databinding.ActivityCheckoutBinding;
import com.streamliners.myecom.databinding.ItemCartSummaryBinding;

public class CheckoutActivity extends AppCompatActivity {
    public static final String KEY_CART = "Cart";
    private ActivityCheckoutBinding mainBinding;
    private LayoutInflater inflater;
    private Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();

        mainBinding = ActivityCheckoutBinding.inflate(inflater);
        setContentView(mainBinding.getRoot());

        cart = (Cart) getIntent().getExtras().getSerializable(KEY_CART);

        setupSummary();
        mainBinding.btnEditAddress.setOnClickListener(view -> fetchAddress());
        mainBinding.btnPlaceOrder.setOnClickListener(view -> placeOrder());
    }

    private void placeOrder() {

    }

    private void fetchAddress() {

    }

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

        mainBinding.titleTotalItems.setText( cart.numberOfItems + " Items");
        mainBinding.tvTotalAmount.setText("₹ " + cart.totalAmount);
    }
}