package com.streamliners.admin_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.streamliners.admin_app.controllers.AdapterCallbacksListener;
import com.streamliners.admin_app.controllers.ProductsAdapter;
import com.streamliners.admin_app.tmp.ProductsHelper;
import com.streamliners.admin_app.databinding.ActivityProductsBinding;
import com.streamliners.admin_app.databinding.DialogAddProductBinding;
import com.streamliners.models.Cart;
import com.streamliners.models.Product;
import com.streamliners.models.ProductType;
import com.streamliners.models.Variant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    ActivityProductsBinding productsBinding;
    DialogAddProductBinding binding;
    Context context;
    ProductsAdapter adapter;
    List<Product> products;
    SharedPreferences sharedPreferences;
    private static final int PICK_IMAGE = 1;

    //Add/Edit Product Dialog and Product Image Uri
    AlertDialog addProductDialog;
    Uri productImgUri;

    //Edit or Add Product Flag
    int flag =0;

    //Drag and Drop Modes and Callback
    ItemTouchHelper itemTouchHelper2;
    ItemTouchHelper.SimpleCallback dragCallback;
    private static final int MODE_ENABLE = 1;
    private static final int MODE_DISABLE = 0;
    private static int MODE = MODE_DISABLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productsBinding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(productsBinding.getRoot());

        sharedPreferences = getPreferences(MODE_PRIVATE);
        products = new ProductsHelper().getProducts();
        getDataFromSharedPrefs();
        context = this;

        setUpRecyclerView();

        setUpFab();
    }

    /**
     * Setup recycler view adapter
     */
    private void setUpRecyclerView(){
        adapter = new ProductsAdapter(this, products);

        productsBinding.list.setLayoutManager(new LinearLayoutManager(this));
        productsBinding.list.setAdapter(adapter);
        itemTouchHelperCallback();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.editProduct){
            editProduct();
            return true;
        }
        else if (item.getItemId() == R.id.deleteProduct){
            deleteProduct();
            return true;
        }
        return false;
    }

    /**
     * Delete Product From List
     */
    private void deleteProduct() {
        //Delete Product and Update Recycler View
        products.remove(adapter.productPosition);
        adapter.visibleProducts.remove(adapter.productPosition);
        adapter.notifyDataSetChanged();
    }

    /**
     * Edit Product
     */
    private void editProduct() {
        //Inflate Dialog Binding
        binding = DialogAddProductBinding.inflate(LayoutInflater.from(context));
        //Product to edit
        Product editProduct = products.get(adapter.productPosition);

        //Set Product information in dialog fields
        binding.productNameEt.setText(editProduct.name);
        Glide.with(context)
                .asBitmap()
                .load(editProduct.imageURL)
                .into(binding.imageProduct);

        flag = 1;

        //For Variant Based Product
        if (editProduct.type == ProductType.TYPE_VARIANT_BASED_PRODUCT){
            //Set Product information in dialog fields
            binding.productTypeRadio.check(R.id.radio_vb_product);
            binding.wbpInput.setVisibility(View.INVISIBLE);
            binding.variantInput.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < editProduct.variants.size(); i++){
                sb.append(editProduct.variants.get(i).name).append(", ").append(editProduct.variants.get(i).price).append("\n");
            }
            binding.variantsEt.setText(sb.toString());
        }
        else {
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
        addProductDialog = new MaterialAlertDialogBuilder(this, R.style.CustomDialogTheme)
                .setView(binding.getRoot())
                .show();

        //Set Image
        productImgUri = Uri.parse(editProduct.imageURL);

        //Set Btns OnClickListeners
        setUpDialogBtn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu
        getMenuInflater().inflate(R.menu.product_activity_options, menu);

        // Get the search view
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
        return true;
    }

    /**
     * Setup Options Menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_products){
            //Sort Products
            adapter.sortAlphabetically();
            return true;
        }
        else if (item.getItemId() == R.id.drag_drop){
            //Change Icon
            if(MODE==MODE_ENABLE) item.setIcon(R.drawable.ic_baseline_drag_handle_24);
            else  item.setIcon(R.drawable.ic_baseline_check_24);
            //Enable/Disable Drag
            enableDisableDrag();
        }
        return false;
    }

    /**
     * swipe and dragDrop callBack
     */
    private void itemTouchHelperCallback() {
         dragCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                Collections.swap(products, fromPosition, toPosition);
                Collections.swap(adapter.visibleProducts, fromPosition, toPosition);

                adapter.notifyItemMoved(fromPosition, toPosition);

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        itemTouchHelper2 = new ItemTouchHelper(dragCallback);
        if(MODE==MODE_ENABLE)
            itemTouchHelper2.attachToRecyclerView(productsBinding.list);
        else
            itemTouchHelper2.attachToRecyclerView(null);
    }

    /**
     * setup enable disable drag and drop
     */
    private void enableDisableDrag() {
        //Disable Drag Drop
        if(MODE==MODE_ENABLE){
            MODE=MODE_DISABLE;
            adapter.mode = 0;
            adapter.notifyDataSetChanged();
            itemTouchHelper2.attachToRecyclerView(null);
        }
        //enable Drag Drop
        else {
            adapter.mode = 1;
            MODE=MODE_ENABLE;
            for (int i = 0; i < adapter.wbProductBindings.size(); i++){
                adapter.listenerSetter(adapter.wbProductBindings.get(i));
            }
            for (int i = 0; i < adapter.vbProductBindings.size(); i++){
                adapter.listenerSetter(adapter.vbProductBindings.get(i));
            }
            itemTouchHelper2.attachToRecyclerView(productsBinding.list);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(products);
        prefsEditor.putString(Constants.productList, json);
        prefsEditor.apply();
        prefsEditor.commit();
    }


    /**
     * Get Data From Shared Preferences
     */
    private void getDataFromSharedPrefs(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Constants.productList, "");
        products = gson.fromJson(json, new TypeToken<List<Product>>(){}.getType());
    }

    /**
     * Setup FAB Button to add image
     */
    private void setUpFab() {
        productsBinding.btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding = DialogAddProductBinding.inflate(LayoutInflater.from(context));
                addProductDialog = new MaterialAlertDialogBuilder(context, R.style.CustomDialogTheme)
                        .setView(binding.getRoot())
                        .show();
                setUpDialogBtn();
            }
        });
    }


    private void setUpDialogBtn() {
        //Setup up image setter
        binding.btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });


        //Setup Save Button
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add VBP to List
                if (binding.productTypeRadio.getCheckedRadioButtonId() == R.id.radio_vb_product) {
                    //Get Product Details
                    String name = binding.productNameEt.getText().toString().trim();
                    String imageUrl = String.valueOf(productImgUri);
                    String variants = binding.variantsEt.getText().toString();
                    List<Variant> variantList = new ArrayList<>();
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

                    //Create Product
                    Product product = new Product(name, imageUrl, variantList);

                    //Add/Edit Product in the list
                    if (flag == 1){
                        flag = 0;
                        products.set(adapter.productPosition, product);
                        adapter.visibleProducts.set(adapter.productPosition, product);
                        adapter.notifyItemChanged(adapter.productPosition);
                        addProductDialog.dismiss();
                        return;
                    }
                    products.add(product);
                    adapter.visibleProducts.add(product);

                    //Dismiss Dialog
                    addProductDialog.dismiss();
                    adapter.notifyDataSetChanged();
                } else {
                    //Get Product Details
                    String name = binding.productNameEt.getText().toString().trim();
                    String minQty = binding.minQtyEt.getText().toString().trim();
                    String pricePerKg = binding.pricePerKgEt.getText().toString().trim();
                    String imageUrl = String.valueOf(productImgUri);

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

                    //Create Product
                    Product product = new Product(name, imageUrl, minQ, Float.parseFloat(pricePerKg));

                    //Add/Edit Product in the list
                    if (flag == 1){
                        flag = 0;
                        products.set(adapter.productPosition, product);
                        adapter.visibleProducts.set(adapter.productPosition, product);
                        adapter.notifyItemChanged(adapter.productPosition);
                        addProductDialog.dismiss();
                        return;
                    }
                    products.add(product);
                    adapter.visibleProducts.add(product);
                    adapter.notifyDataSetChanged();
                    addProductDialog.dismiss();
                }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                productImgUri = data.getData();
            }
        }
        Glide.with(context)
                .asBitmap()
                .load(productImgUri)
                .into(binding.imageProduct);
    }

}