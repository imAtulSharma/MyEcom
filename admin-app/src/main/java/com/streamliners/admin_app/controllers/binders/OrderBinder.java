package com.streamliners.admin_app.controllers.binders;

import android.content.Context;
import android.view.LayoutInflater;

import com.google.firebase.Timestamp;
import com.streamliners.admin_app.OrdersActivity;
import com.streamliners.admin_app.R;
import com.streamliners.admin_app.databinding.ItemOrderBinding;
import com.streamliners.admin_app.firebasehelpers.Order;

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
    public void bind(ItemOrderBinding binding, Order order) {
        // Binding the order status
        switch (order.status) {
            case Order.OrderStatus.ACCEPTED:
                binding.tvStatus.setText(R.string.accepted_state);
                break;
            case Order.OrderStatus.DECLINED:
                binding.tvStatus.setText(R.string.declined_state);
                break;
            case Order.OrderStatus.DELIVERED:
                binding.tvStatus.setText(R.string.delivered_state);
                break;
            case Order.OrderStatus.DISPATCHED:
                binding.tvStatus.setText(R.string.dispatched_state);
                break;
            case Order.OrderStatus.CANCELLED:
                binding.tvStatus.setText(R.string.cancelled_state);
                break;
            case Order.OrderStatus.WAITING:
                binding.tvStatus.setText(R.string.waiting_state);
                break;
        }

        binding.tvOrderDateAndTime.setText(getDateMonthTime(order.createdTime));
        binding.tvUserName.setText(order.userName);
        binding.tvItems.setText(order.noOfItems + " items");
        binding.tvTotalAmount.setText(String.format("₹ %.2f", order.subTotal));
    }

    // Utility methods

    /**
     * return timestamp in the readable format
     * @param timestamp timestamp to be convrted
     * @return converted string
     */
    public static String getDateMonthTime(Timestamp timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM hh:mm aa");
        return simpleDateFormat.format(new Date(timestamp.toDate().getTime()));
    }
}