package com.example.nekr0s.hackathon.views;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

import com.example.nekr0s.hackathon.Constants;
import com.example.nekr0s.hackathon.R;
import com.example.nekr0s.hackathon.adapter.CustomAdapter;
import com.example.nekr0s.hackathon.models.Contact;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView listView = findViewById(R.id.lv_contacts);
        
        getContactsPermission();
        
        CustomAdapter arrayAdapter = new CustomAdapter(
                ContactsActivity.this,
                getContacts()
        );
        
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((parent, view, position, id) ->
        {
            Contact contact = getContacts().get(position);
            String address = contact.getAddress();
            
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, address);
            
            int serviceAvailability =
                    GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            
            if (serviceAvailability == ConnectionResult.SUCCESS)
                startActivity(intent);
            else
                Toast.makeText(getApplicationContext(),
                        "Map requests currently unavailable",
                        Toast.LENGTH_LONG)
                        .show();
        });
    }
    
    private List<Contact> getContacts()
    {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        Cursor cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                null, null, null, null);
        while (Objects.requireNonNull(cursor).moveToNext() && Objects.requireNonNull(cursor2).moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String address = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
            contacts.add(new Contact(name, phoneNumber, address));
        }
        cursor.close();
        return contacts;
    }
    
    private void getContactsPermission()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener()
                {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        Toast.makeText(getApplicationContext(),
                                "Permission granted",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                    
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response)
                    {
                        Toast.makeText(getApplicationContext(),
                                "Permission denied",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                    
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }
}
