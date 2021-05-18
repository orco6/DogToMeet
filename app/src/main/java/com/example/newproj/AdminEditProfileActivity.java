package com.example.newproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.newproj.models.CurrentUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class AdminEditProfileActivity extends AppCompatActivity {
    private EditText firstName,lastName,passwordText;
    private Button updateButton;
    private FirebaseFirestore db;
    private DocumentReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_profile);

        firstName = findViewById(R.id.admin_edit_Name);
        lastName = findViewById(R.id.admin_edit_LastName);
        passwordText = findViewById(R.id.admin_edit_password);
        updateButton = findViewById(R.id.updateButtonAdmin);

        db = FirebaseFirestore.getInstance();
        
        firstName.setText(getIntent().getExtras().getString("firstName"));
        lastName.setText(getIntent().getExtras().getString("lastName"));
        passwordText.setText(getIntent().getExtras().getString("password"));
        
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editValidation(firstName.getText().toString(),lastName.getText().toString(), passwordText.getText().toString()))
                    update();
            }
        });
    }

    public Boolean editValidation(String name, String lastname, String password){

        boolean flag=true;
        String namePattern="[a-zA-Z\u0590-\u05fe]+";
        String passwordPattern="[A-Za-z0-9]";

        //name field validation
        if (!name.isEmpty()){
            if(!(name.trim().matches(namePattern))){
                if(firstName!=null)
                    firstName.setError("Name field letters Must A-Z or a-z!");
                flag=false;
            }
            if(name.length()<2) {
                if(firstName!=null)
                    firstName.setError("Name field must be at least 2 letters!");
                flag=false;
            }
        } else{
            if(firstName!=null)
                firstName.setError("Please enter your name!");
            flag=false;
        }

        //last name field validation
        if (!lastname.isEmpty()){
            if(!(lastname.trim().matches(namePattern))){
                if(lastName!=null)
                    lastName.setError("Last-Name field letters Must A-Z or a-z!");
                flag=false;
            }
            if(lastname.length()<2) {
                if(lastName!=null)
                    lastName.setError("Last-Name field must be at least 2 letters!");
                flag=false;
            }
        }
        else{
            if(lastName!=null)
                lastName.setError("Please enter your last name!");
            flag=false;
        }


        //password field validation
        if (!password.isEmpty()){
            if (password.trim().matches(passwordPattern)){
                if(passwordText!=null)
                    passwordText.setError("Passwrod must contain lower or upper or number letters");
                flag=false;
            }
            if (password.length()<5){
                if(passwordText!=null)
                    passwordText.setError("Password length must be at least 6");
                flag=false;
            }
        }
        else{
            if(passwordText!=null)
                passwordText.setError("Please enter password");
            flag=false;
        }

        return flag;
    }

    //update the data in the database
    private void update() {
        WriteBatch batch = db.batch();
        user=db.collection("users").document(CurrentUser.currentUserEmail);
        batch.update(user, "Name", firstName.getText().toString());
        batch.update(user, "LastName", lastName.getText().toString());
        batch.update(user, "Password", passwordText.getText().toString());
        batch.commit();
        Toast.makeText(this,"פרטי משתמש עודכנו בהצלחה",Toast.LENGTH_SHORT).show();
        goToProfile();
    }

    private void goToProfile() {
        Intent intent = new Intent(AdminEditProfileActivity.this,AdminProfileActivity.class);
        finish();
        startActivity(intent);
    }

}
