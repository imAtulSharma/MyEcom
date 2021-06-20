package com.streamliners.myecom;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.streamliners.myecom.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final float DEFAULT_ZOOM = 15;
    private GoogleMap mMap;
    private ActivityMapsBinding mainBinding;
    private Geocoder geocoder;
    private android.location.Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission not granted!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
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

        getDeviceLocation();
    }

    // Guard Code

    /**
     * Checking for location permission
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission
                (this.getApplicationContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            );
        }
    }

    // Utility Methods

    /**
     * To get the current location of the device
     * @return location in latitude and longitude form
     */
    private void getDeviceLocation() {
        final LatLng[] currentLocation = new LatLng[1];

        // Get the best and most recent location of the device, which may be null in rare cases when a location is not available.
        try {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            Task<Location> locationResult = client.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            currentLocation[0] = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation[0], DEFAULT_ZOOM));
                        }
                     } else {
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }

                    // To setup the map
                    setupMap(currentLocation[0]);
                }
            });
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * To setup the listeners for the marker and button
     */
    private void setupMap(LatLng currentLocation) {
        // Check for null and set to sydney if null found
        if (currentLocation == null) {
            currentLocation = new LatLng(-31, 151);
        }

        try {
            address = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Marking on map
        mMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .draggable(true)
                .title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                try {
                    address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mainBinding.btnSelect.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("ADDRESS", address.getAddressLine(0));
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}