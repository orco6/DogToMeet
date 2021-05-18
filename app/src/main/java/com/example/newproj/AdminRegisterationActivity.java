package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
//create new user
public class AdminRegisterationActivity extends AppCompatActivity {
    private TextView regName,regLastName,regAge,regEmail,regPassword,regBackToLogin,regAdderss;
    private Button regButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registeration);
        regName=findViewById(R.id.AddUserFirstName);
        regLastName=findViewById(R.id.AdduserLastName);
        regAge=findViewById(R.id.AddUserAge);
        regEmail=findViewById(R.id.AddUserEmail);
        regPassword=findViewById(R.id.addPasswordUser);
        regButton=findViewById(R.id.adminAddUser);
        regAdderss=findViewById(R.id.addUserAddress);
        db = FirebaseFirestore.getInstance();



        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidText(regName.getText().toString(),regLastName.getText().toString(),regAge.getText().toString(),regEmail.getText().toString(),regPassword.getText().toString()))
                    signUp();
                }

        });

    }

    private void signUp(){
        DocumentReference user = db.collection("users").document(regEmail.getText().toString());
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(!doc.exists()) {
                        Users usr=new Users(regName.getText().toString(),regLastName.getText().toString(),regAge.getText().toString(),regEmail.getText().toString(),regPassword.getText().toString(),regAdderss.getText().toString());
                        Map<String, Object> user = new HashMap<>();
                        user.put("Name", usr.getName());
                        user.put("LastName", usr.getLastName());
                        user.put("Age", usr.getAge());
                        user.put("Email", usr.getEmail());
                        user.put("Password", usr.getPassword());
                        user.put("UserType", usr.getUserType());
                        user.put("DogName","");
                        user.put("DogType","");
                        user.put("Image",usr.getImage());
                        user.put("Friends",usr.getFriends());
                        user.put("Address",usr.getAddress());
                        user.put("Requests",usr.getRequests());

                        db.collection("users")
                                .document(usr.getEmail()).set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AdminRegisterationActivity.this, "משתמש נוצר בהצלחה", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                    else{
                        Toast.makeText(AdminRegisterationActivity.this, regEmail.getText().toString()+" already exsits \nplease try other email or Login ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public boolean ValidText(String name, String lastname, String age, String email, String password){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
        String namePattern="[a-zA-Z\u0590-\u05fe]+";
        String passwordPattern="[A-Za-z0-9]";
        boolean flag=true;

        //name field validation
        if (!name.isEmpty()){
            if(!(name.trim().matches(namePattern))){
                if(regName!=null)
                    regName.setError("Name field letters Must A-Z or a-z!");
                flag=false;
            }
            if(name.length()<2) {
                if(regName!=null)
                    regName.setError("Name field must be at least 2 letters!");
                flag=false;
            }
        } else{
            if(regName!=null)
                regName.setError("Please enter your name!");
            flag=false;
        }

        //last name field validation
        if (!lastname.isEmpty()){
            if(!(lastname.trim().matches(namePattern))){
                if(regLastName!=null)
                    regLastName.setError("Last-Name field letters Must A-Z or a-z!");
                flag=false;
            }
            if(lastname.length()<2) {
                if(regLastName!=null)
                    regLastName.setError("Last-Name field must be at least 2 letters!");
                flag=false;
            }
        }
        else{
            if(regLastName!=null)
                regLastName.setError("Please enter your last name!");
            flag=false;
        }

        //age field validation
        if (age.isEmpty()){
            if(regAge!=null)
                regAge.setError("Please enter your age");
            flag=false;
        }

        //email field validation
        if (!email.isEmpty()){
            if(!email.trim().matches(emailPattern)){
                if(regEmail!=null)
                    regEmail.setError("Please enter a valid email");
                flag=false;
            }
        }
        else {
            if(regEmail!=null)
                regEmail.setError("Please enter your email");
            flag=false;
        }

        //password field validation
        if (!password.isEmpty()){
            if (password.trim().matches(passwordPattern)){
                if(regPassword!=null)
                    regPassword.setError("Passwrod must contain lower or upper or number letters");
                flag=false;
            }
            if (password.length()<5){
                if(regPassword!=null)
                    regPassword.setError("Password length must be at least 6");
                flag=false;
            }
        }
        else{
            if(regPassword!=null)
                regPassword.setError("Please enter password");
            flag=false;
        }

        return flag;
    }


}
