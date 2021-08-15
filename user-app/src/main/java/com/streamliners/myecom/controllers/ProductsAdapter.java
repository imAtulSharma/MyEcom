package com.streamliners.myecom.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.streamliners.models.models.Cart;
import com.streamliners.models.models.Product;
import com.streamliners.models.models.ProductType;
import com.streamliners.myecom.controllers.binders.ProductBinder;
import com.streamliners.myecom.databinding.ItemVbProductBinding;
import com.streamliners.myecom.databinding.ItemWbProductBinding;
import com.streamliners.myecom.controllers.viewholders.VBProductViewHolder;
import com.streamliners.myecom.controllers.viewholders.WBProductViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Product> products;
    // For the visible items
    public final List<Product> visibleProducts;
    private final ProductBinder productBinder;
    private final AdapterCallbacksListener mListener;

    public ProductsAdapter(Context context, List<Product> products, Cart cart, AdapterCallbacksListener listener){
        this.context = context;
        this.products = products;
        this.visibleProducts  = new ArrayList<>(products);
        this.mListener = listener;
        this.productBinder = new ProductBinder(context, cart, listener);
    }

    @Override
    public int getItemViewType(int position) {
        return visibleProducts.get(position).type;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(viewType == ProductType.TYPE_WEIGHT_BASED_PRODUCT){
            ItemWbProductBinding binding = ItemWbProductBinding.inflate(
                    LayoutInflater.from(context)
                    , parent
                    , false
            );
            return new WBProductViewHolder(binding);
        } else {
            ItemVbProductBinding binding = ItemVbProductBinding.inflate(
                    LayoutInflater.from(context)
                    , parent
                    , false
            );
            return new VBProductViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        Product product = visibleProducts.get(position);

        if(holder instanceof WBProductViewHolder){
            productBinder.bindWBP(((WBProductViewHolder) holder).b, product);
        } else {
            productBinder.bindVBP(((VBProductViewHolder) holder).b, product);
        }
    }

    @Override
    public int getItemCount() {
        mListener.onSizeChanges(visibleProducts.size());
        return visibleProducts.size();
    }

    /**
     * To filter the visible list
     * @param query query for the search
     */
    public void filter(String query) {
        // Clear the list
        visibleProducts.clear();

        // Check for query given
        if (query.trim().isEmpty()) {
            // Add all the products of the main list into visible list
            visibleProducts.addAll(products);
        } else {
            // Filter according to the query
            for (Product product :
                    products) {
                if (product.name.toLowerCase().contains(query.toLowerCase())) {
                    visibleProducts.add(product);
                }
            }
        }

        // Refreshing the list
        notifyDataSetChanged();
    }
}
