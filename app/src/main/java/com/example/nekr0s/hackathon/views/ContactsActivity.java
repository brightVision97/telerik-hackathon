package com.example.nekr0s.hackathon.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

import com.example.nekr0s.hackathon.Constants;
import com.example.nekr0s.hackathon.R;
import com.example.nekr0s.hackathon.adapter.CustomAdapter;
import com.example.nekr0s.hackathon.models.Contact;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactsActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.lv_contacts);

        enableRuntimePermission();

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

    private List<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        Cursor cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                null, null, null, null);
        while (Objects.requireNonNull(cursor).moveToNext() &&Objects.requireNonNull(cursor2).moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String address = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
            contacts.add(new Contact(name, phoneNumber, address));
        }
        cursor.close();
        return contacts;
    }

    private void enableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                ContactsActivity.this,
                Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(ContactsActivity.this,
                    "CONTACTS permission allows us to Access CONTACTS app",
                    Toast.LENGTH_LONG)
                    .show();
        } else {
            ActivityCompat.requestPermissions(ContactsActivity.this, new String[]{
                    Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}