package com.streamliners.admin_app.firebasehelpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.streamliners.models.listeners.OnCompleteListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrdersHelper {

    /**
     * Get the order from the firebase
     * @param listener listener for the callbacks
     */
    public void getOrders(OnCompleteListener<List<Order>> listener){
        List<Order> orders = new ArrayList<>();

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        db.collection("orders").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                            if (snapshot.exists()){
                                orders.add(snapshot.toObject(Order.class));
                            }
                        }
                        listener.onCompleted(orders);
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                listener.onFailed(e.toString());
            }
        });
    }

    public void liveOrders(OnOrderQueryListener listener) {
        CollectionReference colRef = FirebaseFirestore.getInstance()
                .collection("orders");
        colRef.orderBy("createdTime").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        listener.onNewOrderReceived(doc.getId(), doc.toObject(Order.class));
                    }
                }
            }
        });
    }

    public void changeOrderState(String orderId, int state, OrderStateChangeListener listener) {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("orders").document(orderId);

        docRef.update("status", state)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onSuccessfulChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e.toString());
                    }
                });
    }

    public interface OnOrderQueryListener {
        void onCompleted();
        void onNewOrderReceived(String orderId, Order order);
        void onError(String error);
    }

    public interface OrderStateChangeListener {
        void onSuccessfulChanged();
        void onError(String error);
    }
}
