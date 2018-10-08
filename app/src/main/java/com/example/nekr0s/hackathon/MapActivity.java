package com.example.nekr0s.hackathon;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity
{
    private GoogleMap googleMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        getLocationFromAddress(getIntent().getStringExtra("address"));
        
        initializeMap();
    }
    
    private void getLocationFromAddress(String strAddress)
    {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        
        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            
            if (address == null)
                return;
            
            Address location = address.get(0);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            
            Marker srchMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng));
            
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void initializeMap()
    {
        if (googleMap == null)
            ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(gmap -> googleMap = gmap);
    }
}
