package com.streamliners.myecom.viewholders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.streamliners.myecom.databinding.ItemVbProductBinding;
import com.streamliners.myecom.databinding.ItemWbProductBinding;

import org.jetbrains.annotations.NotNull;

public class VBProductViewHolder extends RecyclerView.ViewHolder {
    public ItemVbProductBinding b;

    public VBProductViewHolder(@NonNull ItemVbProductBinding b) {
        super(b.getRoot());
        this.b = b;
    }
}
