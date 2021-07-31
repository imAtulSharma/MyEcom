package com.streamliners.myecom.tmp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.streamliners.models.listeners.OnCompleteListener;
import com.streamliners.models.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FirebaseHelper {
    public void addToFireStore(Product product, OnCompleteListener<Product> listener){
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("products").add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        listener.onCompleted(product);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                    }
                });
    }

    public void getProducts(List<Product> products, OnCompleteListener<List<Product>> listener){
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("products").orderBy("position").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                            if (snapshot.exists()){
                                products.add(snapshot.toObject(Product.class));
                                listener.onCompleted(products);
                            }
                        }
                    }
                });
    }

    /**
     * Fetch the orders according to the particular user
     * @param orders list of orders
     * @param phoneNumber phone number of the user
     * @param listener listener to handle callbacks
     */
    public void getOrders(List<Order> orders, String phoneNumber, OnOrderQueryListener listener){
        CollectionReference colRef = FirebaseFirestore.getInstance()
                .collection("orders");
        colRef.whereEqualTo("userAuthPhoneNumber", phoneNumber)
                .orderBy("createdTime").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                listener.onCompleted();

                if (e != null) {
                    listener.onError(e.toString());
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    if (doc != null) {
                        listener.orderFetched(doc.getId(), doc.toObject(Order.class));
                    }
                }
            }
        });
    }

    /**
     * Cancels a particular order
     * @param orderId ID of the order
     * @param listener listener to handle callbacks
     */
    public void cancelOrder(String orderId, OnOrderCancelListener listener)  {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("orders").document(orderId);

        docRef.update("status", Order.OrderStatus.CANCELLED)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onSuccessfulCancelled();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e.toString());
                    }
                });
    }

    /**
     * Places an order
     * @param order order to be placed
     * @param listener listener to handle callbacks
     */
    public void placeOrder(Order order, OnCompleteListener<Order> listener) {
        // Firstly fetching the user's device token and then sending the notification
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            listener.onFailed(task.getException().toString());
                            return;
                        }

                        order.userDeviceToken = task.getResult();
                        FirebaseFirestore db;
                        db = FirebaseFirestore.getInstance();
                        db.collection("orders").add(order)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        listener.onCompleted(order);
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

    /**
     * Represent listener for the firebase query
     */
    public interface OnOrderQueryListener {
        /**
         * When query executed completely
         */
        void onCompleted();

        /**
         * When new order found
         * @param orderId ID of the order
         * @param order order found
         */
        void orderFetched(String orderId, Order order);

        /**
         * On error occurs
         * @param error error occurred
         */
        void onError(String error);
    }

    /**
     * Represents listener for the order cancelled query
     */
    public interface OnOrderCancelListener {
        /**
         * Successful cancellation of the order
         */
        void onSuccessfulCancelled();

        /**
         * When error occurs
         * @param error error occurred
         */
        void onError(String error);
    }
}
