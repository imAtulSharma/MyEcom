package com.streamliners.admin_app.controllers.binders;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.Timestamp;
import com.streamliners.admin_app.OrdersActivity;
import com.streamliners.admin_app.R;
import com.streamliners.admin_app.databinding.DialogOrderSummaryBinding;
import com.streamliners.admin_app.databinding.ItemOrderBinding;
import com.streamliners.admin_app.databinding.OrderItemBinding;
import com.streamliners.admin_app.firebasehelpers.Order;
import com.streamliners.models.models.CartItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
     * Summary dialog binding
     */
    private DialogOrderSummaryBinding dialogBinding;
    private AlertDialog dialog;

    /**
     * To initialize the object
     * @param context context of the main activity
     */
    public OrderBinder(Context context) {
        this.mContext = context;
        this.inflater = ((OrdersActivity) context).getLayoutInflater();
        dialogBinding = DialogOrderSummaryBinding.inflate(inflater);
        dialog = new MaterialAlertDialogBuilder(mContext)
                .setView(dialogBinding.getRoot())
                .create();
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

        binding.btnDetails.setOnClickListener(view -> {
            setupSummary(order);
        });

        binding.btnLocation.setOnClickListener(view -> {
            openGoogleMap(order.userAddress, order.latitude, order.longitude);
        });

        binding.btnCall.setOnClickListener(view -> {
            openDialer(order.userPhoneNo);
        });
    }

    /**
     * Setup summary of the cart
     */
    private void setupSummary(Order order) {
        // Removing all previous view and adding the new ones
        dialogBinding.listItems.removeAllViews();

        for (String key : order.cartItems.keySet()) {
            // Get item from the hash map
            CartItem item = order.cartItems.get(key);
            OrderItemBinding binding = OrderItemBinding.inflate(inflater);

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
            dialogBinding.listItems.addView(binding.getRoot());
        }

        // Displaying the information
        dialogBinding.titleTotalItems.setText( order.noOfItems + " Items");
        dialogBinding.tvSubTotal.setText("₹ " + order.subTotal);

        dialog.show();

        dialogBinding.btnClose.setOnClickListener(view -> {
            dialog.hide();
        });
    }

    // Utility methods

    /**
     * Opens dialer with the given phone number
     * @param number number to be displayed in the dialer
     */
    public void openDialer(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        mContext.startActivity(intent);
    }

    /**
     * Opens map
     * @param latitude latitude of the location
     * @param longitude longitude of the location
     */
    public void openGoogleMap(String label, double latitude, double longitude) {
        String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + label + ")";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
        mContext.startActivity(intent);
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
}