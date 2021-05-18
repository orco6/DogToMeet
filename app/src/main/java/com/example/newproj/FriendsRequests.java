package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.FriendsAdapter;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsRequests extends AppCompatActivity {
    private ListView requestsListView;
    private TextView numOfRequests;
    private FirebaseFirestore db;
    private ArrayList<String> requestList;
    private ArrayList<Users> usersList;
    private Users usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_requests);

        requestsListView = findViewById(R.id.request_list);
        numOfRequests = findViewById(R.id.request_count);

        db = FirebaseFirestore.getInstance();
        requestList = new ArrayList<String>();
        usersList = new ArrayList<Users>();

        DocumentReference user = db.collection("users").document(CurrentUser.currentUserEmail);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    requestList = (ArrayList<String>) doc.get("Requests");
                    numOfRequests.setText("(" + Integer.toString(requestList.size()) + ")");
                    for(String request : requestList){
                        DocumentReference frnd = db.collection("users").document(request);
                        frnd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                    newUser.setFriends((ArrayList<String>)doc.get("Friends"));
                                    usersList.add(newUser);
                                    fillList();
                                }
                            }
                        });

                    }

                }
            }
        });

        requestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usr = (Users) parent.getItemAtPosition(position);
                String options[] = {"הצג פרופיל","אשר","דחה"};
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsRequests.this);
                builder.setTitle("בקשת חברות מאת " + usr.getName() + " " + usr.getLastName());
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0) {
                            showUserProfile();
                        }
                        else if(which==1){
                            acceptRequest();
                        }
                        else if(which==2){
                            rejectRequest();
                        }
                    }
                });
                builder.create().show();

            }
        });
    }

    private void showUserProfile() {
        Intent intent = new Intent(this,UserProfileActivity.class);
        intent.putExtra("name",usr.getName());
        intent.putExtra("lastName",usr.getLastName());
        intent.putExtra("email",usr.getEmail());
        intent.putExtra("address",usr.getAddress());
        intent.putExtra("dogName",usr.getDogName());
        intent.putExtra("dogType",usr.getDogType());
        intent.putExtra("age",usr.getAge());
        intent.putExtra("image",usr.getImage());
        intent.putExtra("isFriend",true);
        startActivity(intent);
    }

    private void rejectRequest() {
        updateFriendRequests();
    }

    private void acceptRequest() {
        updateMyFriendsList();
        updateUserFriendList();
        updateFriendRequests();
    }

    private void updateFriendRequests() {
        requestList.remove(usr.getEmail());
        HashMap<String,Object> newRequestList = new HashMap<>();
        newRequestList.put("Requests",requestList);
        db.collection("users").document(CurrentUser.currentUserEmail).update(newRequestList);
        usersList.remove(usr);
        fillList();
        numOfRequests.setText("(" + Integer.toString(requestList.size()) + ")");
    }

    private void updateUserFriendList() {
        ArrayList<String> friends = (ArrayList<String>)usr.getFriends();
        friends.add(CurrentUser.currentUserEmail);
        HashMap<String,Object> newFriendList = new HashMap<>();
        newFriendList.put("Friends",friends);
        db.collection("users").document(usr.getEmail()).update(newFriendList);
    }

    private void updateMyFriendsList() {
        //ArrayList<String> friends = (ArrayList<String>)CurrentUser.currentUserFriends;
        ArrayList<String> friends = new ArrayList<String>(CurrentUser.currentUserFriends);
        friends.add(usr.getEmail());
        CurrentUser.currentUserFriends.add(usr.getEmail());
        HashMap<String,Object> newFriendList = new HashMap<>();
        newFriendList.put("Friends",friends);
        db.collection("users").document(CurrentUser.currentUserEmail).update(newFriendList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(FriendsRequests.this,"חבר נוסף בהצלחה",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillList(){
        FriendsAdapter arrayAdapter = new FriendsAdapter(this, usersList);
        requestsListView.setAdapter(arrayAdapter);
    }
}
