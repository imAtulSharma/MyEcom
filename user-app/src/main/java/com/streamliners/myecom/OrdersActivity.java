package com.streamliners.myecom;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.streamliners.models.listeners.OnCompleteListener;
import com.streamliners.myecom.controllers.OrdersAdapter;
import com.streamliners.myecom.databinding.ActivityOrdersBinding;
import com.streamliners.myecom.databinding.DialogCompleteCheckoutBinding;
import com.streamliners.myecom.messaging.FCMSender;
import com.streamliners.myecom.messaging.MessageBuilder;
import com.streamliners.myecom.messaging.RemoteConfigHelper;
import com.streamliners.myecom.tmp.FirebaseHelper;
import com.streamliners.myecom.tmp.Order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    private ActivityOrdersBinding mainBinding;
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();

    private List<Order> orders = new ArrayList<>();
    OrdersAdapter adapter;
    private HashMap<String, Integer> orderIdIndexMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        setTitle("Orders");

        setupRecyclerView();
        fetchOrders();
    }

    /**
     * fetching order from the firebase
     */
    private void fetchOrders() {

        firebaseHelper.getOrders(orders, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),new FirebaseHelper.OnOrderQueryListener() {
            @Override
            public void onCompleted() {
                mainBinding.progressBar.setVisibility(View.GONE);
                if (orders.isEmpty()) mainBinding.tvNoOrders.setVisibility(View.VISIBLE);
            }

            @Override
            public void orderFetched(String orderId, Order order) {
                if (orderIdIndexMap.containsKey(orderId)) {
                    int index = orderIdIndexMap.get(orderId);

                    orders.remove(index);
                    adapter.notifyItemRemoved(index);

                    order.orderId = orderId;
                    orders.add(index, order);
                    adapter.notifyItemChanged(index);
                    return;
                }

                orderIdIndexMap.put(orderId, orders.size());
                order.orderId = orderId;
                orders.add(order);
                adapter.notifyItemInserted(orders.size() - 1);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OrdersActivity.this, error, Toast.LENGTH_SHORT).show();
                Log.e("IndexError", error);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new OrdersAdapter(this, orders, new OrdersAdapter.AdapterCallbacksListener() {
            @Override
            public int onSizeChanges(int size) {
                if (size == 0) {
                    if (mainBinding.progressBar.getVisibility() == View.GONE)
                        mainBinding.tvNoOrders.setVisibility(View.VISIBLE);
                }
                else mainBinding.tvNoOrders.setVisibility(View.INVISIBLE);
                return size;
            }

            @Override
            public void onOrderCancelled(int position) {
                if (position == -1) return;

                Order order = orders.get(position);
                firebaseHelper.cancelOrder(order.orderId, new FirebaseHelper.OnOrderCancelListener() {
                    @Override
                    public void onSuccessfulCancelled() {
                        sendNotification(order.userName, order.noOfItems, (int) order.subTotal);
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });
        mainBinding.list.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.list.setAdapter(adapter);
    }

    /**
     * Starts the notification process to send it
     */
    private void sendNotification(String userName, int noOfItems, int total) {
        Toast.makeText(this, "Begins", Toast.LENGTH_SHORT).show();

        // Getting the authentication key first
        RemoteConfigHelper.getAuthenticationKey(OrdersActivity.this, new RemoteConfigHelper.OnRemoteConfigFetchedListener() {
            @Override
            public void onSuccessfullyFetched(String key) {
                // Creating the message
                String message = MessageBuilder.buildNewOrderMessage(MessageBuilder.ORDER_CANCEL_FORMAT, userName, noOfItems, total);

                // Sending the message and on complete displaying the appropriate dialogs
                new FCMSender().send(message, key, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                });
            }

            @Override
            public void onErrorOccurred(String error) {

            }
        });
    }
}