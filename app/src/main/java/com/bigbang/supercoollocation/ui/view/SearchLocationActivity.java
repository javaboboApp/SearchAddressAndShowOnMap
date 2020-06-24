package com.bigbang.supercoollocation.ui.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.bigbang.supercoollocation.model.data.LocationDetails;
import com.bigbang.supercoollocation.repository.LocationRepositoryImpl;
import com.bigbang.supercoollocation.model.data.Result;
import com.bigbang.supercoollocation.model.network.LocationRetrofitInstance;
import com.bigbang.supercoollocation.ui.view.listeners.LocationListenerImpl;
import com.bigbang.supercoollocation.viewmodel.LocationViewModel;
import com.bigbang.supercoollocation.viewmodel.LocationViewModelFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigbang.supercoollocation.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private EditText searchAddress;
    private GoogleMap mMap;
    private LocationViewModel locationViewModel;
    private static final String TAG = "SearchLocationActivity";
    private final int PERMISSION_REQUEST_ACCES_FINE_LOCATION = 101;
    private LocationManager locationManager;
    private LocationListenerImpl locationListener;
    private TextWatcher textWatcherSearchListener;
    private Button settingsButton;
    private TextView locationPermissionRequired;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        initViews();
        initListeners();
        initLocationManager();
        setUpMap();
        initViewModel();
    }

    private void initLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permission not granted
            requestLocationPermission();
        } else {
            showMap();
            hideLocationPermissionRequired();
            hideSettingsButton();
            registerLocationListener();
        }
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    private void registerLocationListener() {
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10,
                locationListener
        );
    }


    private void initViewModel() {
        //TODO di missing...
        locationViewModel = ViewModelProviders.of(this,
                new LocationViewModelFactory(new LocationRepositoryImpl(LocationRetrofitInstance.getLocationRetrofitInstance().createLocationService())))
                .get(LocationViewModel.class);
    }

    private void setUpMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initViews() {
        searchAddress = findViewById(R.id.search_edittext);
        settingsButton = findViewById(R.id.settings_button);
        locationPermissionRequired = findViewById(R.id.location_textview);
    }

    private void initListeners() {

        locationListener = new LocationListenerImpl() {
            @Override
            public void onLocationChanged(Location location) {
                super.onLocationChanged(location);
                //At this point the location has changed
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                moveAndPaintMarkerOnMap(latLng, "");
            }
        };

        textWatcherSearchListener = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                locationViewModel.getLocationDetails(s.toString()).observe(SearchLocationActivity.this, this::onResult);
            }

            private void onResult(LocationDetails locationDetails) {
                Log.i(TAG, "onResult: called -> " + locationDetails);

                Result result = locationDetails.getResults().get(0);
                //clear the previous markers
                LatLng currentLocation = result.getGeometry().getLocation().asLtLngObject();
                moveAndPaintMarkerOnMap(currentLocation, result.getFormattedAddress());
            }
        };
        searchAddress.addTextChangedListener(textWatcherSearchListener);


        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri packageUri = Uri.fromParts("package", getPackageName(), "Permissions");
            settingsIntent.setData(packageUri);
            startActivity(settingsIntent);
        });
    }

    private void moveAndPaintMarkerOnMap(LatLng latLng, String title) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == PERMISSION_REQUEST_ACCES_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerLocationListener();
                hideSettingsButton();
                hideLocationPermissionRequired();
                showMap();

            } else {
                    showPermissionRequired();
                    showSettingsButton();
                    hideMap();
            }

        }
    }

    private void hideMap() {
        mapFragment.getView().setVisibility(View.GONE);
    }

    private void showMap() {
        mapFragment.getView().setVisibility(View.VISIBLE);

    }

    private void showPermissionRequired() {
        locationPermissionRequired.setVisibility(View.VISIBLE);
    }

    private void hideLocationPermissionRequired() {
        locationPermissionRequired.setVisibility(View.GONE);
    }

    private void showSettingsButton() {
        settingsButton.setVisibility(View.VISIBLE);
    }

    private void hideSettingsButton() {
        settingsButton.setVisibility(View.GONE);
    }

    private void requestLocationPermission() {
        //Request Permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCES_FINE_LOCATION);
    }


}