package com.streamliners.admin_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.streamliners.admin_app.controllers.OrdersAdapter;
import com.streamliners.admin_app.databinding.ActivityOrdersBinding;
import com.streamliners.admin_app.firebasehelpers.Order;
import com.streamliners.admin_app.firebasehelpers.OrdersHelper;

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

                    orders.add(index, order);
                    adapter.notifyItemChanged(index);
                    return;
                }

                orderIdIndexMap.put(orderId, orders.size());
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
            public void onOrderStateChanges(int position) {
                Order order = orders.get(position);
            }
        });
        mainBinding.list.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.list.setAdapter(adapter);
    }
}