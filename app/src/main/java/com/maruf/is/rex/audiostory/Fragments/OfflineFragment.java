package com.maruf.is.rex.audiostory.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.maruf.is.rex.audiostory.R;

public class OfflineFragment extends Fragment {

    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_offline, container, false);

       imageView = view.findViewById(R.id.imgLol);


        Glide.with(this)
                .load("https://i.ibb.co/9GWXYpL/67756812-2041099399518389-2095919619263954944-o.jpg")
                .placeholder(R.drawable.image_loading)
                .into(imageView);

        return view;
    }
}
