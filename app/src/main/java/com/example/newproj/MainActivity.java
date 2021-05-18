package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public TextView emailText,passwordText,registerText;
    public Button loginButton;
    private String type;
    private FirebaseFirestore db;
    private DocumentReference user;
    private Users LoginUser;
    public boolean loginLoged;
    private SharedPreferences sharedpreferences;
    private static final String PREF_NAME = "PreName";
    private int PRIVATE_MODE = 0;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        emailText=findViewById(R.id.EmailText);
        passwordText=findViewById(R.id.PasswordText);
        registerText=findViewById(R.id.register_label);
        loginButton=findViewById(R.id.login_btn);
        db = FirebaseFirestore.getInstance();
        LoginUser = new Users();
        CurrentUser.currentUserEmail=null;
        sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor=sharedpreferences.edit();

        if(sharedpreferences.getString("Email", null)!=null){
            LoginUser.setEmail(sharedpreferences.getString("Email", null));
            LoginUser.setPassword(sharedpreferences.getString("Password", null));
            LoginUser.setType(sharedpreferences.getString("UserType", null));
            if(sharedpreferences.getStringSet("Friends", null)!=null) {
                List<String> Friends = new ArrayList<String>();
                Friends.addAll(sharedpreferences.getStringSet("Friends", null));
                LoginUser.setFriends(Friends);
            }
            String type=sharedpreferences.getString("DogType", null);
            if(sharedpreferences.getString("DogType", null)!=null)
               CurrentUser.dogType=(sharedpreferences.getString("DogType", null));
            passwordText.setText(LoginUser.getPassword());
            CheckUserDetails();

        }




        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToRegisterActivitiy();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();


            }
        });

    }

    //sign in method
    private void SignIn() {
        if(existInput(emailText.getText().toString(),passwordText.getText().toString())){
            user = db.collection("users").document(emailText.getText().toString());
            user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            LoginUser.setEmail(doc.get("Email").toString());
                            LoginUser.setPassword(doc.get("Password").toString());
                            LoginUser.setType(doc.get("UserType").toString());
                            LoginUser.setFriends((List<String>) doc.get("Friends"));
                            LoginUser.setDogType((String)doc.get("DogType"));


                            editor.putString("Email", doc.get("Email").toString());
                            editor.putString("Password", doc.get("Password").toString());
                            editor.putString("UserType", doc.get("UserType").toString());
                            if(LoginUser.getFriends()!=null) {
                                Set<String> hSet = new HashSet<String>(LoginUser.getFriends());
                                editor.putStringSet("Friends", (hSet));
                            }
                            if(LoginUser.getDogType()!=null)
                                editor.putString("DogType", doc.get("DogType").toString());
                            editor.commit();
                            CheckUserDetails();


                        } else {
                            Toast.makeText(MainActivity.this, "Email is not exsits\n please try again", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }

    public boolean existInput(String email, String password) {
        boolean result = true;
        if(email.isEmpty()){
            result = false;
            if(emailText!=null)
                emailText.setError("שדה חובה");
        }
        if(password.isEmpty()){
            result = false;
            if(passwordText!=null)
                passwordText.setError("שדה חובה");
        }
        return result;
    }

    //check user type
    public void CheckUserDetails() {
        String password=passwordText.getText().toString();
        if(LoginUser.getPassword().equals(password)){
            CurrentUser.currentUserEmail=LoginUser.getEmail();
            CurrentUser.currentUserFriends = LoginUser.getFriends();
            if (LoginUser.getUserType().equals("user"))
                goToHomeScreen();
            //check if the user is an admin and go to homescreen admin
            else if (LoginUser.getUserType().equals("admin")){
                goToHomeAdminScreen();
            }
        }
        else{
            Toast.makeText(MainActivity.this,"Incorrect passowrd\n Try Again",Toast.LENGTH_SHORT).show();
        }

    }

    public void setLoginUser(String email, String pass,String type){
        LoginUser.setEmail(email);
        LoginUser.setPassword(pass);
        LoginUser.setUserType(type);
    }

    //switch to RegisterActivity
    private void switchToRegisterActivitiy(){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    //switch to user screen
    private void goToHomeScreen(){
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    //switch to admin screen
    private void goToHomeAdminScreen() {
        Intent intent=new Intent(this,HomeAdminActivity.class);
        startActivity(intent);
        finish();
    }


    public void setLoginUser(Users loginUser) {
        LoginUser = loginUser;
    }
}
