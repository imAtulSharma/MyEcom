package com.streamliners.myecom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.streamliners.models.Cart;
import com.streamliners.models.Product;
import com.streamliners.myecom.MainActivity;
import com.streamliners.myecom.R;
import com.streamliners.myecom.databinding.DialogWeightPickerBinding;

public class WeightPickerDialog {
    private final LayoutInflater inflater;
    private Cart cart;
    private Context context;

    public WeightPickerDialog(Context context, Cart cart){
        this.context = context;
        this.cart = cart;
        inflater = ((MainActivity) context).getLayoutInflater();
    }

    public void show(Product product, WeightPickerCompleteListener listener){

        DialogWeightPickerBinding weightPickerBinding = DialogWeightPickerBinding.inflate(inflater);
        weightPickerBinding.productName.setText(product.name);

        //Create dialog
        AlertDialog dialog = new MaterialAlertDialogBuilder(context, R.style.CustomDialogTheme)
                .setView(weightPickerBinding.getRoot())
                .show();

        //Store minimum qty to make custom number picker
        float minQty = product.minQty;
        int minKg = (int) minQty;
        String minGm = String.valueOf(minQty);
        minGm = minGm.substring(minGm.indexOf( "." )).replace(".","");
        int minG;
        if (minGm.length() == 2) minG = Integer.parseInt(minGm)*10;
        else minG = Integer.parseInt(minGm)*100;

        //Set Number Picker for KG
        NumberPicker picker1 = weightPickerBinding.numberPicker;
        picker1.setMaxValue(10-minKg);
        picker1.setMinValue(0);
        String[] pickerVals1 = new String[10-minKg+1];
        if (minKg == 0) pickerVals1[0] = "0kg";
        else pickerVals1[0] = String.valueOf(minKg) + "kg";
        int minKgTemp = minKg+1;
        int i = 1;
        while (minKgTemp<11){
            pickerVals1[i] = minKgTemp + "kg";
            minKgTemp++;
            i++;
        }
        picker1.setDisplayedValues(pickerVals1);

        //Set Number Picker for grams
        NumberPicker picker2 = weightPickerBinding.numberPicker2;
        picker2.setMaxValue(19-minG/50);
        picker2.setMinValue(0);
        String[] pickerVals2 = new String[20-minG/50];
        if (minKg==0 && minG == 0) pickerVals2[0] = "0g";
        else pickerVals2[0] = minG + "g";
        int qty = minG+50;
        int counter = 1;
        while (qty <= 950) {
            pickerVals2[counter] = qty + "g";
            qty += 50;
            counter++;
        }
        picker2.setDisplayedValues(pickerVals2);

        //Restore Selections
        if (cart.cartItems.containsKey(product.name)){
            int kg = (int) cart.cartItems.get(product.name).qty-minKg;
            String gm = String.valueOf(cart.cartItems.get(product.name).qty);
            gm = gm.substring(gm.indexOf( "." )).replace(".","");
            int g = Integer.parseInt(gm);
            int indexG;
            if (gm.length() == 2) indexG = (g*10)/50-minG/50;
            else indexG = (g*100)/50-minG/50;

            //Set number picker selection according to cart item
            picker1.setValue(kg);
            picker2.setValue(indexG);
        }

        //Set onClickListeners on save button of dialog box
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

                //callback for add to cart
                cart.add(product, qty);
                listener.onCompleted();
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

                //callback for remove from cart
                listener.onCompleted();
                dialog.dismiss();

            }
        });
    }

    public interface WeightPickerCompleteListener{
        void onCompleted();
    }
}
