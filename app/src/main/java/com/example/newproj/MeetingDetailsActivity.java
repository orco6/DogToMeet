package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.FriendsAdapter;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MeetingDetailsActivity extends AppCompatActivity {
    private TextView headLine,description,parkName,parkArea,parkLength,participantsCount;
    private Button joinMeetingButton;
    private ListView participantsListView;
    private FirebaseFirestore db;
    private ArrayList<Users> usersList;
    public ArrayList<String> participants;
    private Users owner,clickedUser;
    private ImageView parkImage;
    StorageReference storageRef;
    public boolean isMember,isOwner;
    private String dogType;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_details);

        FirebaseApp.initializeApp(this);
        headLine = findViewById(R.id.meeting_headline);
        description = findViewById(R.id.meeting_description);
        parkName = findViewById(R.id.park_name);
        parkArea = findViewById(R.id.park_area);
        parkLength = findViewById(R.id.park_length);
        participantsCount = findViewById(R.id.numOfParticipants);
        joinMeetingButton = findViewById(R.id.join_meeting_button);
        participantsListView = findViewById(R.id.participants_listview);
        parkImage = findViewById(R.id.park_img);

        db = FirebaseFirestore.getInstance();
        usersList = new ArrayList<Users>();
        participants = new ArrayList<String>();

        pd = new ProgressDialog(this);
        pd.setMessage("מוחק פגישה...");
        //get owner details
        DocumentReference user = db.collection("users").document(getIntent().getExtras().getString("owner"));
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    owner = new Users();
                    owner.setName(doc.get("Name").toString());
                    owner.setLastName(doc.get("LastName").toString());
                    owner.setEmail(doc.get("Email").toString());
                    owner.setAddress(doc.get("Address").toString());
                    owner.setAge(doc.get("Age").toString());
                    owner.setDogName(doc.get("DogName").toString());
                    owner.setDogType(doc.get("DogType").toString());
                    owner.setImage(doc.get("Image").toString());
                    setHeadLine();
                    usersList.add(0,owner);
                    fillList();
                }
            }
        });

        //get the location details
        DocumentReference park = db.collection("parks").document(getIntent().getExtras().getString("location"));
        park.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    parkName.setText(doc.get("Name").toString());
                    parkLength.setText(doc.get("ShapeLength").toString());
                    parkArea.setText(doc.get("ShapeArea").toString());
                }
            }
        });

        //load park image
        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pref = storageRef.child(getIntent().getExtras().getString("image"));
        Glide.with(this)
                .load(pref)
                .into(parkImage);

        //get participants list
        participants = getIntent().getExtras().getStringArrayList("participants");
        for(String member : participants){
            if(!member.equals(getIntent().getExtras().getString("owner"))) {
                DocumentReference partic = db.collection("users").document(member);
                partic.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            Users newUser = new Users();
                            newUser.setName(doc.get("Name").toString());
                            newUser.setLastName(doc.get("LastName").toString());
                            newUser.setEmail(doc.get("Email").toString());
                            newUser.setAddress(doc.get("Address").toString());
                            newUser.setAge(doc.get("Age").toString());
                            newUser.setDogName(doc.get("DogName").toString());
                            newUser.setDogType(doc.get("DogType").toString());
                            newUser.setImage(doc.get("Image").toString());
                            usersList.add(newUser);
                            fillList();
                        }
                    }
                });
            }
        }

        //set other meeting's details
        if(getIntent().getExtras().getString("description").equals(""))
            description.setText("אין תיאור");
        else
            description.setText(getIntent().getExtras().getString("description"));
        participantsCount.setText("("+Integer.toString(getIntent().getExtras().getStringArrayList("participants").size())+")");

        //button adjustment
        isMember = getIntent().getExtras().getBoolean("isMember");
        isOwner = getIntent().getExtras().getBoolean("isOwner");

        joinMeetingButton.setText(checkButtonText(isMember,isOwner));

        /*if(isMember){
            joinMeetingButton.setText("בטל הצטרפות");
        }
        if(isOwner){
            joinMeetingButton.setText("ערוך פגישה");
        }*/
        dogType=getIntent().getExtras().getString("dogType").toString();
        if(getIntent().getExtras().getString("activityscreen").equals("AdminMeetingsActivity")||(!isOwner&&(!dogType.equals(CurrentUser.dogType.toString()))&&(!dogType.equals("הכל")))){
            joinMeetingButton.setEnabled(false);
        }


        joinMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOwner){
                    openDialog();
                    return;
                }
                HashMap<String,Object> updatedList = new HashMap<>();
                if(isMember){
                    //the user is already participate in the meeting - will remove him from the list
                    participants.remove(CurrentUser.currentUserEmail);
                }
                else {
                    //the user is not participate in the meeting - will add him to the list
                    participants.add(CurrentUser.currentUserEmail);
                }
                if(!getIntent().getExtras().getString("activityscreen").equals("Test")) {
                    updatedList.put("Participants", participants);
                    db.collection("meetings").document(getIntent().getExtras().getString("id")).update(updatedList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DocumentReference user = db.collection("users").document(CurrentUser.currentUserEmail);
                            user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        Users newMember = new Users();
                                        newMember.setName(doc.get("Name").toString());
                                        newMember.setLastName(doc.get("LastName").toString());
                                        newMember.setEmail(doc.get("Email").toString());
                                        newMember.setAddress(doc.get("Address").toString());
                                        newMember.setAge(doc.get("Age").toString());
                                        newMember.setDogName(doc.get("DogName").toString());
                                        newMember.setDogType(doc.get("DogType").toString());
                                        newMember.setImage(doc.get("Image").toString());
                                        if (isMember) {
                                            for (Users search : usersList) {
                                                if (search.getEmail().equals(CurrentUser.currentUserEmail)) {
                                                    usersList.remove(search);
                                                }
                                            }
                                            isMember = false;
                                            joinMeetingButton.setText("הצטרף למפגש");
                                            Toast.makeText(MeetingDetailsActivity.this, "ההצטרפות בוטלה", Toast.LENGTH_SHORT).show();
                                        } else {
                                            usersList.add(newMember);
                                            isMember = true;
                                            joinMeetingButton.setText("בטל הצטרפות");
                                            Toast.makeText(MeetingDetailsActivity.this, "הצטרפת בהצלחה!", Toast.LENGTH_SHORT).show();
                                        }
                                        fillList();
                                        participantsCount.setText("(" + Integer.toString(participants.size()) + ")");
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        participantsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedUser = (Users) parent.getItemAtPosition(position);
                if(clickedUser.getEmail().equals(CurrentUser.currentUserEmail))
                    goToMyProfile();
                else
                    showFriendProfile();
            }
        });
    }

    private void openDialog() {
        String options[] = {"ערוך נתונים","מחק פגישה"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MeetingDetailsActivity.this);
        builder.setTitle("עריכת פגישה");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user chose edit
                if(which==0) {
                    openEditMeeting();
                }
                //user chose delete
                else if(which==1){
                    deleteMeeting();
                }
            }
        });
        builder.create().show();
    }

    private void deleteMeeting() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MeetingDetailsActivity.this);
        builder.setMessage("האם אתה בטוח שברצונך למחוק פגישה זו?").setTitle("מחיקת פגישה");
        builder.setPositiveButton("מחק", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                pd.show();
                //delete meeting from database
                db.collection("meetings").document(getIntent().getExtras().getString("id")).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        Toast.makeText(MeetingDetailsActivity.this,"הפגישה נמחקה",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            }
        });
        builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean allowDelete(Users own){
        return CurrentUser.currentUserEmail.equals(own.getEmail());
    }

    private void openEditMeeting() {
        Intent intent = new Intent(this,CreateMeetingActivity.class);
        intent.putExtra("Target","edit");
        intent.putExtra("id",getIntent().getExtras().getString("id"));
        intent.putExtra("location",getIntent().getExtras().getString("location"));
        intent.putExtra("date",getIntent().getExtras().getString("date"));
        intent.putExtra("hour",getIntent().getExtras().getString("hour"));
        intent.putExtra("description",getIntent().getExtras().getString("desc"));
        intent.putExtra("type",getIntent().getExtras().getString("dogType"));
        startActivity(intent);
    }

    private void goToMyProfile(){
        Intent intent=new Intent(this,UserScreenActivity.class);
        startActivity(intent);
    }

    //open friend profile
    private void showFriendProfile() {
        Intent intent = new Intent(this,UserProfileActivity.class);
        intent.putExtra("name",clickedUser.getName());
        intent.putExtra("lastName",clickedUser.getLastName());
        intent.putExtra("address",clickedUser.getAddress());
        intent.putExtra("dogName",clickedUser.getDogName());
        intent.putExtra("dogType",clickedUser.getDogType());
        intent.putExtra("age",clickedUser.getAge());
        intent.putExtra("image",clickedUser.getImage());
        intent.putExtra("email",clickedUser.getEmail());
        if(isFriend(clickedUser.getEmail())){
            intent.putExtra("isFriend",true);
        }
        else{
            intent.putExtra("isFriend",false);
        }
        startActivity(intent);
    }

    public boolean isFriend(String email) {
        for(String friend_email : CurrentUser.currentUserFriends){
            if(friend_email.equals(email)){
                return true;
            }
        }
        return false;
    }

    private void setHeadLine() {
        String ownerName = owner.getName();
        String dogName = owner.getDogName();
        String date = getIntent().getExtras().getString("date");
        String hour = getIntent().getExtras().getString("hour");
        String park = getIntent().getExtras().getString("location");
        String type = getIntent().getExtras().getString("dogType");
        String typePattern;
        if(type.equals("הכל")){
            typePattern = "לכל סוגי הכלבים";
        }
        else{
            typePattern ="לכלבים מסוג " + type;
        }
        headLine.setText("הצטרפו אל " + ownerName + " ו" + dogName + " במפגש " + typePattern + " ב" + park + " בתאריך " + date + " בשעה " + hour);
    }

    private void fillList(){
        FriendsAdapter arrayAdapter = new FriendsAdapter(this, usersList);
        participantsListView.setAdapter(arrayAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent=null;
        if (getIntent().getExtras().getString("activityscreen").toString().equals("UpcomingMeetingActivity"))
            intent = new Intent(MeetingDetailsActivity.this, UpcomingMeetingsActivity.class);
        else if(getIntent().getExtras().getString("activityscreen").toString().equals("FindMeetingActivity"))
            intent = new Intent(MeetingDetailsActivity.this, FindMeetingsActivity.class);
        else
            intent = new Intent(MeetingDetailsActivity.this,AdminMeetingsActivity.class);
        finish();
        startActivity(intent);
    }

    public int getAmountOfMembers(ArrayList<String> members){
        return members.size();
    }


    public String checkButtonText(boolean is_member,boolean is_owner){
        if(is_owner)
            return "ערוך פגישה";
        if(is_member)
            return "בטל הצטרפות";
        return "הצטרף למפגש";
    }
}
