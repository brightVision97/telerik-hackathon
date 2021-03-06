package com.example.nekr0s.hackathon.views;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.nekr0s.hackathon.utils.Constants;
import com.example.nekr0s.hackathon.R;
import com.example.nekr0s.hackathon.adapter.ContactsAdapter;
import com.example.nekr0s.hackathon.models.Contact;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactsActivity extends AppCompatActivity implements ContactsAdapter.OnContactClickListener
{
    @BindView(R.id.rv_contacts)
    RecyclerView mRecyclerView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        
        ButterKnife.bind(this);
        
        getContactsPermissions();
    }
    
    private void setAdapterAndLayoutManager()
    {
        ContactsAdapter contactsAdapter = new ContactsAdapter(getContacts());
        
        contactsAdapter.setOnContactClickListener(this);
        mRecyclerView.setAdapter(contactsAdapter);
        
        LinearLayoutManager mContactsViewLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mContactsViewLayoutManager);
    }
    
    @Override
    public void onClick(Contact contact)
    {
        String address = contact.getAddress();
        
        if (address == null)
        {
            Toast.makeText(getApplicationContext(),
                    "The contact has no address",
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        
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
    }
    
    private List<Contact> getContacts()
    {
        ArrayList<Contact> contacts = new ArrayList<>();
        try (Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, "display_name ASC"))
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
    
    private String findAddress(final String contactId)
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
    
    private void getContactsPermissions()
    {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS)
                .withListener(new MultiplePermissionsListener()
                {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if (report.areAllPermissionsGranted())
                        {
                            setAdapterAndLayoutManager();
                            Toast.makeText(getApplicationContext(),
                                    "Permissions granted",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else
                            Toast.makeText(getApplicationContext(),
                                    "Permissions denied",
                                    Toast.LENGTH_SHORT)
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
