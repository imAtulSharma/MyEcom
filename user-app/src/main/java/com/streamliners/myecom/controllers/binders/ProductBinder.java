package com.streamliners.myecom.controllers.binders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.streamliners.models.models.Cart;
import com.streamliners.models.models.Product;
import com.streamliners.myecom.MainActivity;
import com.streamliners.myecom.R;
import com.streamliners.myecom.controllers.AdapterCallbacksListener;
import com.streamliners.myecom.databinding.ChipVariantBinding;
import com.streamliners.myecom.databinding.ItemVbProductBinding;
import com.streamliners.myecom.databinding.ItemWbProductBinding;
import com.streamliners.myecom.dialogs.VariantsQtyPickerDialog;
import com.streamliners.myecom.dialogs.WeightPickerDialog;

import org.jetbrains.annotations.NotNull;

public class ProductBinder {
    Context context;
    LayoutInflater inflater;
    AdapterCallbacksListener listener;
    Cart cart;

    /**
     * Parameterised Constructor for ProductBinder
     * @param context
     * @param cart
     * @param listener
     */
    public ProductBinder(Context context, Cart cart, AdapterCallbacksListener listener){
        this.context = context;
        this.cart = cart;
        this.listener = listener;
        inflater  = ((MainActivity) context).getLayoutInflater();
    }

