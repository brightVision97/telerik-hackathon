package com.example.nekr0s.hackathon.utils;

import agency.tango.android.avatarview.AvatarPlaceholder;
import agency.tango.android.avatarview.ImageLoaderBase;
import agency.tango.android.avatarview.views.AvatarView;
import android.support.annotation.NonNull;
import com.bumptech.glide.Glide;

public class GlideLoader extends ImageLoaderBase
{
    public GlideLoader()
    {
        super();
    }
    
    public GlideLoader(String defaultPlaceholderString)
    {
        super(defaultPlaceholderString);
    }
    
    @Override
    public void loadImage(@NonNull AvatarView avatarView,
                          @NonNull AvatarPlaceholder avatarPlaceholder,
                          String avatarUrl)
    {
        Glide.with(avatarView.getContext())
                .load(avatarUrl)
                .crossFade()
                .placeholder(avatarPlaceholder)
                .fitCenter()
                .into(avatarView);
    }
}
