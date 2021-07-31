package com.streamliners.myecom.controllers.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.streamliners.myecom.databinding.ItemOrderBinding;

import org.jetbrains.annotations.NotNull;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public ItemOrderBinding b;
    public OrderViewHolder(@NonNull @NotNull ItemOrderBinding b) {
        super(b.getRoot());
        this.b = b;
    }
}
