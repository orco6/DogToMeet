package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText firstNameText,lastNameText,addressText,dogNameText,dogTypeText,ageText;
    private Button update_btn;
    private FirebaseFirestore db;
    DocumentReference user;
    private Users usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firstNameText = findViewById(R.id.editFirstName);
        lastNameText = findViewById(R.id.editLastName);
        ageText = findViewById(R.id.editAge);
        dogNameText = findViewById(R.id.editDogName);
        dogTypeText = findViewById(R.id.editDogType);
        addressText = findViewById(R.id.editAdress);
        update_btn = findViewById(R.id.updateButton);
        db = FirebaseFirestore.getInstance();

        user = db.collection("users").document(CurrentUser.currentUserEmail);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    firstNameText.setText(doc.get("Name").toString());
                    lastNameText.setText(doc.get("LastName").toString());
                    ageText.setText(doc.get("Age").toString());
                    dogNameText.setText(doc.get("DogName").toString());
                    dogTypeText.setText(doc.get("DogType").toString());
                    addressText.setText(doc.get("Address").toString());
                    ArrayList<String> friends = (ArrayList<String>) doc.get("Friends");
                    ArrayList<String> requests = (ArrayList<String>) doc.get("Requests");
                    usr = new Users(firstNameText.getText().toString(),lastNameText.getText().toString(),ageText.getText().toString(),doc.get("Email").toString(),doc.get("Password").toString()
                            ,addressText.getText().toString(),dogNameText.getText().toString(),dogTypeText.getText().toString(),doc.get("Image").toString(),friends,requests);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateValidation(firstNameText.getText().toString(), lastNameText.getText().toString(), ageText.getText().toString(), addressText.getText().toString(), dogNameText.getText().toString(), dogTypeText.getText().toString())) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("Name", firstNameText.getText().toString());
                    user.put("LastName", lastNameText.getText().toString());
                    user.put("Age", ageText.getText().toString());
                    user.put("Email", usr.getEmail());
                    user.put("Password", usr.getPassword());
                    user.put("UserType", usr.getUserType());
                    user.put("DogName", dogNameText.getText().toString());
                    user.put("DogType", dogTypeText.getText().toString());
                    user.put("Image", usr.getImage());
                    user.put("Friends", usr.getFriends());
                    user.put("Address", addressText.getText().toString());
                    user.put("Requests",usr.getRequests());

                    db.collection("users")
                            .document(usr.getEmail()).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EditProfileActivity.this, "הפרטים עודכנו בהצלחה", Toast.LENGTH_SHORT).show();
                                    CurrentUser.dogType=dogTypeText.getText().toString();
                                    goBackToUserScreen();
                                }
                            });
                }
            }
        });
    }

    public boolean updateValidation(String name,String lastname,String age,String address,String dogname,String dogtype){
        String namePattern="[a-zA-Z\u0590-\u05fe]+";
        boolean flag = true;
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

        //dog name field validation
        if (!dogname.isEmpty()){
            if(!(dogname.trim().matches(namePattern))){
                if(dogNameText!=null)
                    dogNameText.setError("Dog-Name field letters Must A-Z or a-z!");
                flag=false;
            }
            if(dogname.length()<2) {
                if(dogNameText!=null)
                    dogNameText.setError("Dog-Name field must be at least 2 letters!");
                flag=false;
            }
        }

        //dog type field validation
        if (!dogtype.isEmpty()){
            if(!(dogtype.trim().matches(namePattern))){
                if(dogTypeText!=null)
                    dogTypeText.setError("Dog-Type field letters Must A-Z or a-z!");
                flag=false;
            }
            if(dogtype.length()<2) {
                if(dogTypeText!=null)
                    dogTypeText.setError("Dog-Type field must be at least 2 letters!");
                flag=false;
            }
        }


        return flag;

    }
    // אThe user back to userscreen
    private void goBackToUserScreen(){
        Intent intent = new Intent(this, UserScreenActivity.class);
        startActivity(intent);
        finish();
    }
}
