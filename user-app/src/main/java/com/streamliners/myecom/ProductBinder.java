
package com.streamliners.myecom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.streamliners.models.Cart;
import com.streamliners.models.Product;
import com.streamliners.myecom.databinding.ChipVariantBinding;
import com.streamliners.myecom.databinding.DialogVariantsQtyPickerBinding;
import com.streamliners.myecom.databinding.DialogWeightPickerBinding;
import com.streamliners.myecom.databinding.ItemVariantBinding;
import com.streamliners.myecom.databinding.ItemVbProductBinding;
import com.streamliners.myecom.databinding.ItemWbProductBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProductBinder {
    Context context;
    LayoutInflater inflater;
    AdapterCallbacksListener listener;
    Cart cart;
    private AlertDialog dialog;
    DialogWeightPickerBinding weightPickerBinding;
    DialogVariantsQtyPickerBinding variantsQtyPickerBinding;
    public int currentPos = 0;

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

        //Set Title, Subtitle, Quantity and Image in the binding
        wbProductBinding.titleProduct.setText(product.name);
        String price = String.valueOf(product.pricePerKg).replaceFirst("\\.0+$", "");
        wbProductBinding.subtitleProduct.setText("₹ " + price + "/kg");

        // Update Binding accordingly if product is present in cart
        if (cart.cartItems.containsKey(product.name)){
            wbProductBinding.grpNonZeroQuantity.setVisibility(View.VISIBLE);
            wbProductBinding.grpZeroQuantity.setVisibility(View.INVISIBLE);
            wbProductBinding.quantity.setText(cart.cartItems.get(product.name).qty + "");
        }
        //Load Image
        Glide.with(context)
                .asBitmap()
                .load(product.imageURL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
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
                setupWBProductDialog(product, wbProductBinding);

                assert weightPickerBinding != null;
                if(weightPickerBinding.getRoot().getParent()!=null)
                    ((ViewGroup) weightPickerBinding.getRoot().getParent()).removeView(weightPickerBinding.getRoot());
                dialog = new MaterialAlertDialogBuilder(context, R.style.CustomDialogTheme)
                        .setView(weightPickerBinding.getRoot())
                        .show();
            }
        });


        //Setup edit button
        wbProductBinding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupWBProductDialog(product, wbProductBinding);
                if(weightPickerBinding.getRoot().getParent()!=null)
                    ((ViewGroup) weightPickerBinding.getRoot().getParent()).removeView(weightPickerBinding.getRoot());

                dialog = new MaterialAlertDialogBuilder(context, R.style.CustomDialogTheme)
                        .setView(weightPickerBinding.getRoot())
                        .show();
            }
        });
        currentPos++;
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
        int quantity = 0;
        for (int i = 0; i<product.variants.size(); i++){
            if (cart.cartItems.containsKey(product.name + " " + product.variants.get(i).name)){
                quantity += cart.cartItems.get(product.name + " " + product.variants.get(i).name).qty;
            }
        }

        if (quantity>0){
            vbProductBinding.grpNonZeroQuantity.setVisibility(View.VISIBLE);
            vbProductBinding.quantity.setText(quantity + "");
        }

        //Set image of the product
        Glide.with(context)
                .asBitmap()
                .load(product.imageURL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        vbProductBinding.imgProduct.setImageBitmap(resource);
                        vbProductBinding.imgLoader.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                    }
                });

        //Inflate variant chips in the chip group
        for (int i = 0; i < product.variants.size(); i++){
            ChipVariantBinding b = ChipVariantBinding.inflate(inflater);
            b.getRoot().setClickable(false);
            String price = String.valueOf(product.variants.get(i).price).replaceFirst("\\.0+$", "");
            b.getRoot().setText(product.variants.get(i).name + " - ₹" + price);
            vbProductBinding.variants.addView(b.getRoot());
        }



        //Set on click listener for buttons of VB product binding

        // Show/Hide Variants
        vbProductBinding.btnVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vbProductBinding.variants.getVisibility() == View.GONE) {
                    vbProductBinding.variants.setVisibility(View.VISIBLE);
                    vbProductBinding.btnVariants.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                } else {
                    vbProductBinding.variants.setVisibility(View.GONE);
                    vbProductBinding.btnVariants.setImageResource(R.drawable.ic_drop_down);
                }
            }
        });


        //Show variants quantity picker dialog
        vbProductBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupVBProductDialog(product, vbProductBinding);
                if(variantsQtyPickerBinding.getRoot().getParent()!=null)
                    ((ViewGroup) variantsQtyPickerBinding.getRoot().getParent()).removeView(variantsQtyPickerBinding.getRoot());
                dialog = new MaterialAlertDialogBuilder(context, R.style.CustomDialogTheme)
                        .setView(variantsQtyPickerBinding.getRoot())
                        .show();
            }
        });

        //Show variants quantity picker dialog
        vbProductBinding.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupVBProductDialog(product, vbProductBinding);
                if(variantsQtyPickerBinding.getRoot().getParent()!=null)
                    ((ViewGroup) variantsQtyPickerBinding.getRoot().getParent()).removeView(variantsQtyPickerBinding.getRoot());
                dialog = new MaterialAlertDialogBuilder(context,R.style.CustomDialogTheme)
                        .setView(variantsQtyPickerBinding.getRoot())
                        .show();
            }
        });
        currentPos++;
    }

    /**
     * Setup WB Product Dialog box
     * @param product
     * @param wbProductBinding
     */
    public void setupWBProductDialog(Product product, ItemWbProductBinding wbProductBinding) {
        //Implementation of dialog box
        weightPickerBinding = DialogWeightPickerBinding.inflate(inflater);
        weightPickerBinding.productName.setText(product.name);

        //Set Number Picker for KG
        NumberPicker picker1 = weightPickerBinding.numberPicker;
        picker1.setMaxValue(9);
        picker1.setMinValue(0);
        String[] pickerVals1 = new String[11];
        pickerVals1[0] = "0kg";
        for (int i = 2; i < 12; i++) {
            pickerVals1[i - 1] = i-1 + "kg";
        }
        picker1.setDisplayedValues(pickerVals1);

        //Set Number Picker for grams
        NumberPicker picker2 = weightPickerBinding.numberPicker2;
        picker2.setMaxValue(19);
        picker2.setMinValue(0);
        String[] pickerVals2 = new String[20];
        pickerVals2[0] = "0g";
        int qty = 50;
        int counter = 1;
        while (qty <= 950) {
            pickerVals2[counter] = qty + "g";
            qty += 50;
            counter++;
        }
        picker2.setDisplayedValues(pickerVals2);

        //Restore Selections
        if (cart.cartItems.containsKey(product.name)){
            int kg = (int) cart.cartItems.get(product.name).qty;
            String gm = String.valueOf(cart.cartItems.get(product.name).qty);
            gm = gm.substring(gm.indexOf( "." )).replace(".","");
            int g = Integer.parseInt(gm);
            int indexG;
            if (gm.length() == 2) indexG = (g*10)/50;
            else indexG = (g*100)/50;


            picker1.setValue(kg);
            picker2.setValue(indexG);
        }

        //Set onClickListeners on save button of dialog box
        int k = currentPos;
        weightPickerBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get qty selected in number picker
                String kg = pickerVals1[picker1.getValue()];
                String gm = pickerVals2[picker2.getValue()];
                kg = kg.replace("kg", "");
                gm = gm.replace("g", "");

                //Add given qty to cart and update the binding respectively
                float qty = Float.parseFloat(kg) + Float.parseFloat(gm) / 1000;
                String minMessage = "Minimum " + product.minQty + " kg needs to be selected";
                if (qty< product.minQty) {
                    Toast.makeText(context, minMessage, Toast.LENGTH_SHORT).show();
                    return;
                }
                cart.add(product, qty);
                //Update data in wbProduct binding
                wbProductBinding.grpNonZeroQuantity.setVisibility(View.VISIBLE);
                wbProductBinding.quantity.setText(qty + "");
                wbProductBinding.grpZeroQuantity.setVisibility(View.INVISIBLE);

                //Callback to update cart
                listener.onCartUpdated(k);
                dialog.dismiss();
            }
        });

        weightPickerBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove product from cart
                if (cart.cartItems.containsKey(product.name)) cart.remove(product);
                else {
                    dialog.dismiss();
                    return;
                }
                //Update data in wbProduct binding
                wbProductBinding.grpZeroQuantity.setVisibility(View.VISIBLE);
                wbProductBinding.grpNonZeroQuantity.setVisibility(View.GONE);

                //Callback to update cart
                listener.onCartUpdated(k);
                dialog.dismiss();
            }
        });
    }

    /**
     * Setup for VB Product Dialog box
     * @param product
     * @param vbProductBinding
     */
    public void setupVBProductDialog(Product product, ItemVbProductBinding vbProductBinding){
        //Set up variants quantity picker dialog
        variantsQtyPickerBinding = DialogVariantsQtyPickerBinding.inflate(inflater);
        variantsQtyPickerBinding.productName.setText(product.name);

        List<ItemVariantBinding> itemVariantBindings = new ArrayList<>();

        //Add variant items in the dialog
        for (int i = 0; i<product.variants.size(); i++){
            ItemVariantBinding ivb = ItemVariantBinding.inflate(inflater);
            String price = String.valueOf(product.variants.get(i).price).replaceFirst("\\.0+$", "");
            ivb.variantName.setText("₹"+ price + " - " + product.variants.get(i).name);

            //Restore Selections
            if (cart.cartItems.containsKey(product.name + " " + product.variants.get(i).name)){
                String qty = String.valueOf(cart.cartItems.get(product.name + " " + product.variants.get(i).name).qty).replaceFirst("\\.0+$", "");
                ivb.qtyCurrent.setText(qty);
                ivb.btnDec.setVisibility(View.VISIBLE);
                ivb.qtyCurrent.setVisibility(View.VISIBLE);
            }
            itemVariantBindings.add(ivb);

            //Setup Increment button
            ivb.btnInc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currQty = Integer.parseInt(ivb.qtyCurrent.getText().toString());
                    if (currQty == 0) {
                        ivb.btnDec.setVisibility(View.VISIBLE);
                        ivb.qtyCurrent.setVisibility(View.VISIBLE);
                    }
                    ivb.qtyCurrent.setText(++currQty + "");
                }
            });

            //Setup decrement button
            ivb.btnDec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currQty = Integer.parseInt(ivb.qtyCurrent.getText().toString())-1;
                    if (currQty == 0) {
                        ivb.btnDec.setVisibility(View.INVISIBLE);
                        ivb.qtyCurrent.setVisibility(View.INVISIBLE);
                    }
                    ivb.qtyCurrent.setText(currQty + "");
                }
            });
            variantsQtyPickerBinding.variantsHolder.addView(ivb.getRoot());
        }

        //Set on click listener for save and remove all button of dialog box

        int k = currentPos;
        variantsQtyPickerBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = 0;
                //Remove all variants first to overcome duplicacy
                cart.removeAllVariantsOfVariantBasedProduct(product);

                //Add product variants to cart according to selected qty
                for (int i = 0;i<product.variants.size(); i++){
                    ItemVariantBinding ivb = itemVariantBindings.get(i);
                    qty += Integer.parseInt(ivb.qtyCurrent.getText().toString());

                    for (int j = 0; j < Integer.parseInt(ivb.qtyCurrent.getText().toString()); j++){
                        cart.add(product, product.variants.get(i));
                    }
                }

                //Update data in vbProduct binding
                if (qty == 0)
                    vbProductBinding.grpNonZeroQuantity.setVisibility(View.GONE);
                else vbProductBinding.grpNonZeroQuantity.setVisibility(View.VISIBLE);

                vbProductBinding.quantity.setText(qty + "");

                //Callback to update cart
                listener.onCartUpdated(k);
                dialog.dismiss();
            }
        });

        variantsQtyPickerBinding.btnRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove all variants from cart
                cart.removeAllVariantsOfVariantBasedProduct(product);

                // Update ItemVariantBinding and set 0 value for each
                for (int i = 0; i<product.variants.size(); i++){
                    ItemVariantBinding ivb = itemVariantBindings.get(i);
                    ivb.qtyCurrent.setText("0");
                    ivb.btnDec.setVisibility(View.INVISIBLE);
                    ivb.qtyCurrent.setVisibility(View.INVISIBLE);
                }

                //Update data in vbProduct binding
                vbProductBinding.quantity.setText("0");
                vbProductBinding.grpNonZeroQuantity.setVisibility(View.GONE);

                //Callback to update cart
                listener.onCartUpdated(k);
                dialog.dismiss();
            }
        });
    }
}
