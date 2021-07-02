package com.streamliners.admin_app.controllers.viewholders;

import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.streamliners.admin_app.ProductsActivity;
import com.streamliners.admin_app.R;
import com.streamliners.admin_app.databinding.ItemVbProductBinding;
import com.streamliners.admin_app.databinding.ItemWbProductBinding;

import org.jetbrains.annotations.NotNull;

public class WBProductViewHolder extends RecyclerView.ViewHolder {

    public ItemWbProductBinding b;
    public WBProductViewHolder(@NonNull @NotNull ItemWbProductBinding b) {
        super(b.getRoot());
        this.b = b;
    }


}
