package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.FriendsAdapter;
import com.example.newproj.models.Parks;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyFriendsActivity extends AppCompatActivity {
    public ListView friends;
    private TextView friendsCount;
    private FirebaseFirestore db;
    private ArrayList<String> friendsList;
    private ArrayList<Users> usersList;
    FriendsAdapter arrayAdapter;


    public Users friend;
    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        FirebaseApp.initializeApp(this);
        friends = findViewById(R.id.friends_listview);
        friendsCount = findViewById(R.id.friends_count);
        db = FirebaseFirestore.getInstance();
        friendsList = new ArrayList<String>();
        usersList = new ArrayList<Users>();


        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                friend = (Users) parent.getItemAtPosition(position);
                showFriendProfile();
            }
        });


        DocumentReference user = db.collection("users").document(CurrentUser.currentUserEmail);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    friendsList = (ArrayList<String>) doc.get("Friends");
                    int count = friendsList.size();
                    friendsCount.setText("(" + Integer.toString(count) + ")");
                    for(String friend : friendsList){
                        DocumentReference frnd = db.collection("users").document(friend);
                        frnd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    Users newUser;
                                    newUser=setUserFields(doc.get("Name").toString(),doc.get("LastName").toString(),
                                                  doc.get("Email").toString(),doc.get("Address").toString(),
                                                  doc.get("Age").toString(),doc.get("DogName").toString(),
                                                  doc.get("DogType").toString(),doc.get("Image").toString());
                                    usersList.add(newUser);
                                    /*
                                    newUser.setName(doc.get("Name").toString());
                                    newUser.setLastName(doc.get("LastName").toString());
                                    newUser.setEmail(doc.get("Email").toString());
                                    newUser.setAddress(doc.get("Address").toString());
                                    newUser.setAge(doc.get("Age").toString());
                                    newUser.setDogName(doc.get("DogName").toString());
                                    newUser.setDogType(doc.get("DogType").toString());
                                    newUser.setImage(doc.get("Image").toString());
                                    usersList.add(newUser);
                                     */
                                    fillList();
                                }
                            }
                        });

                    }

                }
            }
        });


    }

    public void setFriend(Users friend) {
        this.friend = friend;
    }

    public Users setUserFields(String name,String lastname,String email,String address,String age,String dogname,String dogtype,String image) {
        Users newUser = new Users();
        newUser.setName(name);
        newUser.setLastName(lastname);
        newUser.setEmail(email);
        newUser.setAddress(address);
        newUser.setAge(age);
        newUser.setDogName(dogname);
        newUser.setDogType(dogtype);
        newUser.setImage(image);
        return newUser;
    }

    public void fillList(){
        arrayAdapter = new FriendsAdapter(this, usersList);
        friends.setAdapter(arrayAdapter);
    }

    //open friend profile
    public void showFriendProfile(){
        intent = new Intent(this,UserProfileActivity.class);
        putIntent(intent,friend);
        /*
        intent.putExtra("name",friend.getName());
        intent.putExtra("lastName",friend.getLastName());
        intent.putExtra("email",friend.getEmail());
        intent.putExtra("address",friend.getAddress());
        intent.putExtra("dogName",friend.getDogName());
        intent.putExtra("dogType",friend.getDogType());
        intent.putExtra("age",friend.getAge());
        intent.putExtra("image",friend.getImage());
        intent.putExtra("isFriend",true);

         */
        startActivity(intent);

    }

    public void putIntent(Intent intent, Users friend) {
        intent.putExtra("name",friend.getName());
        intent.putExtra("lastName",friend.getLastName());
        intent.putExtra("email",friend.getEmail());
        intent.putExtra("address",friend.getAddress());
        intent.putExtra("dogName",friend.getDogName());
        intent.putExtra("dogType",friend.getDogType());
        intent.putExtra("age",friend.getAge());
        intent.putExtra("image",friend.getImage());
        intent.putExtra("isFriend",true);
    }

    public void setUsersList(ArrayList<Users> usersList) {
        this.usersList = usersList;
    }
}
