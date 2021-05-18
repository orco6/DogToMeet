package com.example.newproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.Users;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//edit user ditails
public class AdminEditUserProfileActivity extends AppCompatActivity {
    public EditText firstNameText,lastNameText,addressText,dogNameText,dogTypeText,ageText,passwordText;
    private Button update_btn;
    private FirebaseFirestore db;
    private DocumentReference user;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_user_profile);
        FirebaseApp.initializeApp(this);
        initButtons();
        setTextsToButtons();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //put validation method here
                if (editValidation(firstNameText.getText().toString(),lastNameText.getText().toString(),ageText.getText().toString(),
                        passwordText.getText().toString()))
                    upadte();
            }
        });


    }

    public Boolean editValidation(String name, String lastname, String age, String password){

        boolean flag=true;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
        String namePattern="[a-zA-Z\u0590-\u05fe]+";
        String passwordPattern="[A-Za-z0-9]";

        //name field validation
        if (!name.isEmpty()){
            if(!(name.trim().matches(namePattern))){
                if(firstNameText!=null)
                    firstNameText.setError("Name field letters Must A-Z or a-z!");
                flag=false;
            }
            if(name.length()<2) {
                if(firstNameText!=null)
                    firstNameText.setError("Name field must be at least 2 letters!");
                flag=false;
            }
        } else{
            if(firstNameText!=null)
                firstNameText.setError("Please enter your name!");
            flag=false;
        }

        //last name field validation
        if (!lastname.isEmpty()){
            if(!(lastname.trim().matches(namePattern))){
                if(lastNameText!=null)
                    lastNameText.setError("Last-Name field letters Must A-Z or a-z!");
                flag=false;
            }
            if(lastname.length()<2) {
                if(lastNameText!=null)
                    lastNameText.setError("Last-Name field must be at least 2 letters!");
                flag=false;
            }
        }
        else{
            if(lastNameText!=null)
                lastNameText.setError("Please enter your last name!");
            flag=false;
        }

        //age field validation
        if (age.isEmpty()){
            if(ageText!=null)
                ageText.setError("Please enter your age");
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

    private void upadte() {
        WriteBatch batch = db.batch();
        user=db.collection("users").document(getIntent().getExtras().getString("email").toString());
        batch.update(user, "Name", firstNameText.getText().toString());
        batch.update(user, "LastName", lastNameText.getText().toString());
        batch.update(user, "Age", ageText.getText().toString());
        batch.update(user, "Address", addressText.getText().toString());
        batch.update(user, "DogName", dogNameText.getText().toString());
        batch.update(user, "DogType", dogTypeText.getText().toString());
        batch.update(user, "Password", passwordText.getText().toString());
        batch.commit();
        CurrentUser.dogType=dogTypeText.getText().toString();
        Toast.makeText(this,"פרטי משתמש עודכנו בהצלחה",Toast.LENGTH_SHORT).show();
        goToProfile();
    }

    private void goToProfile() {
        Intent intent = new Intent(AdminEditUserProfileActivity.this,AdminToUserProfileActivity.class);
        putextras(intent);
        finish();
        startActivity(intent);
    }

    public void putextras(Intent intent) {
        intent.putExtra("name",firstNameText.getText().toString());
        intent.putExtra("lastName",lastNameText.getText().toString());
        intent.putExtra("address",addressText.getText().toString());
        intent.putExtra("dogName",dogNameText.getText().toString());
        intent.putExtra("dogType",dogTypeText.getText().toString());
        intent.putExtra("age",ageText.getText().toString());
        intent.putExtra("email",getIntent().getExtras().getString("email"));
        intent.putExtra("image",getIntent().getExtras().getString("image"));
        intent.putExtra("password",passwordText.getText().toString());
    }

    public void setTextsToButtons() {
        try {
            firstNameText.setText(getIntent().getExtras().getString("name").toString());
            lastNameText.setText(getIntent().getExtras().getString("lastName").toString());
            ageText.setText(getIntent().getExtras().getString("age").toString());
            addressText.setText(getIntent().getExtras().getString("address").toString());
            dogNameText.setText(getIntent().getExtras().getString("dogName").toString());
            dogTypeText.setText(getIntent().getExtras().getString("dogType").toString());
            passwordText.setText(getIntent().getExtras().getString("password").toString());
        }
        catch(Exception e){

        }
    }

    public void initButtons(){
        firstNameText=findViewById(R.id.editFirstName);
        lastNameText=findViewById(R.id.editLastName);
        addressText=findViewById(R.id.editAdress);
        dogNameText=findViewById(R.id.editDogName);
        dogTypeText=findViewById(R.id.editDogType);
        ageText=findViewById(R.id.editAge);
        passwordText=findViewById(R.id.editPassword);
        update_btn=findViewById(R.id.updateButton);
    }
}
