package com.streamliners.admin_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.streamliners.admin_app.controllers.OrdersAdapter;
import com.streamliners.admin_app.databinding.ActivityOrdersBinding;
import com.streamliners.admin_app.firebasehelpers.Order;
import com.streamliners.admin_app.firebasehelpers.OrdersHelper;
import com.streamliners.models.listeners.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    ActivityOrdersBinding mainBinding;
    OrdersHelper ordersHelper = new OrdersHelper();
    List<Order> orders = new ArrayList<>();
    OrdersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        fetchOrders();
    }

    /**
     * fetching order from the firebase
     */
    private void fetchOrders() {
        ordersHelper.liveOrders(new OrdersHelper.OnOrderQueryListener() {
            @Override
            public void onCompleted() {
                setupRecyclerView();
                mainBinding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onOrderFetched(Order order) {
                orders.add(order);
                adapter.notifyDataSetChanged();
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
                if (size == 0) mainBinding.tvNoOrders.setVisibility(View.VISIBLE);
                else mainBinding.tvNoOrders.setVisibility(View.INVISIBLE);
                return size;
            }
        });
        mainBinding.list.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.list.setAdapter(adapter);
    }
}