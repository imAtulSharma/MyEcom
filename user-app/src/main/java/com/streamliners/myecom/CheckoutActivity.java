package com.streamliners.myecom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.streamliners.models.listeners.OnCompleteListener;
import com.streamliners.models.models.Cart;
import com.streamliners.models.models.CartItem;
import com.streamliners.myecom.databinding.ActivityCheckoutBinding;
import com.streamliners.myecom.databinding.DialogCompleteCheckoutBinding;
import com.streamliners.myecom.databinding.ItemCartSummaryBinding;
import com.streamliners.myecom.messaging.FCMSender;
import com.streamliners.myecom.messaging.MessageBuilder;
import com.streamliners.myecom.messaging.RemoteConfigHelper;
import com.streamliners.myecom.tmp.FirebaseHelper;
import com.streamliners.myecom.tmp.Order;

import java.io.IOException;

import static com.streamliners.myecom.MapsActivity.KEY_LATLNG;

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
    // For firebase work
    private FirebaseHelper firebaseHelper;

    // Preferences
    private SharedPreferences preferences;

    // Details of the user
    private String address = "";
    private String name = "";
    private String number = "";
    private double[] latLng = new double[2];

    public static final String KEY_ADDRESS = "Address";
    public static final String KEY_NAME = "Name";
    public static final String KEY_NUMBER = "Number";
    private static final String KEY_LNG = "Longitude";
    private static final String KEY_LAT = "Latitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();

        // Initializing the binding and set to the activity
        mainBinding = ActivityCheckoutBinding.inflate(inflater);
        setContentView(mainBinding.getRoot());

        // Getting cart from intent
        cart = (Cart) getIntent().getExtras().getSerializable(KEY_CART);

        firebaseHelper = new FirebaseHelper();

        preferences = getPreferences(MODE_PRIVATE);
        getDataFromSharedPreferences();

        // Setup summary
        setupSummary();
        setupHideError();

        // Adding listeners to the buttons
        mainBinding.btnEditAddress.setOnClickListener(view -> fetchAddress());
        mainBinding.btnPlaceOrder.setOnClickListener(view -> placeOrder());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_ADDRESS && resultCode == RESULT_OK) {
            address = data.getStringExtra(KEY_ADDRESS);
            latLng = data.getDoubleArrayExtra(KEY_LATLNG);
            mainBinding.tvAddress.setText("Address : " + address);
        }
    }

    /**
     * Places the order with the selected items
     */
    private void placeOrder() {
        name = mainBinding.tvName.getEditText().getText().toString();
        number = mainBinding.tvContactNo.getEditText().getText().toString();

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

        saveDataInSharedPreferences();

        firebaseHelper.placeOrder(new Order(cart, name, number, address, null, new LatLng(latLng[0], latLng[1])),
                new OnCompleteListener<Order>() {
                    @Override
                    public void onCompleted(Order order) {
                        sendNotification(name, order.noOfItems, (int) order.subTotal);
                    }

                    @Override
                    public void onFailed(String error) {
                        new MaterialAlertDialogBuilder(CheckoutActivity.this)
                                .setTitle("Error")
                                .setMessage(error)
                                .show();
                    }
                });
    }

    /**
     * Starts the notification process to send it
     */
    private void sendNotification(String userName, int noOfItems, int total) {
        Toast.makeText(this, "Begins", Toast.LENGTH_SHORT).show();

        // Getting the authentication key first
        RemoteConfigHelper.getAuthenticationKey(CheckoutActivity.this, new RemoteConfigHelper.OnRemoteConfigFetchedListener() {
            @Override
            public void onSuccessfullyFetched(String key) {
                // Creating the message
                String message = MessageBuilder.buildNewOrderMessage(userName, noOfItems, total);

                // Sending the message and on complete displaying the appropriate dialogs
                new FCMSender().send(message, key, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog("Failure!", "Error: "+ e.toString());
                            }
                        });
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogCompleteCheckoutBinding binding = DialogCompleteCheckoutBinding.inflate(inflater);
                                 AlertDialog dialog = new MaterialAlertDialogBuilder(CheckoutActivity.this)
                                        .setView(binding.getRoot())
                                        .setCancelable(false)
                                        .show();
                                binding.btnGoToOrders.setOnClickListener(view -> {
                                    startActivity(new Intent(CheckoutActivity.this, OrdersActivity.class));
                                    dialog.dismiss();
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onErrorOccurred(String error) {
                showDialog("Failure!", "Error: "+ error);
            }
        });
    }

    /**
     * Show the dialog
     * @param title title of the dialog
     * @param message message in the dialog
     */
    private void showDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
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

            String[] name = item.name.split(" ");

            if (name.length == 1) {
                binding.tvItemDetails.setText(String.format("%.2fkg X ₹ %.2f / kg", item.qty, item.unitPrice));
            } else {
                binding.tvItemDetails.setText(String.format("%.0f X ₹ %.0f", item.qty, item.unitPrice));
            }
            binding.tvTotalAmount.setText("₹ " + item.cost());

            // Adding view to the list
            mainBinding.listItems.addView(binding.getRoot());
        }

        // Displaying the information
        mainBinding.titleTotalItems.setText( cart.numberOfItems + " Items");
        mainBinding.tvTotalAmount.setText("₹ " + cart.totalAmount);
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

    /**
     * To save data in shared preferences
     */
    private void saveDataInSharedPreferences() {
        preferences.edit()
                .putString(KEY_ADDRESS, address)
                .putString(KEY_NAME, name)
                .putString(KEY_NUMBER, number)
                .putString(KEY_LAT, String.valueOf(latLng[0]))
                .putString(KEY_LNG, String.valueOf(latLng[1]))
                .apply();
    }

    /**
     * To get the data from shared preferences
     */
    private void getDataFromSharedPreferences() {
        name = preferences.getString(KEY_NAME, "");
        mainBinding.tvName.getEditText().setText(name);

        number = preferences.getString(KEY_NUMBER, "");
        mainBinding.tvContactNo.getEditText().setText(number);

        address = preferences.getString(KEY_ADDRESS, "");
        mainBinding.tvAddress.setText("Address : " + address);

        latLng[0] = Double.parseDouble(preferences.getString(KEY_LAT, "0"));
        latLng[1] = Double.parseDouble(preferences.getString(KEY_LNG, "0"));
    }
}