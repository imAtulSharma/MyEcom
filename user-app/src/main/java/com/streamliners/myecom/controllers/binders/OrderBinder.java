package com.streamliners.myecom.controllers.binders;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.firebase.Timestamp;
import com.streamliners.models.models.CartItem;
import com.streamliners.myecom.OrdersActivity;
import com.streamliners.myecom.R;
import com.streamliners.myecom.databinding.ItemCartSummaryBinding;
import com.streamliners.myecom.databinding.ItemOrderBinding;
import com.streamliners.myecom.tmp.Order;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represent binder for the product
 */
public class OrderBinder {
    /**
     * Context of the main activity
     */
    private final Context mContext;

    /**
     * Inflate to inflate the objects
     */
    private final LayoutInflater inflater;

    /**
     * To initialize the object
     * @param context context of the main activity
     */
    public OrderBinder(Context context) {
        this.mContext = context;
        this.inflater = ((OrdersActivity) context).getLayoutInflater();
    }

    // Binding methods

    /**
     * To bind weight based order
     * @param binding binding for the order in the recycler view
     * @param order order to be bind
     */
    public void bind(ItemOrderBinding binding, Order order, OnOrderStatusChangeListener listener) {
        binding.btnCancel.setVisibility(View.GONE);

        // Binding the order status
        switch (order.status) {
            case Order.OrderStatus.ACCEPTED:
                binding.tvStatus.setText(R.string.accepted_state);
                binding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.accepted_status));
                binding.constraintLayout1.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.accepted_status)));
                break;
            case Order.OrderStatus.DECLINED:
                binding.tvStatus.setText(R.string.declined_state);
                binding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.declined_status));
                binding.constraintLayout1.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.declined_status)));
                break;
            case Order.OrderStatus.DELIVERED:
                binding.tvStatus.setText(R.string.delivered_state);
                binding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.delivered_status));
                binding.constraintLayout1.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.delivered_status)));
                break;
            case Order.OrderStatus.DISPATCHED:
                binding.tvStatus.setText(R.string.dispatched_state);
                binding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.dispatched_status));
                binding.constraintLayout1.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.dispatched_status)));
                break;
            case Order.OrderStatus.CANCELLED:
                binding.tvStatus.setText(R.string.cancelled_state);
                binding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.cancelled_state));
                binding.constraintLayout1.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.cancelled_state)));
                break;
            case Order.OrderStatus.WAITING:
                binding.tvStatus.setText(R.string.waiting_state);
                binding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.waiting_state));
                binding.constraintLayout1.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.waiting_state)));
                binding.btnCancel.setVisibility(View.VISIBLE);
                break;
        }

        binding.tvOrderDateAndTime.setText(getDateMonthTime(order.createdTime));
        binding.titleTotalItems.setText(order.noOfItems + " items");
        binding.tvSubTotal.setText(String.format("₹ %.2f", order.subTotal));

        // Removing all previous view and adding the new ones
        binding.listItems.removeAllViews();

        for (String key : order.cartItems.keySet()) {
            // Get item from the hash map
            CartItem item = order.cartItems.get(key);
            ItemCartSummaryBinding itemBinding = ItemCartSummaryBinding.inflate(inflater);

            // Binding the data
            itemBinding.titleTotal.setText(item.name);

            String[] name = item.name.split(" ");

            if (name.length == 1) {
                itemBinding.tvItemDetails.setText(String.format("%.2fkg X ₹ %.2f / kg", item.qty, item.unitPrice));
            } else {
                itemBinding.tvItemDetails.setText(String.format("%.0f X ₹ %.0f", item.qty, item.unitPrice));
            }
            itemBinding.tvTotalAmount.setText("₹ " + item.cost());

            // Adding view to the list
            binding.listItems.addView(itemBinding.getRoot());
        }

        binding.btnCancel.setOnClickListener(view -> {
            binding.btnCancel.setVisibility(View.GONE);
            listener.onOrderCancelled(order);
        });
    }

    /**
     * return timestamp in the readable format
     * @param timestamp timestamp to be convrted
     * @return converted string
     */
    public static String getDateMonthTime(Timestamp timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM hh:mm aa");
        return simpleDateFormat.format(new Date(timestamp.toDate().getTime()));
    }

    public interface OnOrderStatusChangeListener {
        void onOrderCancelled(Order order);
    }
}