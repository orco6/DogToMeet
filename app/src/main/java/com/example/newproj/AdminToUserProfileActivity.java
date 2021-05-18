package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminToUserProfileActivity extends AppCompatActivity {
    private TextView userName,userAge,userDogName,userDogType,userAddress,userEmail;
    private ImageView userImage,editProfile,deleteUser;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private StorageReference pref;
    public ArrayList<String> participantsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_to_user_profile);
        userName=findViewById(R.id.userName);
        userEmail=findViewById(R.id.profileEmail);
        userAddress=findViewById(R.id.myAddress);
        userAge=findViewById(R.id.profileAge);
        userDogName=findViewById(R.id.dogName);
        userDogType=findViewById(R.id.typeName);
        userImage=findViewById(R.id.userImage);
        editProfile=findViewById(R.id.editProfile);
        deleteUser=findViewById(R.id.deleteUser);
        participantsList=new ArrayList<String>();

        userName.setText(getIntent().getExtras().getString("name") + " " + getIntent().getExtras().getString("lastName"));
        userAge.setText(getIntent().getExtras().getString("age"));
        userDogName.setText(getIntent().getExtras().getString("dogName"));
        userDogType.setText(getIntent().getExtras().getString("dogType"));
        userAddress.setText(getIntent().getExtras().getString("address"));
        userEmail.setText(getIntent().getExtras().getString("email"));

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        pref = storageRef.child(getIntent().getExtras().getString("image"));
        Glide.with(this)
                .load(pref)
                .into(userImage);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePicture();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToEditScreen();
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(AdminToUserProfileActivity.this);
                builder.setMessage("האם אתה בטוח שאתה רוצה למחוק את המשתמש"+" "+userName.getText().toString()+"?").setTitle("מחיקה");
                builder.setPositiveButton("מחק", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeUser();
                    }
                });
                builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void removePicture() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(AdminToUserProfileActivity.this);
        builder.setMessage("האם אתה בטוח שאתה רוצה למחוק את התמונה של המשתמש").setTitle("מחיקת תמונה");
        builder.setPositiveButton("מחק", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePicture();

            }
        });
        builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //delete the user's picture from database
    private void deletePicture() {
        StorageReference storageReference=FirebaseStorage.getInstance().getReference();
        StorageReference delete = storageReference.child(getIntent().getExtras().getString("image"));
        delete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                WriteBatch batch = db.batch();
                DocumentReference sfRef= db.collection("users").document(getIntent().getExtras().getString("email"));
                batch.update(sfRef, "Image","empty_profile.png");
                batch.commit();
                Toast.makeText(AdminToUserProfileActivity.this,"תמונה נמחקה בהצלחה",Toast.LENGTH_LONG).show();
                pref = storageRef.child("empty_profile.png");
                Glide.with(AdminToUserProfileActivity.this)
                        .load(pref)
                        .into(userImage);
                LoadProfileScreen();
            }
        });
    }

    private void LoadProfileScreen(){
        Intent intent = new Intent(AdminToUserProfileActivity.this, AdminToUserProfileActivity.class);
        intent.putExtra("name",getIntent().getExtras().getString("name"));
        intent.putExtra("lastName",getIntent().getExtras().getString("lastName"));
        intent.putExtra("address",getIntent().getExtras().getString("address"));
        intent.putExtra("dogName",getIntent().getExtras().getString("dogName"));
        intent.putExtra("dogType",getIntent().getExtras().getString("dogType"));
        intent.putExtra("age",getIntent().getExtras().getString("age"));
        intent.putExtra("email",getIntent().getExtras().getString("email"));
        intent.putExtra("image","empty_profile.png");
        intent.putExtra("password",getIntent().getExtras().getString("password"));
        startActivity(intent);
        finish();
    }

    private void goToEditScreen() {
        Intent intent = new Intent(AdminToUserProfileActivity.this,AdminEditUserProfileActivity.class);
        intent.putExtra("name",getIntent().getExtras().getString("name"));
        intent.putExtra("lastName",getIntent().getExtras().getString("lastName"));
        intent.putExtra("address",getIntent().getExtras().getString("address"));
        intent.putExtra("dogName",getIntent().getExtras().getString("dogName"));
        intent.putExtra("dogType",getIntent().getExtras().getString("dogType"));
        intent.putExtra("age",getIntent().getExtras().getString("age"));
        intent.putExtra("email",getIntent().getExtras().getString("email"));
        intent.putExtra("image",getIntent().getExtras().getString("image"));
        intent.putExtra("password",getIntent().getExtras().getString("password"));
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AdminToUserProfileActivity.this, AdminAllUsers.class);
        startActivity(intent);
        finish();
    }

    public boolean isParticipate(String email){
        return participantsList.contains(email);
    }

    private void removeUser() {
        db.collection("users").document(getIntent().getExtras().getString("email")).delete();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()) {
                        if (!(doc.get("UserType").toString().equals("admin") || doc.get("Email").toString().equals(CurrentUser.currentUserEmail))) {
                            Users user1 = new Users();
                            user1.setEmail(doc.get("Email").toString());
                            user1.setFriends((List<String>)(doc.get("Friends")));
                            user1.getFriends().remove(getIntent().getExtras().getString("email"));

                            WriteBatch batch = db.batch();
                            DocumentReference sfRef= db.collection("users").document(user1.getEmail());
                            batch.update(sfRef, "Friends", user1.getFriends());
                            batch.commit();
                        }
                    }
                    db.collectionGroup("meetings").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        participantsList = (ArrayList<String>) doc.get("Participants");
                                        if (isParticipate(getIntent().getExtras().getString("email"))){
                                            participantsList.remove(getIntent().getExtras().getString("email"));
                                            WriteBatch batch = db.batch();
                                            String key=doc.getId();
                                            DocumentReference laRef = db.collection("meetings").document(doc.getId());
                                            batch.update(laRef,"Participants",participantsList);
                                            batch.commit();
                                        }

                                        if(doc.get("Owner").toString().equals(getIntent().getExtras().getString("email"))) {
                                            WriteBatch batch = db.batch();
                                            String key=doc.getId();
                                            DocumentReference laRef = db.collection("meetings").document(doc.getId());
                                            batch.delete(laRef);
                                            batch.commit();
                                        }
                                    }
                                    StorageReference storageReference=FirebaseStorage.getInstance().getReference();
                                    StorageReference delete = storageReference.child(getIntent().getExtras().getString("image"));
                                    delete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(AdminToUserProfileActivity.this,"משתמש נמחק בהצלחה",Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(AdminToUserProfileActivity.this, AdminAllUsers.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                }

                            });


                }
            }
        });


    }


}
