package com.streamliners.myecom.controllers.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.streamliners.myecom.databinding.ItemVbProductBinding;

public class VBProductViewHolder extends RecyclerView.ViewHolder {
    public ItemVbProductBinding b;

    public VBProductViewHolder(@NonNull ItemVbProductBinding b) {
        super(b.getRoot());
        this.b = b;
    }
}
