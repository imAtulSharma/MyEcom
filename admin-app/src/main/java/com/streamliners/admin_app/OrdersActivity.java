package com.streamliners.admin_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.streamliners.admin_app.controllers.OrdersAdapter;
import com.streamliners.admin_app.databinding.ActivityOrdersBinding;
import com.streamliners.admin_app.firebasehelpers.Order;
import com.streamliners.admin_app.firebasehelpers.OrdersHelper;
import com.streamliners.admin_app.messaging.FCMSender;
import com.streamliners.admin_app.messaging.MessageBuilder;
import com.streamliners.admin_app.messaging.RemoteConfigHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    ActivityOrdersBinding mainBinding;
    OrdersHelper ordersHelper = new OrdersHelper();
    List<Order> orders = new ArrayList<>();
    OrdersAdapter adapter;
    final HashMap<String, Integer> orderIdIndexMap  = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        setupRecyclerView();
        fetchOrders();
    }

    /**
     * fetching order from the firebase
     */
    private void fetchOrders() {
        ordersHelper.liveOrders(new OrdersHelper.OnOrderQueryListener() {
            @Override
            public void onCompleted() {
                mainBinding.progressBar.setVisibility(View.GONE);
                if (orders.isEmpty()) mainBinding.tvNoOrders.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNewOrderReceived(String orderId, Order order) {
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
                Toast.makeText(OrdersActivity.this, "error", Toast.LENGTH_SHORT).show();
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
            public void onOrderStateChanges(int position, int state) {
                if (position == -1) return;

                Order order = orders.get(position);
                ordersHelper.changeOrderState(order.orderId, state, new OrdersHelper.OrderStateChangeListener() {
                    @Override
                    public void onSuccessfulChanged() {
                        sendNotification(order.userDeviceToken, getOrderStatus(state));
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

    private String getOrderStatus(int state) {
        switch (state) {
            case Order.OrderStatus.ACCEPTED: return getString(R.string.accepted_state);
            case Order.OrderStatus.WAITING: return getString(R.string.waiting_state);
            case Order.OrderStatus.DECLINED: return getString(R.string.declined_state);
            case Order.OrderStatus.DELIVERED: return getString(R.string.delivered_state);
            case Order.OrderStatus.DISPATCHED: return getString(R.string.dispatched_state);
            case Order.OrderStatus.CANCELLED: return getString(R.string.cancelled_state);
            default: return null;
        }
    }

    /**
     * Starts the notification process to send it
     */
    private void sendNotification(String token, String status) {
        Toast.makeText(this, "Begins", Toast.LENGTH_SHORT).show();

        // Getting the authentication key first
        RemoteConfigHelper.getAuthenticationKey(OrdersActivity.this, new RemoteConfigHelper.OnRemoteConfigFetchedListener() {
            @Override
            public void onSuccessfullyFetched(String key) {
                // Creating the message
                String message = MessageBuilder.buildNewOrderMessage(token, status);

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