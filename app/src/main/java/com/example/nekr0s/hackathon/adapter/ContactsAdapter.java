package com.example.nekr0s.hackathon.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nekr0s.hackathon.R;
import com.example.nekr0s.hackathon.models.Contact;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>  {
    private List<Contact> mContacts;
    private OnContactClickListener mOnContactClickListener;

    public ContactsAdapter(List<Contact> contacts) {
        mContacts = contacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_item, parent, false);
        
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.setOnContactClickListener(mOnContactClickListener);
        holder.bind(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public Contact getItem(int position) {
        return mContacts.get(position);
    }

    public void clear() {
        mContacts.clear();
    }

    public void addAll(List<Contact> contacts) {
        mContacts.addAll(contacts);
    }

    public void setOnContactClickListener(OnContactClickListener mOnContactClickListener) {
        this.mOnContactClickListener = mOnContactClickListener;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        
        @BindView(R.id.name)
        TextView mName;

        @BindView(R.id.number)
        TextView mNumber;

        @BindView(R.id.address)
        TextView mAddress;

        private Contact mContact;
        private OnContactClickListener mOnClickListener;

        ContactViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bind(Contact contact) {
            mName.setText(contact.getName());
            mNumber.setText(contact.getNumber());
            mAddress.setText(contact.getAddress());

            mContact = contact;
        }

        @OnClick
        public void onClick() {
            mOnClickListener.onClick(mContact);
        }

        void setOnContactClickListener(OnContactClickListener onClickListener) {
            mOnClickListener = onClickListener;
        }
    }

    public interface OnContactClickListener {
        void onClick(Contact contact);
    }
}
