package com.streamliners.myecom.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.streamliners.myecom.controllers.binders.OrderBinder;
import com.streamliners.myecom.controllers.viewholders.OrderViewHolder;
import com.streamliners.myecom.databinding.ItemOrderBinding;
import com.streamliners.myecom.tmp.Order;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<Order> orders;
    public int orderPosition;

    public OrderBinder binder;
    public AdapterCallbacksListener listener;

    public OrdersAdapter(Context context, List<Order> orders, AdapterCallbacksListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
        binder = new OrderBinder(context);
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(LayoutInflater.from(context), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RecyclerView.ViewHolder holder, int position) {
        // Getting the order from the list
        Order order = orders.get(orders.size() - 1 - position);
        // Bind the order
        binder.bind(((OrderViewHolder) holder).b, order, new OrderBinder.OnOrderStatusChangeListener() {
            @Override
            public void onOrderCancelled(Order order) {
                int index = orders.indexOf(order);
                listener.onOrderCancelled(index);
                OrdersAdapter.this.notifyItemChanged(orders.size() - 1 - index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listener.onSizeChanges(orders.size());
    }

    public interface AdapterCallbacksListener {
        int onSizeChanges(int size);
        void onOrderCancelled(int position);
    }
}


