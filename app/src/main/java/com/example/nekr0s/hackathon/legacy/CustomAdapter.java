package com.example.nekr0s.hackathon.legacy;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nekr0s.hackathon.R;
import com.example.nekr0s.hackathon.models.Contact;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    
    private List<Contact> contacts;
    private static LayoutInflater inflater = null;

    public CustomAdapter(Activity a, List<Contact> contacts) {
        this.contacts = contacts;
        inflater = a.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null)
            view = inflater.inflate(R.layout.contacts_listview_legacy, null);
        
        TextView name = view.findViewById(R.id.name);
        TextView number = view.findViewById(R.id.number);
        TextView address = view.findViewById(R.id.address);
        
        name.setText(contacts.get(position).getName());
        number.setText(contacts.get(position).getNumber());
        address.setText(contacts.get(position).getAddress());

        return view;
    }
}
