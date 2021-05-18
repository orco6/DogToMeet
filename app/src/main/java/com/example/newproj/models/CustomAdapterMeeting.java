package com.example.newproj.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newproj.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CustomAdapterMeeting extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    ArrayList<Parks> parks;
    StorageReference storageRef;
    ImageView image;

    public CustomAdapterMeeting(Context applicationContext, ArrayList<Parks> parkList) {
        this.context = context;
        this.parks = parkList;
        inflter = (LayoutInflater.from(applicationContext));
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public int getCount() {
        return parks.size();
    }

    @Override
    public Object getItem(int position) {
        return parks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.activity_park_meetinglist,null);
        TextView name = (TextView)  view.findViewById(R.id.parkNameMeeting);
        image = (ImageView) view.findViewById(R.id.parkImgMeeting);
        name.setText(parks.get(i).getName());
        StorageReference pref = storageRef.child(parks.get(i).getImage());
        Glide.with(view.getContext())
                .load(pref)
                .into(image);
        //Picasso.get().load(uri.getPath()).into(image);
        return view;
    }
}