    /**
     * Binds WB Product Item
     * @param wbProductBinding
     * @param product
     */
    public void bindWBP(ItemWbProductBinding wbProductBinding, Product product) {

        //Set Title in binding
        wbProductBinding.titleProduct.setText(product.name);

        //Set Price in binding
        String price = String.valueOf(product.pricePerKg).replaceFirst("\\.0+$", "");
        wbProductBinding.subtitleProduct.setText("₹ " + price + "/kg");

        // Update Binding accordingly if product is present in cart
        updateWBProductBinding(wbProductBinding, product);

        //Load Image
        Glide.with(context)
                .asBitmap()
                .load(product.imageURL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        //Hide Loader and Show Image
                        wbProductBinding.imgProduct.setImageBitmap(resource);
                        wbProductBinding.imgLoader.setVisibility(View.GONE);
                        wbProductBinding.imgProduct.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                    }
                });


        // Set OnClickListener on add and edit button of WB Product binding

        //Setup Add Button
        wbProductBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new WeightPickerDialog(context, cart).show(product, new WeightPickerDialog.WeightPickerCompleteListener() {
                    @Override
                    public void onCompleted() {
                        updateWBProductBinding(wbProductBinding, product);
//                      Callback to update cart
                        listener.onCartUpdated();
                    }
                });
            }
        });


        //Setup edit button
        wbProductBinding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new WeightPickerDialog(context, cart).show(product, new WeightPickerDialog.WeightPickerCompleteListener() {
                    @Override
                    public void onCompleted() {
                        updateWBProductBinding(wbProductBinding, product);
                    }
                });
            }
        });
    }

    /**
     * Binds VB Product Item
     * @param vbProductBinding
     * @param product
     */
    @SuppressLint("SetTextI18n")
    public void bindVBP (ItemVbProductBinding vbProductBinding, Product product){

        // Set Title, Subtitle, Quantity and Image in the binding

        //Set title and subtitle
        vbProductBinding.titleProduct.setText(product.name);
        vbProductBinding.subtitleProduct.setText(product.variants.size() + " Variants");

        // Update Binding accordingly if product is present in cart
        updateVBProductBinding(vbProductBinding, product);

        //Set image of the product
        Glide.with(context)
                .asBitmap()
                .load(product.imageURL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        vbProductBinding.imgProduct.setImageBitmap(resource);
                        vbProductBinding.imgLoader.setVisibility(View.GONE);
                        vbProductBinding.imgProduct.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                    }
                });


        //Set on click listener for buttons of VB product binding

        // Show/Hide Variants
        vbProductBinding.btnVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inflate variant chips in the chip group

                if (vbProductBinding.variants.getVisibility() == View.GONE) {
                    //Show Variants

                    for (int i = 0; i < product.variants.size(); i++){
                        ChipVariantBinding b = ChipVariantBinding.inflate(inflater);
                        b.getRoot().setClickable(false);
                        String price = String.valueOf(product.variants.get(i).price).replaceFirst("\\.0+$", "");
                        b.getRoot().setText(product.variants.get(i).name + " - ₹" + price);
                        vbProductBinding.variants.addView(b.getRoot());
                    }
                    vbProductBinding.cl.setPadding(0,0,0,16);
                    vbProductBinding.variants.setVisibility(View.VISIBLE);
                    vbProductBinding.btnVariants.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                } else {
                    vbProductBinding.cl.setPadding(0,0,0,(int) (24*context.getResources().getDisplayMetrics().density + 0.5f));

                    //Hide Variants
                    vbProductBinding.variants.setVisibility(View.GONE);
                    vbProductBinding.btnVariants.setImageResource(R.drawable.ic_drop_down);
                    vbProductBinding.variants.removeAllViews();
                }
            }
        });


        //Show variants quantity picker dialog
        vbProductBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new VariantsQtyPickerDialog(context, cart)
                        .show(product, new VariantsQtyPickerDialog.VariantsQtyPickerCompleteListener() {
                            @Override
                            public void onCompleted() {
                                updateVBProductBinding(vbProductBinding, product);
                            }
                        });
            }
        });

        //Show variants quantity picker dialog
        vbProductBinding.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VariantsQtyPickerDialog(context, cart)
                        .show(product, new VariantsQtyPickerDialog.VariantsQtyPickerCompleteListener() {
                            @Override
                            public void onCompleted() {
                                updateVBProductBinding(vbProductBinding, product);
                            }
                        });
            }
        });
    }

    /**
     * Update data in WBProduct binding according to cart
     * @param wbProductBinding
     * @param product
     */
    private void  updateWBProductBinding(ItemWbProductBinding wbProductBinding, Product product){
        if (!cart.cartItems.containsKey(product.name)) {
            wbProductBinding.grpZeroQuantity.setVisibility(View.VISIBLE);
            wbProductBinding.grpNonZeroQuantity.setVisibility(View.GONE);
            wbProductBinding.quantity.setText("0");
        }
        else {
            wbProductBinding.grpNonZeroQuantity.setVisibility(View.VISIBLE);
            wbProductBinding.quantity.setText(cart.cartItems.get(product.name).qty + "");
            wbProductBinding.grpZeroQuantity.setVisibility(View.INVISIBLE);
        }
        listener.onCartUpdated();
    }

    /**
     * Update data in VBProduct binding according to cart
     * @param vbProductBinding
     * @param product
     */
    private void updateVBProductBinding(ItemVbProductBinding vbProductBinding, Product product){
        int qty = 0;

        //Close Variant Chips
        vbProductBinding.cl.setPadding(0,0,0,(int) (24*context.getResources().getDisplayMetrics().density + 0.5f));
        vbProductBinding.variants.setVisibility(View.GONE);
        vbProductBinding.btnVariants.setImageResource(R.drawable.ic_drop_down);
        vbProductBinding.variants.removeAllViews();

        //Get Total Quantity of variants added to cart
        for (int i = 0;i<product.variants.size(); i++){
            if (cart.cartItems.containsKey(product.name + " "+ product.variants.get(i).name))
                qty += cart.cartItems.get(product.name + " " + product.variants.get(i).name).qty;
        }

        //Update data in vbProduct binding
        if (qty == 0) {
            vbProductBinding.grpNonZeroQuantity.setVisibility(View.GONE);
            vbProductBinding.quantity.setText("0");
        }
        else {
            vbProductBinding.grpNonZeroQuantity.setVisibility(View.VISIBLE);
            vbProductBinding.quantity.setText(qty + "");
        }

        //Callback to update cart
        listener.onCartUpdated();
    }



}
