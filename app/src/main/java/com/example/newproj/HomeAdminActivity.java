package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.newproj.models.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeAdminActivity extends AppCompatActivity {
    private CardView newUserButton;
    private CardView logOutButton;
    private CardView ParksButton;
    private CardView removeUserButton;
    private CardView searchUsers;
    private CardView profile;
    private CardView meetings;
    private TextView helloLabel;
    private FirebaseFirestore db;

    //initialize
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        newUserButton=findViewById(R.id.addNewUser);
        logOutButton=findViewById(R.id.LogoutButtonAdmin);
        ParksButton = findViewById(R.id.parks_btn);
        helloLabel=findViewById(R.id.hello_label_admin);
        removeUserButton=findViewById(R.id.deleteUser);
        searchUsers = findViewById(R.id.search_users);
        profile = findViewById(R.id.admin_profile);
        meetings = findViewById(R.id.admin_meetings);
        db = FirebaseFirestore.getInstance();
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegiterScreen();

            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        ParksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToParks();
            }
        });

        removeUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRemoveUserScreen();
            }
        });

        searchUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchUsers();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });

        meetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMeetings();
            }
        });


        DocumentReference user = db.collection("users").document(CurrentUser.currentUserEmail);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    helloLabel.setText("שלום"+" "+doc.get("Name").toString());
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    private void goToMeetings() {
        Intent intent = new Intent(this, AdminMeetingsActivity.class);
        startActivity(intent);
    }

    private void goToProfile() {
        Intent intent = new Intent(this, AdminProfileActivity.class);
        startActivity(intent);
    }

    private void goToSearchUsers() {
        Intent intent = new Intent(this, SearchUsersActivity.class);
        intent.putExtra("userType","admin");
        startActivity(intent);
    }

    //Open Parks Activity
    private void goToParks() {
        Intent intent = new Intent(this, ParksActivity.class);
        startActivity(intent);
    }

    //go to register screen
    private void goToRegiterScreen() {
        Intent intent = new Intent(this,AdminRegisterationActivity.class);
        startActivity(intent);

    }
    //logout funtion
    private void logOut() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        SharedPreferences sharedpreferences;
        SharedPreferences.Editor editor;
        sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor=sharedpreferences.edit();
        editor.clear();
        editor.commit();
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    //go to RemoveUsersScreen
    private void goToRemoveUserScreen() {
        Intent intent = new Intent(this, AdminAllUsers.class);
        startActivity(intent);

    }


}

/*
*
* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        newUserButton=findViewById(R.id.addNewUser);
        logOutButton=findViewById(R.id.LogoutButtonAdmin);
        ParksButton = findViewById(R.id.parks_btn);
        helloLabel=findViewById(R.id.hello_label_admin);
        removeUserButton=findViewById(R.id.deleteUser);
        db = FirebaseFirestore.getInstance();
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegiterScreen();

            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        ParksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToParks();
            }
        });

        removeUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRemoveUserScreen();
            }
        });


        DocumentReference user = db.collection("users").document(CurrentUser.currentUserEmail);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    helloLabel.setText("שלום"+" "+doc.get("Name").toString());
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    //open Parks Activity
    private void goToParks() {
        Intent intent = new Intent(this, ParksActivity.class);
        startActivity(intent);
    }

    //go to register screen
    private void goToRegiterScreen() {
        Intent intent = new Intent(this,AdminRegisterationActivity.class);
        startActivity(intent);

    }
    //logout funtion
    private void logOut() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    //go to RemoveUsersScreen
    private void goToRemoveUserScreen() {
        Intent intent = new Intent(this,AdminRemoveUsersActivity.class);
        startActivity(intent);
    }*/