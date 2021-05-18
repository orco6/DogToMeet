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

public class FriendsAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    ArrayList<Users> friends;
    StorageReference storageRef;
    ImageView image;

    public FriendsAdapter(Context applicationContext, ArrayList<Users> userList) {
        this.context = context;
        this.friends = userList;
        inflter = (LayoutInflater.from(applicationContext));
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.activity_friends_listview,null);
        TextView name = (TextView)  view.findViewById(R.id.user_name);
        image = (ImageView) view.findViewById(R.id.usrImg);
        name.setText(friends.get(i).getName()+" "+friends.get(i).getLastName());
        StorageReference pref;
        pref = storageRef.child(friends.get(i).getImage());
        Glide.with(view.getContext())
                .load(pref)
                .into(image);
        return view;
    }
}
