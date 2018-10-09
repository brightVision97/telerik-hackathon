package com.example.nekr0s.hackathon.views;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.lv_contacts)
    ListView mContactsListView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ButterKnife.bind(this);
        
        getContactsPermission();
        
        CustomAdapter arrayAdapter = new CustomAdapter(this, getContacts());
        
        mContactsListView.setAdapter(arrayAdapter);
        mContactsListView.setOnItemClickListener((parent, view, position, id) ->
        {
            String address = getContacts().get(position)
                    .getAddress();
            
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
        try (Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, "display_name" + " ASC"))
        {
            while (Objects.requireNonNull(cursor).moveToNext())
            {
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                String address = findAddress(cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
                contacts.add(new Contact(name, phoneNumber, address));
            }
        }
        
        return contacts;
    }
    
    private String findAddress(String contactId)
    {
        String emailToReturn = null;
        try (Cursor addresses = getContentResolver().query(
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID +
                        " = " + contactId, null, null))
        {
            while (Objects.requireNonNull(addresses).moveToNext())
                emailToReturn = addresses.getString(addresses.getColumnIndex(
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
        }
        
        return emailToReturn;
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
