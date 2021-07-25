package com.streamliners.myecom;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.streamliners.myecom.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String KEY_ADDRESS = "Address";
    public static final String KEY_LATLNG = "LatLng";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final int REQ_CODE_CHECK_SETTINGS = 1003;
    private static final float DEFAULT_ZOOM = 15;

    private FusedLocationProviderClient client;

    private GoogleMap mMap;
    private ActivityMapsBinding mainBinding;
    private Geocoder geocoder;
    private android.location.Address address;
    private Marker marker;
    private LatLng midLatLng;
    private boolean isSelectionAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.map);

        mainBinding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkLocationPermission();

        mainBinding.fab.setOnClickListener(v -> getLiveLocation());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.no_location_permission, Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
            }

            if (checkGpsEnabledOrNot()){
                getLastLocation();
            } else{
                GPSOnDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    getLastLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(this, R.string.no_gps, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    // Guard Code

    /**
     * Checking for location permission
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission
                (this.getApplicationContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            );
        } else {
            if (checkGpsEnabledOrNot()){
                getLastLocation();
            } else{
                GPSOnDialog();
            }
        }
    }

    /**
     * Check for GPS enabled or not
     */
    private boolean checkGpsEnabledOrNot() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * To open dialog for GPS on
     */
    private void GPSOnDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().

                                resolvable.startResolutionForResult(
                                        MapsActivity.this,
                                        REQ_CODE_CHECK_SETTINGS);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.

                            break;
                    }
                }
            }
        });
    }

    // Utility Methods

    /**
     * To get the current location of the device
     */
    private void getLastLocation() {
        final LatLng[] currentLocation = new LatLng[1];
        // Get the best and most recent location of the device, which may be null in rare cases when a location is not available.
        try {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            Task<Location> locationResult = client.getLastLocation();

            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    Location lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        currentLocation[0] = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation[0], DEFAULT_ZOOM));

                        // To setup the map
                        setupMap(currentLocation[0]);
                    } else {
                        getLiveLocation();
                    }
                } else {
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            });
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * To get live location of device
     */
    private void getLiveLocation() {
        if (!checkGpsEnabledOrNot()) {
            GPSOnDialog();
        }
        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient

        try {
            client = LocationServices.getFusedLocationProviderClient(this);
            client.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location mLastLocation = locationResult.getLastLocation();
                    setupMap(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                }
            }, Looper.myLooper());
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * To setup the listeners for the marker and button
     */
    private void setupMap(LatLng currentLocation) {

        // Check for null and set to sydney if null found
        if (currentLocation == null) {
            currentLocation = new LatLng(28.614230857536658, 77.20901794731617);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));

        mainBinding.btnSelection.setOnClickListener(view -> changeSelectionMode());

        mainBinding.btnConfirm.setOnClickListener(view -> sendData());
    }

    /**
     * To change the selection mode for location setting
     */
    private void changeSelectionMode() {
        if (isSelectionAvailable) {
            // Marking on map
            midLatLng = new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);

            try {
                address = geocoder.getFromLocation(midLatLng.latitude, midLatLng.longitude, 1).get(0);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                return;
            }

            marker = mMap.addMarker(new MarkerOptions()
                    .position(midLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
                    .draggable(true)
                    .title(address.getAddressLine(0)));

            mainBinding.marker.setVisibility(View.INVISIBLE);
            mainBinding.btnConfirm.setVisibility(View.VISIBLE);
            mainBinding.btnSelection.setText(getResources().getString(R.string.select_again));
            mainBinding.btnSelection.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.light_red), PorterDuff.Mode.MULTIPLY);
        } else {
            marker.remove();
            mainBinding.marker.setVisibility(View.VISIBLE);
            mainBinding.btnConfirm.setVisibility(View.GONE);
            mainBinding.btnSelection.setText(getResources().getString(R.string.select));
            mainBinding.btnSelection.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
        }

        isSelectionAvailable = !isSelectionAvailable;
    }

    /**
     * Send the result to the parent activity
     */
    private void sendData() {
        Intent intent = new Intent();

        // Make latitude and longitude array
        double[] latLng = {midLatLng.latitude, midLatLng.longitude};

        // Set the data in the intent
        intent.putExtra(KEY_ADDRESS, address.getAddressLine(0));
        intent.putExtra(KEY_LATLNG, latLng);

        // Send result and finish the activity
        setResult(RESULT_OK, intent);
        finish();
    }
}