package com.streamliners.myecom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.streamliners.models.Cart;
import com.streamliners.models.Product;
import com.streamliners.models.ProductType;
import com.streamliners.myecom.databinding.ItemVbProductBinding;
import com.streamliners.myecom.databinding.ItemWbProductBinding;
import com.streamliners.myecom.viewholders.VBProductViewHolder;
import com.streamliners.myecom.viewholders.WBProductViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Product> products;
    private ProductBinder productBinder;

    public ProductsAdapter(Context context, List<Product> products, Cart cart, AdapterCallbacksListener listener){
        this.context = context;
        this.products = products;
        productBinder = new ProductBinder(context, cart, listener);
    }

    @Override
    public int getItemViewType(int position) {
        return products.get(position).type;
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
        Product product = products.get(position);

        if(holder instanceof WBProductViewHolder){
            productBinder.bindWBP(((WBProductViewHolder) holder).b, product);
        } else {
            productBinder.bindVBP(((VBProductViewHolder) holder).b, product);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
