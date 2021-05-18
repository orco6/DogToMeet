package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newproj.models.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class AdminProfileActivity extends AppCompatActivity {
    private TextView fullName,email,password;
    private ImageView editButton;
    private String first,last;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        fullName = findViewById(R.id.adminName);
        email = findViewById(R.id.profileAdminEmail);
        password = findViewById(R.id.profilePassword);
        editButton = findViewById(R.id.editProfileAdmin);

        db = FirebaseFirestore.getInstance();

        DocumentReference user = db.collection("users").document(CurrentUser.currentUserEmail);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    first = doc.get("Name").toString();
                    last = doc.get("LastName").toString();
                    fullName.setText(first + " " + last);
                    email.setText(doc.get("Email").toString());
                    password.setText(doc.get("Password").toString());
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });
    }

    private void goToEditProfile() {
        Intent intent = new Intent(this,AdminEditProfileActivity.class);
        intent.putExtra("firstName",first);
        intent.putExtra("lastName",last);
        intent.putExtra("password",password.getText().toString());
        finish();
        startActivity(intent);
    }
}
