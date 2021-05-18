package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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

public class SearchUsersActivity extends AppCompatActivity {
    private ListView users;
    private FirebaseFirestore db;
    private ArrayList<Users> usersList;
    private ArrayList<Users> results;
    private Users user,clickedUser;
    private ImageButton searchButton;
    private RadioGroup options;
    private RadioButton owner_option,dog_option;
    private EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        FirebaseApp.initializeApp(this);

        users = findViewById(R.id.users_listview);
        searchButton = findViewById(R.id.search_btn);
        options = findViewById(R.id.search_options);
        searchText = findViewById(R.id.search_text);
        owner_option = findViewById(R.id.owner_name);
        dog_option = findViewById(R.id.dog_type);

        db = FirebaseFirestore.getInstance();

        results = new ArrayList<Users>();
        usersList = new ArrayList<Users>();


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
                            usersList.add(user);
                        }
                    showResults(usersList);
                    }
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                results.clear();
                //showResults(results);
                String searchValue = searchText.getText().toString();
                if(searchValue.isEmpty()){
                    showResults(usersList);
                    return;
                }
                if(options.getCheckedRadioButtonId() == owner_option.getId()){
                    serachByUserName(searchValue);
                }
                else{
                    searchByDogType(searchValue);
                }
            }
        });

        users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedUser = (Users) parent.getItemAtPosition(position);
                showFriendProfile();
            }
        });
    }

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
        //check if the user is admin
        if(getIntent().getExtras().getString("userType").equals("admin")){
            intent.putExtra("isFriend",true);
        }
        else if(isFriend(clickedUser.getEmail())){
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

    private void searchByDogType(String searchValue) {
        for(Users usr : usersList){
            if(searchValue.equals(usr.getDogType())){
                results.add(usr);
            }
        }
        showResults(results);
    }

    public void serachByUserName(String searchValue) {
        for(Users usr : usersList){
            String full_name = usr.getName() + " " + usr.getLastName();
            if(searchValue.equals(full_name) || searchValue.equals(usr.getName()) || searchValue.equals(usr.getLastName())){
                    results.add(usr);
            }
        }
        showResults(results);
    }

    private void showResults(ArrayList<Users> list) {
        FriendsAdapter arrayAdapter = new FriendsAdapter(this, list);
        users.setAdapter(arrayAdapter);
    }

    public void setUsersList(ArrayList<Users> usersList) {
        this.usersList = usersList;
    }

    public ArrayList<Users> getResults() {
        return results;
    }
}
