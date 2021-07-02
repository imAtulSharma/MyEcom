package com.streamliners.admin_app.controllers.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.streamliners.admin_app.databinding.ItemVbProductBinding;

import org.jetbrains.annotations.NotNull;

public class VBProductViewHolder extends RecyclerView.ViewHolder {
    public ItemVbProductBinding b;

    public VBProductViewHolder(@NonNull @NotNull ItemVbProductBinding b) {
        super(b.getRoot());
        this.b = b;
    }

}
