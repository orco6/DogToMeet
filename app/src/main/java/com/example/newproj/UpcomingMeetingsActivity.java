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

import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.CustomAdapter;
import com.example.newproj.models.Meeting;
import com.example.newproj.models.MeetingsAdapter;
import com.example.newproj.models.Parks;
import com.example.newproj.models.SortByDate;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class UpcomingMeetingsActivity extends AppCompatActivity {
    public ListView meetingsListView;
    private TextView meetingsCount;
    private FirebaseFirestore db;
    private ArrayList<Meeting> meetingsList;
    private ArrayList<Meeting> result;
    private ArrayList<Users> usersList;
    private Users user;
    private RadioButton allMeetingsOption,iCreatedOption,meetingsHistory;
    private Button showButton;
    private RadioGroup options;
    public Intent intent;
    public Meeting clickedMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_meetings);
        FirebaseApp.initializeApp(this);
        meetingsListView = findViewById(R.id.meetings_listview);
        meetingsCount = findViewById(R.id.meetings_count);
        allMeetingsOption = findViewById(R.id.all_meetings);
        iCreatedOption = findViewById(R.id.meetings_i_created);
        meetingsHistory = findViewById(R.id.meetings_history);
        options = findViewById(R.id.meeting_options);

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
                    showAllMeetings();
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
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


        iCreatedOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.clear();
                showMeetingICreated();
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

        allMeetingsOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.clear();
                showAllMeetings();
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

        //get meeting history
        meetingsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.clear();
                showAllMeetings();
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

    private ArrayList<Meeting> getHistoryMeetings(ArrayList<Meeting> list) throws ParseException {
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

    private void showAllMeetings() {
        showMeetingICreated();
        for(Meeting meeting : meetingsList){
            if(iParticipateInMeeting(meeting)){
                if(!meeting.getOwner().equals(CurrentUser.currentUserEmail.toString()))
                    result.add(meeting);
            }
        }
        //Collections.sort(result, new SortByDate());
       // fillList(result);
    }

    private void showMeetingICreated() {
        for(Meeting meeting : meetingsList){
            if(iAmOwnerOfMeeting(meeting.getOwner(),CurrentUser.currentUserEmail)){
                result.add(meeting);
            }
        }
    }

    public boolean iAmOwnerOfMeeting(String meetingOwner,String myName){
        if(meetingOwner.equals(myName)){
            return true;
        }
        return false;
    }

    private boolean iParticipateInMeeting(Meeting meeting){
        for(String user : meeting.getParticipants()){
            if(CurrentUser.currentUserEmail.equals(user))
                return true;
        }
        return false;
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
        MeetingsAdapter arrayAdapter = new MeetingsAdapter(UpcomingMeetingsActivity.this, meetings_list,usersList);
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
        intent.putExtra("activityscreen","UpcomingMeetingActivity");
        if(isMember()){
            intent.putExtra("isMember",true);
        }
        else{
            intent.putExtra("isMember",false);
        }
        if(isOwner()){
            intent.putExtra("isOwner",true);
        }
        else{
            intent.putExtra("isOwner",false);
        }
        finish();
        startActivity(intent);
    }

    private boolean isOwner() {
        if(clickedMeeting.getOwner().equals(CurrentUser.currentUserEmail))
            return true;
        return false;
    }

    private boolean isMember(){
        if(clickedMeeting.getParticipants().indexOf(CurrentUser.currentUserEmail) == -1)
            return false;
        return true;
    }

    public void setUsersList(ArrayList<Users> usersList) {
        this.usersList = usersList;
    }
}
