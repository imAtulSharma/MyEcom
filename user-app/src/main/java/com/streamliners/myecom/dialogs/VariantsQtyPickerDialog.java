package com.streamliners.myecom.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.streamliners.models.Cart;
import com.streamliners.models.Product;
import com.streamliners.myecom.MainActivity;
import com.streamliners.myecom.R;
import com.streamliners.myecom.databinding.DialogVariantsQtyPickerBinding;
import com.streamliners.myecom.databinding.ItemVariantBinding;

import java.util.ArrayList;
import java.util.List;

public class VariantsQtyPickerDialog {
    private final LayoutInflater inflater;
    private Context context;
    private Cart cart;

    public VariantsQtyPickerDialog(Context context, Cart cart){
        this.context = context;
        this.cart = cart;
        inflater = ((MainActivity) context).getLayoutInflater();
    }

    public void show(Product product, VariantsQtyPickerCompleteListener listener){
        DialogVariantsQtyPickerBinding variantsQtyPickerBinding = DialogVariantsQtyPickerBinding.inflate(inflater);
        variantsQtyPickerBinding.productName.setText(product.name);

        //Create dialog
        AlertDialog dialog = new MaterialAlertDialogBuilder(context, R.style.CustomDialogTheme)
                .setView(variantsQtyPickerBinding.getRoot())
                .show();

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
                    //Add variant to cart
                    for (int j = 0; j < Integer.parseInt(ivb.qtyCurrent.getText().toString()); j++){
                        cart.add(product, product.variants.get(i));
                    }
                }

                listener.onCompleted();
                dialog.dismiss();
            }
        });

        variantsQtyPickerBinding.btnRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove all variants from cart
                cart.removeAllVariantsOfVariantBasedProduct(product);

                //Update Data in IVBinding
                for (int i = 0; i<product.variants.size(); i++){
                    ItemVariantBinding ivb = itemVariantBindings.get(i);
                    ivb.qtyCurrent.setText("0");
                    ivb.btnDec.setVisibility(View.INVISIBLE);
                    ivb.qtyCurrent.setVisibility(View.INVISIBLE);
                }
                listener.onCompleted();
                dialog.dismiss();
            }
        });
    }

    public interface VariantsQtyPickerCompleteListener {
        void onCompleted();
    }
}
