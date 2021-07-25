package com.streamliners.admin_app.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.streamliners.admin_app.ProductsActivity;
import com.streamliners.admin_app.R;
import com.streamliners.admin_app.controllers.viewholders.VBProductViewHolder;
import com.streamliners.admin_app.controllers.viewholders.WBProductViewHolder;
import com.streamliners.admin_app.databinding.ChipVariantBinding;
import com.streamliners.admin_app.databinding.ItemVbProductBinding;
import com.streamliners.admin_app.databinding.ItemWbProductBinding;
import com.streamliners.admin_app.firebasehelpers.ProductsHelper;
import com.streamliners.models.models.Product;
import com.streamliners.models.models.ProductType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<Product> products;
    public List<Product> visibleProducts;
    public int productPosition;
    public ArrayList<ItemWbProductBinding> wbProductBindings;
    public ArrayList<ItemVbProductBinding> vbProductBindings;
    public int mode = 0;

    public ProductsAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.visibleProducts = new ArrayList<>(products);

        wbProductBindings = new ArrayList<>();
        vbProductBindings = new ArrayList<>();
    }


    @Override
    public int getItemViewType(int position) {
        return visibleProducts.get(position).type;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        if (viewType == ProductType.TYPE_WEIGHT_BASED_PRODUCT) {
            ItemWbProductBinding binding = ItemWbProductBinding.inflate(
                    LayoutInflater.from(context)
                    , parent
                    , false
            );
            wbProductBindings.add(binding);
            return new WBProductViewHolder(binding);
        } else {
            ItemVbProductBinding binding = ItemVbProductBinding.inflate(
                    LayoutInflater.from(context)
                    , parent
                    , false
            );
            vbProductBindings.add(binding);
            return new VBProductViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RecyclerView.ViewHolder holder, int position) {
        Product product = visibleProducts.get(position);

        if (holder instanceof WBProductViewHolder) {
            //Set Product Details in Binding
            ((WBProductViewHolder) holder).b.titleProduct.setText(product.name);
            ((WBProductViewHolder) holder).b.subtitleProduct.setText("₹" + String.valueOf(product.pricePerKg).replaceFirst("\\.0+$", "") + "/kg");
            Glide.with(context)
                    .asBitmap()
                    .load(product.imageURL)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @org.jetbrains.annotations.NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                            ((WBProductViewHolder) holder).b.imgLoader.setVisibility(View.GONE);
                            ((WBProductViewHolder) holder).b.imgProduct.setVisibility(View.VISIBLE);
                            ((WBProductViewHolder) holder).b.imgProduct.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                        }
                    });
            registerForContextMenu(((WBProductViewHolder) holder).b, position);
        } else {
            //Set Product Details in Binding
            ((VBProductViewHolder) holder).b.titleProduct.setText(product.name);
            ((VBProductViewHolder) holder).b.subtitleProduct.setText(product.variants.size() + " Variants");
            Glide.with(context)
                    .asBitmap()
                    .load(product.imageURL)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @org.jetbrains.annotations.NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                            ((VBProductViewHolder) holder).b.imgLoader.setVisibility(View.GONE);
                            ((VBProductViewHolder) holder).b.imgProduct.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                        }
                    });

            //Setup Drop Down For VBP
            setupDropDownForVBP(((VBProductViewHolder) holder).b, product);
            //Register to show context menu
            registerForContextMenu(((VBProductViewHolder) holder).b, position);
        }
    }

    @Override
    public int getItemCount() {
        return visibleProducts.size();
    }

    private void setupDropDownForVBP(ItemVbProductBinding binding, Product product) {
        binding.btnVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inflate variant chips in the chip group

                if (binding.variants.getVisibility() == View.GONE) {
                    //Show Variants
                    for (int i = 0; i < product.variants.size(); i++) {
                        ChipVariantBinding b = ChipVariantBinding.inflate(LayoutInflater.from(context));
                        b.getRoot().setClickable(false);
                        String price = String.valueOf(product.variants.get(i).price).replaceFirst("\\.0+$", "");
                        b.getRoot().setText(product.variants.get(i).name + " - ₹" + price);
                        binding.variants.addView(b.getRoot());
                    }
                    binding.variants.setVisibility(View.VISIBLE);
                    binding.btnVariants.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                } else {
                    //Hide Variants
                    binding.variants.setVisibility(View.GONE);
                    binding.btnVariants.setImageResource(R.drawable.ic_drop_down);
                    binding.variants.removeAllViews();
                }
            }
        });
    }

    /**
     * To filter the visible list
     *
     * @param query query for the search
     */
    public void filter(String query) {
        // Clear the list
        visibleProducts.clear();
        // Check for query given
        if (query.trim().isEmpty()) {
            // Add all the products of the main list into visible list
            visibleProducts = new ArrayList<>(products);
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

    /**
     * Sort Items Alphabetically
     */
    public void sortAlphabetically() {
        //Sort List of Items according to alphabetical order of labels
        Collections.sort(products, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
            }
        });

        if (!visibleProducts.equals(products)) ProductsHelper.sortAlphabetically(context);
        visibleProducts = new ArrayList<>(products);
        notifyDataSetChanged();
    }

    private void registerForContextMenu(ItemVbProductBinding binding, int position) {
        binding.frameLayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Choose your option");
                MenuInflater inflater = ((ProductsActivity) context).getMenuInflater();
                productPosition = position;
                inflater.inflate(R.menu.product_menu, menu);
            }
        });
    }

    private void registerForContextMenu(ItemWbProductBinding binding, int position) {
        binding.frameLayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Choose your option");

                MenuInflater inflater = ((ProductsActivity) context).getMenuInflater();
                productPosition = position;
                inflater.inflate(R.menu.product_menu, menu);

            }
        });
    }

    /**
     * setUp listener according to mode
     */
    public void listenerSetter(ItemWbProductBinding b) {
        b.frameLayout.setOnCreateContextMenuListener(null);
    }

    /**
     * setUp listener according to mode
     */
    public void listenerSetter(ItemVbProductBinding b) {
        b.frameLayout.setOnCreateContextMenuListener(null);
    }
}
