package com.streamliners.admin_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.streamliners.admin_app.controllers.ProductsAdapter;
import com.streamliners.admin_app.databinding.LoaderBinding;
import com.streamliners.admin_app.dialogs.AddProductDialog;
import com.streamliners.admin_app.firebasehelpers.ProductsHelper;
import com.streamliners.admin_app.databinding.ActivityProductsBinding;
import com.streamliners.admin_app.databinding.DialogAddProductBinding;
import com.streamliners.models.listeners.OnCompleteListener;
import com.streamliners.models.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    ActivityProductsBinding productsBinding;
    DialogAddProductBinding binding;
    Context context;
    ProductsAdapter adapter;
    List<Product> products;
    SharedPreferences sharedPreferences;

    AddProductDialog addProductDialog = new AddProductDialog();

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
        products = new ArrayList<>();
        new ProductsHelper().getData(products, new OnCompleteListener<List<Product>>() {
            @Override
            public void onCompleted(List<Product> products) {
                if (products.size() == 0){
                    productsBinding.noProductsTv.setVisibility(View.VISIBLE);
                }
                productsBinding.progressBar.setVisibility(View.GONE);
                setUpRecyclerView();
            }

            @Override
            public void onFailed(String error) {
                new MaterialAlertDialogBuilder(context)
                        .setMessage(error)
                        .show();
            }
        });
        context = this;


        setUpFab();
    }

    /**
     * Setup recycler view adapter
     */
    private void setUpRecyclerView() {
        adapter = new ProductsAdapter(this, products);

        productsBinding.list.setLayoutManager(new LinearLayoutManager(this));
        productsBinding.list.setAdapter(adapter);
        itemTouchHelperCallback();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.editProduct) {
            addProductDialog.editProduct(this, products, products.get(adapter.productPosition), new AddProductDialog.onProductCreatedListener() {
                @Override
                public void onProductAdded(Product product, int flag) {
                    LoaderBinding loader = LoaderBinding.inflate(getLayoutInflater());
                    loader.textView.setText("Updating Product");
                    AlertDialog dialog = new MaterialAlertDialogBuilder(ProductsActivity.this)
                            .setView(loader.getRoot())
                            .show();
                    ProductsHelper.editProduct(adapter.productPosition, product, new OnCompleteListener<Integer>() {
                        @Override
                        public void onCompleted(Integer integer) {
                            dialog.dismiss();
                            Toast.makeText(context, "Product Updated", Toast.LENGTH_SHORT).show();
                            products.set(adapter.productPosition, product);
                            adapter.visibleProducts.set(adapter.productPosition, product);
                            adapter.notifyItemChanged(adapter.productPosition);
                        }
                        @Override
                        public void onFailed(String error) {
                            dialog.dismiss();
                            new MaterialAlertDialogBuilder(context)
                                    .setMessage(error)
                                    .show();

                        }
                    });

                }
            });
            return true;
        } else if (item.getItemId() == R.id.deleteProduct) {
            deleteProduct();
            return true;
        }
        return false;
    }

    /**
     * Delete Product From List
     */
    private void deleteProduct() {
        LoaderBinding loader = LoaderBinding.inflate(getLayoutInflater());
        loader.textView.setText("Deleting Product");
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(loader.getRoot())
                .show();
        //Delete Product and Update Recycler View
        ProductsHelper.deleteProduct(adapter.productPosition,products, new OnCompleteListener<String>() {
            @Override
            public void onCompleted(String s) {
                dialog.dismiss();
                products.remove(adapter.productPosition);
                adapter.visibleProducts.remove(adapter.productPosition);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Deletion Failed")
                        .setMessage(error)
                        .show();
            }
        });

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
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_products) {
            //Sort Products
            adapter.sortAlphabetically();
            return true;
        } else if (item.getItemId() == R.id.drag_drop) {
            //Change Icon
            if (MODE == MODE_ENABLE) item.setIcon(R.drawable.ic_baseline_drag_handle_24);
            else item.setIcon(R.drawable.ic_baseline_check_24);
            //Enable/Disable Drag
            enableDisableDrag();
        }
        return false;
    }



    /**
     * dragDrop callBack
     */
    private void itemTouchHelperCallback() {
        List<Integer> tos = new ArrayList<>();
        List<Integer> froms = new ArrayList<>();
        dragCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                tos.add(toPosition);
                froms.add(fromPosition);
                Collections.swap(products, fromPosition, toPosition);
                Collections.swap(adapter.visibleProducts, fromPosition, toPosition);

                adapter.notifyItemMoved(fromPosition, toPosition);

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public boolean canDropOver(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder current, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return super.canDropOver(recyclerView, current, target);
            }

            @Override
            public void clearView(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
                if (froms.size() == 0 || tos.size() == 0){
                    return;
                }
                ProductsHelper.move(froms.get(0), tos.get(tos.size()-1), context);
                tos.clear();
                froms.clear();
            }
        };



        itemTouchHelper2 = new ItemTouchHelper(dragCallback);
        if (MODE == MODE_ENABLE)
            itemTouchHelper2.attachToRecyclerView(productsBinding.list);
        else
            itemTouchHelper2.attachToRecyclerView(null);
    }

    /**
     * setup enable disable drag and drop
     */
    private void enableDisableDrag() {
        //Disable Drag Drop
        if (MODE == MODE_ENABLE) {
            MODE = MODE_DISABLE;
            adapter.mode = 0;
            adapter.notifyDataSetChanged();
            itemTouchHelper2.attachToRecyclerView(null);
        }
        //enable Drag Drop
        else {
            adapter.mode = 1;
            MODE = MODE_ENABLE;
            for (int i = 0; i < adapter.wbProductBindings.size(); i++) {
                adapter.listenerSetter(adapter.wbProductBindings.get(i));
            }
            for (int i = 0; i < adapter.vbProductBindings.size(); i++) {
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
    private void getDataFromSharedPrefs() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Constants.productList, "");
        products = gson.fromJson(json, new TypeToken<List<Product>>() {
        }.getType());
    }

    /**
     * Setup FAB Button to add image
     */
    private void setUpFab() {
        productsBinding.btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding = DialogAddProductBinding.inflate(LayoutInflater.from(context));
                addProductDialog.show(context, products,getContentResolver(), new AddProductDialog.onProductCreatedListener() {
                    @Override
                    public void onProductAdded(Product product, int flag) {
                        new ProductsHelper().addToFireStore(product,ProductsActivity.this, new OnCompleteListener<Product>() {
                            @Override
                            public void onCompleted(Product product) {
                                products.add(product);
                                adapter.visibleProducts.add(product);
                                if (products.size() == 1) setUpRecyclerView();
                                else adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailed(String error) {

                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addProductDialog.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 13:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ((ProductsActivity) context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
                } else {
                    Toast.makeText(context, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);

        }
    }

}