package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.newproj.models.Meeting;
import com.example.newproj.models.MeetingsAdapter;
import com.example.newproj.models.SortByDate;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class AdminMeetingsActivity extends AppCompatActivity {
    public ListView meetingsListView;
    private TextView meetingsCount;
    private FirebaseFirestore db;
    public ArrayList<Meeting> meetingsList;
    public ArrayList<Meeting> result;
    private ArrayList<Users> usersList;
    private Users user;
    public RadioButton allMeetingsOption,todayOption,meetingsHistory;
    private RadioGroup options;
    public Intent intent;
    public Meeting clickedMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_meetings);

        meetingsListView = findViewById(R.id.admin_meetings_listview);
        meetingsCount = findViewById(R.id.admin_meetings_count);
        allMeetingsOption = findViewById(R.id.admin_all_meetings);
        todayOption = findViewById(R.id.admin_meetings_today);
        meetingsHistory = findViewById(R.id.admin_meetings_history);
        options = findViewById(R.id.admin_meeting_options);

        meetingsList = new ArrayList<Meeting>();
        result = new ArrayList<Meeting>();
        usersList = new ArrayList<Users>();

        db = FirebaseFirestore.getInstance();

        CollectionReference user = db.collection("meetings");
        user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task< QuerySnapshot > task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Meeting meeting = new Meeting();
                        meeting.setID(doc.get("ID").toString());
                        meeting.setDate(doc.get("Date").toString());
                        meeting.setLocation(doc.get("Location").toString());
                        meeting.setHour(doc.get("Hour").toString());
                        meeting.setDogType(doc.get("DogType").toString());
                        meeting.setDiscription(doc.get("Discription").toString());
                        meeting.setOwner(doc.get("Owner").toString());
                        meeting.setParticipants((ArrayList<String>)doc.get("Participants"));
                        meeting.setParkImage(doc.get("ParkImage").toString());
                        meeting.setUserImage(doc.get("UserImage").toString());
                        meetingsList.add(meeting);
                    }
                    result.clear();
                    result.addAll(meetingsList);
                    Collections.sort(result, new SortByDate());
                    try {
                        result = getReleventMeetings(result);
                        meetingsCount.setText("(" + Integer.toString(result.size()) + ")");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    fillList(result);
                }
            }
        });

        todayOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.clear();
                result.addAll(meetingsList);
                Collections.sort(result, new SortByDate());
                try {
                    result = getTodayMeetings(result);
                    meetingsCount.setText("(" + Integer.toString(result.size()) + ")");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                fillList(result);
            }
        });
        allMeetingsOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.clear();
                result.addAll(meetingsList);
                Collections.sort(result, new SortByDate());
                try {
                    result = getReleventMeetings(result);
                    meetingsCount.setText("(" + Integer.toString(result.size()) + ")");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                fillList(result);
            }
        });

        meetingsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.clear();
                result.addAll(meetingsList);
                Collections.sort(result, new SortByDate());
                try {
                    result = getHistoryMeetings(result);
                    meetingsCount.setText("(" + Integer.toString(result.size()) + ")");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                fillList(result);
            }
        });

        meetingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedMeeting = (Meeting) parent.getItemAtPosition(position);
                showMeetingDetails();
            }
        });
    }

    private ArrayList<Meeting> getReleventMeetings(ArrayList<Meeting> list) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String now = df.format(new Date());
        Date today = new SimpleDateFormat("dd/MM/yyyy").parse(now);
        ArrayList<Meeting> relevant = new ArrayList<Meeting>();
        for(Meeting meeting : list){
            Date meetingDate = new SimpleDateFormat("dd/MM/yyyy").parse(meeting.getDate().toString());
            if(!meetingDate.before(today)){
                relevant.add(meeting);
            }
        }
        return relevant;
    }

    //get all meetings that take place today
    private ArrayList<Meeting> getTodayMeetings(ArrayList<Meeting> list) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String now = df.format(new Date());
        Date today = new SimpleDateFormat("dd/MM/yyyy").parse(now);
        ArrayList<Meeting> relevant = new ArrayList<Meeting>();
        for(Meeting meeting : list){
            Date meetingDate = new SimpleDateFormat("dd/MM/yyyy").parse(meeting.getDate().toString());
            if(meetingDate.compareTo(today)==0){
                relevant.add(meeting);
            }
        }
        return relevant;
    }

    //get all meetings that already took place
    public ArrayList<Meeting> getHistoryMeetings(ArrayList<Meeting> list) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String now = df.format(new Date());
        Date today = new SimpleDateFormat("dd/MM/yyyy").parse(now);
        ArrayList<Meeting> relevant = new ArrayList<Meeting>();
        for(Meeting meeting : list){
            Date meetingDate = new SimpleDateFormat("dd/MM/yyyy").parse(meeting.getDate().toString());
            if(meetingDate.before(today)){
                relevant.add(meeting);
            }
        }
        return relevant;
    }

    public int getAmountOfHistoryMeetings(ArrayList<Meeting> list) throws ParseException {
        return getHistoryMeetings(list).size();
    }

    public int getAmountOfTodayMeetings(ArrayList<Meeting> list) throws ParseException {
        return getTodayMeetings(list).size();
    }

    public int getAmountOfRelevantMeetings(ArrayList<Meeting> list) throws ParseException {
        return getReleventMeetings(list).size();
    }

    private void fillList(final ArrayList<Meeting> meetings_list){
        usersList.clear();
        CollectionReference allUsers = db.collection("users");
        allUsers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(Meeting meeting : meetings_list){
                        for(QueryDocumentSnapshot doc : task.getResult()){
                            if(doc.get("Email").toString().equals(meeting.getOwner())){
                                user = new Users();
                                user.setName(doc.get("Name").toString());
                                user.setLastName(doc.get("LastName").toString());
                                user.setEmail(doc.get("Email").toString());
                                user.setImage(doc.get("Image").toString());
                                usersList.add(user);
                            }
                        }
                    }
                    fillList_adapter(meetings_list);
                }
            }
        });

    }

    public void fillList_adapter(ArrayList<Meeting> meetings_list) {
        MeetingsAdapter arrayAdapter = new MeetingsAdapter(AdminMeetingsActivity.this, meetings_list,usersList);
        meetingsListView.setAdapter(arrayAdapter);
    }

    private void showMeetingDetails() {
        intent = new Intent(this,MeetingDetailsActivity.class);
        putextras(intent);
    }

    public void putextras(Intent intent) {
        intent.putExtra("id",clickedMeeting.getID());
        intent.putExtra("owner",clickedMeeting.getOwner());
        intent.putExtra("location",clickedMeeting.getLocation());
        intent.putExtra("date",clickedMeeting.getDate());
        intent.putExtra("hour",clickedMeeting.getHour());
        intent.putExtra("dogType",clickedMeeting.getDogType());
        intent.putExtra("description",clickedMeeting.getDiscription());
        intent.putExtra("image",clickedMeeting.getParkImage());
        intent.putExtra("participants",(ArrayList<String>)clickedMeeting.getParticipants());
        intent.putExtra("activityscreen","AdminMeetingsActivity");
        intent.putExtra("isMember",false);
        intent.putExtra("isOwner",false);
        finish();
        startActivity(intent);
    }
}
