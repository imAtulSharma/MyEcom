package com.streamliners.myecom.messaging;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper to get the firebase remote config key
 */
public class RemoteConfigHelper {
    /**
     * Remote config instance
     */
    private final static FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    /**
     * Key to be fetched
     */
    private final static String AUTHENTICATION_KEY = "authentication_key";

    /**
     * Returns the key fetched from the server
     * @param context context of the application
     * @param listener listner ot handle the callbacks
     */
    public static void getAuthenticationKey(Context context, OnRemoteConfigFetchedListener listener) {
        // Remote config settings
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        // Setting default parameters from the remote config
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(AUTHENTICATION_KEY, "");
        mFirebaseRemoteConfig.setDefaultsAsync(parameters);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        // Extracting the key
                        String key = mFirebaseRemoteConfig.getString(AUTHENTICATION_KEY);

                        // Checking the key's emptiness
                        if (key.isEmpty()) {
                            listener.onErrorOccurred("Unable to get authentication key");
                            return;
                        }

                        // calling the listener
                        listener.onSuccessfullyFetched(key);
                    }
                })
                .addOnFailureListener((Activity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        // Calling the listener
                        listener.onErrorOccurred(e.toString());
                    }
                });
    }

    /**
     * Represents the listener for the remote config calls
     */
    public interface OnRemoteConfigFetchedListener {
        /**
         * When the data fetched successfully
         * @param key authentication key
         */
        void onSuccessfullyFetched(String key);

        /**
         * When error occurred in fetching the data
         * @param error error occurred
         */
        void onErrorOccurred(String error);
    }
}
