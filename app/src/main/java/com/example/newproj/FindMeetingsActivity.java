package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.Meeting;
import com.example.newproj.models.MeetingsAdapter;
import com.example.newproj.models.Parks;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class FindMeetingsActivity extends AppCompatActivity {
    private Spinner parkSpinner,dateSpinner,dogSpinner;
    private Button searchMeetingsButton;
    private ListView meetingsListView;
    public ArrayList<String> parksNames;
    private ArrayList<String> dateOptions;
    public ArrayList<Meeting> meetingsList;
    public ArrayList<Meeting> result;
    private ArrayList<Users> usersList;
    private Users user;
    private Meeting clickedMeeting;
    private FirebaseFirestore db;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_meetings);

        parkSpinner = findViewById(R.id.choose_park);
        dateSpinner = findViewById(R.id.choose_date);
        dogSpinner = findViewById(R.id.choose_dog);
        meetingsListView = findViewById(R.id.result_meetings_listview);
        searchMeetingsButton = findViewById(R.id.search_meeting);

        db = FirebaseFirestore.getInstance();
        myCalendar = Calendar.getInstance();
        parksNames = new ArrayList<String>();
        usersList = new ArrayList<Users>();
        meetingsList = new ArrayList<Meeting>();
        result = new ArrayList<Meeting>();
        dateOptions = new ArrayList<String>();
        dateOptions.add("כל תאריך");
        dateOptions.add("בחר תאריך");

        //get the parks names list
        CollectionReference user = db.collection("parks");
        user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task< QuerySnapshot > task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        parksNames.add(doc.get("Name").toString());
                    }
                    setParksSpinner();
                }
            }
        });

        //get all the meetings
        CollectionReference meetings = db.collection("meetings");
        meetings.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    Collections.sort(meetingsList, new SortByDate());
                    try {
                       meetingsList = getReleventMeetings(meetingsList);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    fillList(meetingsList);
                }
            }
        });

        date= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                setDateSpinner();
                dateSpinner.setSelection(2);
            }
        };

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(dateSpinner.getSelectedItem().toString().equals("בחר תאריך")){
                    new DatePickerDialog(FindMeetingsActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchMeetingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.clear();
                String park = parkSpinner.getSelectedItem().toString();
                String date = dateSpinner.getSelectedItem().toString();
                String dog = dogSpinner.getSelectedItem().toString();
                boolean park_flag,date_flag,dog_flag;
                for(Meeting meeting : meetingsList){
                    park_flag = false;
                    date_flag = false;
                    dog_flag = false;
                    if(park.equals("כל הפארקים") || park.equals(meeting.getLocation()))
                        park_flag = true;
                    if(date.equals("כל תאריך") || date.equals(meeting.getDate()))
                        date_flag = true;
                    if(dog.equals("הכל") || dog.equals(meeting.getDogType()))
                        dog_flag = true;
                    if(park_flag && date_flag && dog_flag)
                        result.add(meeting);
                }
                Collections.sort(result, new SortByDate());
                try {
                   result = getReleventMeetings(result);
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

    //get only the meetings that the date is after today's date
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

    private void showMeetingDetails() {
        Intent intent = new Intent(this,MeetingDetailsActivity.class);
        intent.putExtra("id",clickedMeeting.getID());
        intent.putExtra("owner",clickedMeeting.getOwner());
        intent.putExtra("location",clickedMeeting.getLocation());
        intent.putExtra("date",clickedMeeting.getDate());
        intent.putExtra("hour",clickedMeeting.getHour());
        intent.putExtra("dogType",clickedMeeting.getDogType());
        intent.putExtra("description",clickedMeeting.getDiscription());
        intent.putExtra("image",clickedMeeting.getParkImage());
        intent.putExtra("participants",(ArrayList<String>)clickedMeeting.getParticipants());
        intent.putExtra("activityscreen","FindMeetingActivity");
        if(isMember(clickedMeeting)){
            intent.putExtra("isMember",true);
        }
        else{
            intent.putExtra("isMember",false);
        }
        if(isOwner(clickedMeeting)){
            intent.putExtra("isOwner",true);
        }
        else{
            intent.putExtra("isOwner",false);
        }
        finish();
        startActivity(intent);
    }

    public boolean isOwner(Meeting clicked) {
        if(clicked.getOwner().equals(CurrentUser.currentUserEmail))
            return true;
        return false;
    }

    public boolean isMember(Meeting clicked){
        if(clicked.getParticipants().indexOf(CurrentUser.currentUserEmail) == -1)
            return false;
        return true;
    }

    private void fillList(final ArrayList<Meeting> meetings_list) {
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
                        MeetingsAdapter arrayAdapter = new MeetingsAdapter(FindMeetingsActivity.this, meetings_list,usersList);
                        meetingsListView.setAdapter(arrayAdapter);
                    }
                }
            });

    }

    public void setParksSpinner() {
        parksNames.add(0,"כל הפארקים");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, parksNames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parkSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void setDateSpinner(){
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, dateOptions);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        if(dateOptions.size()<3)
            dateOptions.add(2,sdf.format(myCalendar.getTime()));
        else
            dateOptions.set(2,sdf.format(myCalendar.getTime()));
    }
}
