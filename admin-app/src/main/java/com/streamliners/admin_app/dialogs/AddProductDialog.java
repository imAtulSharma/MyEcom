package com.streamliners.admin_app.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.streamliners.admin_app.MainActivity;
import com.streamliners.admin_app.ProductsActivity;
import com.streamliners.admin_app.R;
import com.streamliners.admin_app.databinding.DialogAddProductBinding;
import com.streamliners.admin_app.databinding.LoaderBinding;
import com.streamliners.admin_app.firebasehelpers.ProductsHelper;
import com.streamliners.models.listeners.OnCompleteListener;
import com.streamliners.models.models.Product;
import com.streamliners.models.models.ProductType;
import com.streamliners.models.models.Variant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddProductDialog extends Activity{
    private static final int PICK_IMAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 13;
    Context context;
    AlertDialog addProductDialog;
    DialogAddProductBinding binding;
    Uri productImgUri;
    int flag =0;
    List<Product> products;
    onProductCreatedListener listener;
    ContentResolver cr;
    LoaderBinding loader;
    AlertDialog loading;
    int prevPos;
    Uri firebaseImgUrl;

    /**
     * Show Add Product Dialog
     * @param context
     * @param products
     * @param cr
     * @param listener
     */
    public void show(Context context, List<Product> products, ContentResolver cr, onProductCreatedListener listener){
        this.cr = cr;
        flag = 0;
        this.context = context;
        this.products = products;
        this.listener = listener;

        loader = LoaderBinding.inflate(LayoutInflater.from(context));
        binding = DialogAddProductBinding.inflate(LayoutInflater.from(context));
        addProductDialog = new MaterialAlertDialogBuilder(context, R.style.CustomDialogTheme)
                .setView(binding.getRoot())
                .show();
        setUpDialogBtn();
        hideError();
    }

    private void hideError() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.pricePerKgEt.setError(null);
                binding.variantsEt.setError(null);
                binding.productNameEt.setError(null);
                binding.minQtyEt.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        binding.pricePerKgEt.addTextChangedListener(textWatcher);
        binding.variantsEt.addTextChangedListener(textWatcher);
        binding.productNameEt.addTextChangedListener(textWatcher);
        binding.minQtyEt.addTextChangedListener(textWatcher);
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ((ProductsActivity) context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    private void setUpDialogBtn() {
        //Setup up image setter
        binding.btnAddImage.setOnClickListener(v -> {
                   if (checkPermissionREAD_EXTERNAL_STORAGE(context)){
                       Intent intent = new Intent();
                       intent.setType("image/*");
                       intent.setAction(Intent.ACTION_GET_CONTENT);
                       ((ProductsActivity) context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                   }

        });


        //Setup Save Button
        binding.btnSave.setOnClickListener(v -> {

            //Add VBP to List
            if (binding.productTypeRadio.getCheckedRadioButtonId() == R.id.radio_vb_product) {
                //Get Product Details
                String name = binding.productNameEt.getText().toString().trim();
                String variants = binding.variantsEt.getText().toString();
                List<Variant> variantList = new ArrayList<>();
                if (name.isEmpty()) {
                    binding.productNameEt.setError("Please Enter Name");
                    return;
                }
                if (variants.isEmpty()) {
                    binding.variantsEt.setError("Please Enter Variants");
                    return;
                }

                List<String> variantStrings = Arrays.asList(variants.split("\n"));
                int counter = 0;
                for (int i =0; i<variantStrings.size(); i++){
                    if (!variantStrings.get(counter).matches("^[a-zA-Z0-9]+,\\s[0-9]+$")){
                        binding.variantsEt.setError("Enter in Correct Format");
                        return;
                    }
                    String[] variantStr = variantStrings.get(counter++).split(",");
                    Variant variant = new Variant((variantStr[0]),Float.parseFloat(String.valueOf(variantStr[1])));
                    variantList.add(variant);
                }

                if (flag == 1) {
                    if (productImgUri == null){
                        addProductDialog.dismiss();
                        loading = new MaterialAlertDialogBuilder(context)
                                .setView(loader.getRoot())
                                .setCancelable(false)
                                .show();
                        Product product = new Product(name, firebaseImgUrl.toString(), variantList);
                        product.position = prevPos;
                        listener.onProductAdded(product,0);
                        loading.dismiss();
                        addProductDialog.dismiss();
                        return;
                    }
                }

                if (productImgUri == null){
                    Toast.makeText(context, "Please Select Image", Toast.LENGTH_LONG).show();
                    return;
                }
                addProductDialog.dismiss();
                loading = new MaterialAlertDialogBuilder(context)
                        .setView(loader.getRoot())
                        .setCancelable(false)
                        .show();
                //Create Product
                ProductsHelper.uploadImg(productImgUri, cr, context, new OnCompleteListener<Uri>() {
                    @Override
                    public void onCompleted(Uri uri) {
                        Product product = new Product(name, uri.toString(), variantList);
                        if (flag == 0) product.position = products.size();
                        else product.position = prevPos;
                        productImgUri = null;
                        listener.onProductAdded(product,flag);
                        loading.dismiss();
                    }

                    @Override
                    public void onFailed(String error) {

                    }
                });



            } else {
                //Get Product Details
                String name = binding.productNameEt.getText().toString().trim();
                String minQty = binding.minQtyEt.getText().toString().trim();
                String pricePerKg = binding.pricePerKgEt.getText().toString().trim();

                if (name.isEmpty()) {
                    binding.productNameEt.setError("Please Enter Name");
                    return;
                }
                if (pricePerKg.isEmpty()){
                    binding.pricePerKgEt.setError("Enter Price Per Kg");
                    return;
                }
                if (minQty.isEmpty()){
                    binding.minQtyEt.setError("Enter Min Qty");
                    return;
                }
                if (!minQty.matches("^([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[eE]([+-]?\\d+))?kg$") && !minQty.matches("^([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[eE]([+-]?\\d+))?g$")){
                    binding.minQtyEt.setError("Enter in format : 1kg/1g");
                    return;
                }
                Float minQ;
                if (minQty.matches("^([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[eE]([+-]?\\d+))?kg$")){
                    minQty = minQty.replace("kg","");
                    minQ = Float.valueOf(minQty);
                }
                else minQ = Float.parseFloat(minQty)/1000;

                if (flag == 1) {
                    if (productImgUri == null){
                        addProductDialog.dismiss();
                        loading = new MaterialAlertDialogBuilder(context)
                                .setView(loader.getRoot())
                                .setCancelable(false)
                                .show();
                        Product product = new Product(name, firebaseImgUrl.toString(), minQ, Float.parseFloat(pricePerKg));
                        product.position = prevPos;
                        listener.onProductAdded(product,0);
                        loading.dismiss();
                        addProductDialog.dismiss();
                        return;
                    }
                }


                if (productImgUri == null){
                    Toast.makeText(context, "Please Select Image", Toast.LENGTH_LONG).show();
                    return;
                }

                addProductDialog.dismiss();
                loading = new MaterialAlertDialogBuilder(context)
                        .setView(loader.getRoot())
                        .setCancelable(false)
                        .show();
                //Create Product
                ProductsHelper.uploadImg(productImgUri, cr, context, new OnCompleteListener<Uri>() {
                    @Override
                    public void onCompleted(Uri uri) {
                        Product product = new Product(name, uri.toString(), minQ, Float.parseFloat(pricePerKg));
                        if (flag == 0) product.position = products.size();
                        else product.position = prevPos;
                        productImgUri = null;
                        listener.onProductAdded(product,0);

                        addProductDialog.dismiss();
                        loading.dismiss();
                    }

                    @Override
                    public void onFailed(String error) {

                    }
                });

            }
        });

        if (flag == 0) binding.productTypeRadio.check(R.id.radio_wb_product);

        //SetUpRadio Buttons
        binding.productTypeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_vb_product) {
                    binding.variantInput.setVisibility(View.VISIBLE);
                    binding.wbpInput.setVisibility(View.INVISIBLE);
                } else {
                    binding.variantInput.setVisibility(View.INVISIBLE);
                    binding.wbpInput.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Dialog to Edit Product
     */
    public void editProduct(Context context, List<Product> products,Product product, onProductCreatedListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
        productImgUri = null;


        flag = 1;
        loader = LoaderBinding.inflate(LayoutInflater.from(context));

        //Inflate Dialog Binding
        binding = DialogAddProductBinding.inflate(LayoutInflater.from(context));
        //Product to edit
        Product editProduct = product;
        prevPos = product.position;
        firebaseImgUrl = Uri.parse(editProduct.imageURL);


        //Set Product information in dialog fields
        binding.productNameEt.setText(editProduct.name);
        Glide.with(context)
                .asBitmap()
                .load(editProduct.imageURL)
                .into(binding.imageProduct);



        //For Variant Based Product
        if (editProduct.type == ProductType.TYPE_VARIANT_BASED_PRODUCT){
            //Set Product information in dialog fields
            binding.productTypeRadio.check(R.id.radio_vb_product);
            binding.wbpInput.setVisibility(View.INVISIBLE);
            binding.variantInput.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < editProduct.variants.size(); i++){
                sb.append(editProduct.variants.get(i).name).append(", ").append(String.valueOf(editProduct.variants.get(i).price).replaceFirst("\\.0+$", ""));
                if (i!= (editProduct.variants.size()-1)) sb.append("\n");
            }
            binding.variantsEt.setText(sb.toString());
        }
        else {
            binding.productTypeRadio.check(R.id.radio_wb_product);
            //Set Product information in dialog fields
            //Set PricePerKG
            String pricePerKg = String.valueOf(editProduct.pricePerKg);
            pricePerKg = pricePerKg.replaceFirst("\\.0+$", "");
            binding.pricePerKgEt.setText(pricePerKg);

            //Set MinQty
            String minQty;
            if (editProduct.minQty<1) minQty = String.valueOf(editProduct.minQty*1000).replaceFirst("\\.0+$", "") + "g";
            else minQty = String.valueOf(editProduct.minQty).replaceFirst("\\.0+$", "") + "kg";
            binding.minQtyEt.setText(minQty);
        }

        //Show Dialog
        addProductDialog = new MaterialAlertDialogBuilder(context, R.style.CustomDialogTheme)
                .setView(binding.getRoot())
                .show();


        //Set Btns OnClickListeners
        setUpDialogBtn();
    }

    //Add Image
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Checking the result status
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            assert data != null;
            this.productImgUri = data.getData();
            Glide.with(context)
                    .asBitmap()
                    .load(productImgUri)
                    .into(binding.imageProduct);
        }
    }



    public interface onProductCreatedListener{
        void onProductAdded(Product product, int flag);
    }
}
