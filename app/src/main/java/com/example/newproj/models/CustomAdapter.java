package com.example.newproj.models;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newproj.ParksActivity;
import com.example.newproj.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    ArrayList<Parks> parks;
    StorageReference storageRef;
    ImageView image;

    public CustomAdapter(Context applicationContext, ArrayList<Parks> parkList) {
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.activity_park_listview,null);
        TextView name = (TextView)  view.findViewById(R.id.parkName);
        TextView area = (TextView)  view.findViewById(R.id.parkArea);
        TextView length = (TextView)  view.findViewById(R.id.parkLength);
        image = (ImageView) view.findViewById(R.id.parkImg);
        name.setText(parks.get(i).getName());
        area.setText(parks.get(i).getArea());
        length.setText(parks.get(i).getLength());
        StorageReference pref = storageRef.child(parks.get(i).getImage());
        Glide.with(view.getContext())
                .load(pref)
                .into(image);
        //Picasso.get().load(uri.getPath()).into(image);
        return view;
    }
}
