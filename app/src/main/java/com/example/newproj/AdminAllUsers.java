package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.FriendsAdapter;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminAllUsers extends AppCompatActivity {
    public ListView users;
    private TextView userCount,userLabel;
    private FirebaseFirestore db;
    public ArrayList<Users> usersList;
    public Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        FirebaseApp.initializeApp(this);
        users = findViewById(R.id.friends_listview);
        userCount=findViewById(R.id.friends_count);
        userLabel=findViewById(R.id.friends_label);
        userLabel.setText("משתמשים");
        db = FirebaseFirestore.getInstance();
        usersList = new ArrayList<Users>();

        //get all users from database - except admins
        CollectionReference allUsers = db.collection("users");
        allUsers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        if(!(doc.get("UserType").toString().equals("admin") || doc.get("Email").toString().equals(CurrentUser.currentUserEmail))){
                            user = new Users();
                            user.setName(doc.get("Name").toString());
                            user.setLastName(doc.get("LastName").toString());
                            user.setAge(doc.get("Age").toString());
                            user.setAddress(doc.get("Address").toString());
                            user.setImage(doc.get("Image").toString());
                            user.setDogType(doc.get("DogType").toString());
                            user.setDogName(doc.get("DogName").toString());
                            user.setEmail(doc.get("Email").toString());
                            user.setPassword(doc.get("Password").toString());
                            usersList.add(user);
                        }
                    }
                    showResults(usersList);
                    userCount.setText(""+usersList.size());
                }
            }
        });

        users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                user = (Users) parent.getItemAtPosition(position);
                Intent intent = new Intent(AdminAllUsers.this,AdminToUserProfileActivity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("lastName",user.getLastName());
                intent.putExtra("address",user.getAddress());
                intent.putExtra("dogName",user.getDogName());
                intent.putExtra("dogType",user.getDogType());
                intent.putExtra("age",user.getAge());
                intent.putExtra("image",user.getImage());
                intent.putExtra("email",user.getEmail());
                intent.putExtra("password",user.getPassword());
                startActivity(intent);
                finish();
            }
        });


    }

    public boolean isAdmin(String type){
        return type.equals("admin");
    }

    public void showResults(ArrayList<Users> list) {
        FriendsAdapter arrayAdapter = new FriendsAdapter(this, list);
        users.setAdapter(arrayAdapter);
    }
}
