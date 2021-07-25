package com.streamliners.admin_app.firebasehelpers;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.streamliners.models.listeners.OnCompleteListener;
import com.streamliners.models.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProductsHelper {

    /**
     * Add Product to Firestore
     * @param product
     * @param context
     * @param listener
     */
    public void addToFireStore(Product product,Context context, OnCompleteListener<Product> listener) {
        //Initialize FireStore Database
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("products").add(product).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        listener.onCompleted(product);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        new MaterialAlertDialogBuilder(context)
                                .setMessage(e.toString())
                                .show();
                    }
                });
    }

    /**
     * Edit/Update Product in Firestore
     * @param position
     * @param newProduct
     * @param listener
     */
    public static void editProduct(int position, Product newProduct, OnCompleteListener<Integer> listener) {
        //Initialize Firestore Database
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        //Update product
        db.collection("products").whereEqualTo("position", position).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            db.collection("products").document(snapshot.getId())
                                    .set(newProduct)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            listener.onCompleted(position);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            listener.onFailed(e.toString());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        listener.onFailed(e.toString());
                    }
                });
    }

    /**
     * Delete Product From FireStore
     * @param position
     * @param products
     * @param listener
     */
    public static void deleteProduct(int position,List<Product> products, OnCompleteListener<String> listener) {
        //Initialize Firestore Database
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        //Delete Product
        db.collection("products").whereEqualTo("position", position).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String id = "";
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            id = snapshot.getId();
                        }

                        db.collection("products").document(id).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(products.get(position).imageURL);
                                        reference.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        listener.onCompleted("Done");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull @NotNull Exception e) {
                                                        listener.onFailed(e.toString());
                                                    }
                                                });

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        listener.onFailed(e.toString());
                    }
                });

        //Update Position of other products
        db.collection("products").whereGreaterThan("position", position).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int posy = position;
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            db.collection("products").document(snapshot.getId()).update("position", posy);
                            posy++;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        listener.onFailed(e.toString());
                    }
                });

    }

    /**
     * Move Product after drag and drop in firestore
     * @param from
     * @param to
     * @param context
     */
    public static void move(int from, int to, Context context) {
        //Initialize Firestore Database
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        //if product is dragged up
        if (to < from) {
            db.collection("products").whereGreaterThanOrEqualTo("position", to).whereLessThanOrEqualTo("position", from).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int counter = to + 1;
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                if (counter == from + 1) {
                                    db.collection("products").document(snapshot.getId()).update("position", to);
                                    continue;
                                }
                                db.collection("products").document(snapshot.getId()).update("position", counter);
                                counter++;
                            }
                        }
                    });
        }
        //if product is dragged down
        else {
            db.collection("products").whereGreaterThanOrEqualTo("position", from).whereLessThanOrEqualTo("position", to).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int counter = from;
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                if (counter == from) {
                                    db.collection("products").document(snapshot.getId()).update("position", to);
                                    counter++;
                                    continue;
                                }
                                db.collection("products").document(snapshot.getId()).update("position", counter - 1);
                                counter++;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            new MaterialAlertDialogBuilder(context)
                                    .setMessage(e.toString())
                                    .show();

                        }
                    });

        }
    }

    /**
     * Fetch Products From FireStore
     * @param products
     * @param listener
     */
    public void getData(List<Product> products, OnCompleteListener<List<Product>> listener) {
        //Initialize Firestore Database
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        //Get products according to position
        db.collection("products").orderBy("position").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            if (snapshot.exists()) {
                                products.add(snapshot.toObject(Product.class));
                            }
                        }
                        listener.onCompleted(products);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        listener.onFailed(e.toString());
                    }
                });
    }

    /**
     * Sort Products in Firestore
     * @param context
     */
    public static void sortAlphabetically(Context context) {
        //Initialize Firestore Database
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        //Sort products in firestore
        db.collection("products").orderBy("name").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int posy = 0;
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            db.collection("products").document(snapshot.getId()).update("position", posy);
                            posy++;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        new MaterialAlertDialogBuilder(context)
                                .setMessage(e.toString())
                                .show();

                    }
                });
    }

    /**
     * Upload Image in firebase storage
     * @param uri
     * @param cr
     * @param context
     * @param listener
     */
    public static void uploadImg(Uri uri, ContentResolver cr, Context context, OnCompleteListener<Uri> listener) {
        //Initialize Storage of Firebase
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri, cr));

        //Put Image
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listener.onCompleted(uri);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                new MaterialAlertDialogBuilder(context)
                        .setMessage(e.toString())
                        .show();
            }
        });
    }

    private static String getFileExtension(Uri uri, ContentResolver cr) {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}
