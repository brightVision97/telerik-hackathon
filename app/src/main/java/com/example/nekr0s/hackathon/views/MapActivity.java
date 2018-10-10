package com.example.nekr0s.hackathon.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;
import com.example.nekr0s.hackathon.Constants;
import com.example.nekr0s.hackathon.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.example.nekr0s.hackathon.R.layout.activity_map);
        
        getLocationPermission();
        
        initializeMap();
    }
    
    private void geoLocate(final String locationName)
    {
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        
        try
        {
            list = geocoder.getFromLocationName(locationName, 1);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        Address address = list.get(0);
        
        moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                address.getAddressLine(0));
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        
        geoLocate(getIntent().getStringExtra(Constants.LOCATION_NAME_DATA_EXTRA));
    }
    
    private void moveCamera(LatLng latLng, String title)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM));
        
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        
        mMap.addMarker(options);
    }
    
    private void initializeMap()
    {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }
    
    private void getLocationPermission()
    {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener()
                {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if (report.areAllPermissionsGranted())
                        {
                            Toast.makeText(getApplicationContext(),
                                    "Permissions granted",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else
                            Toast.makeText(getApplicationContext(),
                                    "Permissions denied",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        
                        if (report.isAnyPermissionPermanentlyDenied())
                            Toast.makeText(getApplicationContext(),
                                    "Permissions permanently denied",
                                    Toast.LENGTH_LONG)
                                    .show();
                    }
                    
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                   PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }
}
