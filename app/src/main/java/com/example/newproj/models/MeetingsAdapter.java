package com.example.newproj.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.newproj.R;
import com.example.newproj.UserScreenActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MeetingsAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    ArrayList<Meeting> meetings;
    ArrayList<Users> usersList;
    StorageReference storageRef;
    ImageView Parkimage;
    ImageView usrimage;
    FirebaseFirestore db;
    View view;

    public MeetingsAdapter(Context applicationContext, final ArrayList<Meeting> meetingsList,ArrayList<Users> usersList) {
        this.context = context;
        this.meetings = meetingsList;
        this.usersList = usersList;
        inflter = (LayoutInflater.from(applicationContext));
        storageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public int getCount() {
        return meetings.size();
    }

    @Override
    public Object getItem(int position) {
        return meetings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View temp_view, ViewGroup parent) {
        view = temp_view;
        view = inflter.inflate(R.layout.activity_meetings_listview,null);
        TextView date = (TextView)  view.findViewById(R.id.meeting_date);
        TextView location = (TextView)  view.findViewById(R.id.meeting_park);
        TextView hour = (TextView)  view.findViewById(R.id.meeting_time);
        TextView count = (TextView)  view.findViewById(R.id.participants_count);
        TextView type = (TextView)  view.findViewById(R.id.meeting_type);
        final TextView creator = (TextView)  view.findViewById(R.id.meeting_creator);
        Parkimage = (ImageView) view.findViewById(R.id.meeting_img);
        usrimage = (ImageView) view.findViewById(R.id.creator_img);
        date.setText(meetings.get(i).getDate());
        location.setText(meetings.get(i).getLocation());
        hour.setText(meetings.get(i).getHour());
        count.setText(Integer.toString(meetings.get(i).getParticipants().size()));
        type.setText(meetings.get(i).getDogType());
        StorageReference pref;
        creator.setText(usersList.get(i).getName() + " " + usersList.get(i).getLastName());
        pref = storageRef.child(usersList.get(i).getImage());
        Glide.with(view.getContext())
                .load(pref)
                .into(usrimage);

        pref = storageRef.child(meetings.get(i).getParkImage());
        Glide.with(view.getContext())
                .load(pref)
                .into(Parkimage);


        return view;
    }

}
