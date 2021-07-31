package com.streamliners.myecom;

import android.app.Application;
import android.content.Context;


import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MyApplication extends Application {
    private AlertDialog alertDialog;

    /**
     * Constructor to initialize the dialog
     */
    MyApplication(Context context) {
        alertDialog = new MaterialAlertDialogBuilder(context)
                .setView(R.layout.dialog_loading)
                .setCancelable(false)
                .create();
    }

    /**
     * Shows the dialog
     */
    public void showDialog() {
        alertDialog.show();
    }

    /**
     * Hides the dialog
     */
    public void hideDialog() {
        alertDialog.hide();
    }
}
