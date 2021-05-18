package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.newproj.models.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class UserProfileActivity extends AppCompatActivity {
    private TextView userName,userAge,userDogName,userDogType,userAddress;
    private ImageView userImage,addFriend;
    StorageReference storageRef;
    private FirebaseFirestore db;
    StorageReference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = findViewById(R.id.friendName);
        userAge = findViewById(R.id.friendAge);
        userDogName = findViewById(R.id.friendDogName);
        userDogType = findViewById(R.id.friendDogType);
        userAddress = findViewById(R.id.friendAddress);
        userImage = findViewById(R.id.friendImage);
        addFriend = findViewById(R.id.add_friend);
        userName.setText(getIntent().getExtras().getString("name") + " " + getIntent().getExtras().getString("lastName"));
        userAge.setText(getIntent().getExtras().getString("age"));
        userDogName.setText(getIntent().getExtras().getString("dogName"));
        userDogType.setText(getIntent().getExtras().getString("dogType"));
        userAddress.setText(getIntent().getExtras().getString("address"));
        if(isFriend(getIntent().getExtras().getBoolean("isFriend"))){
            addFriend.setVisibility(View.INVISIBLE);
            addFriend.setEnabled(false);
        }
        else{
            addFriend.setVisibility(View.VISIBLE);
            addFriend.setEnabled(true);
        }
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        pref = storageRef.child(getIntent().getExtras().getString("image"));
        Glide.with(this)
                .load(pref)
                .into(userImage);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                builder.setMessage("האם לשלוח בקשת חברות?").setTitle("בקשת חברות");
                builder.setPositiveButton("שלח", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DocumentReference user = db.collection("users").document(getIntent().getExtras().getString("email"));
                        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot doc = task.getResult();
                                    ArrayList<String> requestList = (ArrayList<String>) doc.get("Requests");
                                    if(!isRequested(requestList))
                                        requestList.add(CurrentUser.currentUserEmail);
                                    HashMap<String,Object> requests = new HashMap<>();
                                    requests.put("Requests",requestList);
                                    db.collection("users").document(getIntent().getExtras().getString("email")).update(requests).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(UserProfileActivity.this,"הבקשה נשלחה בהצלחה",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });

                    }
                });
                builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    //check if email is already exist in user's request list
    public boolean isRequested(ArrayList<String> requestList) {
        boolean bool=requestList.contains(CurrentUser.currentUserEmail);
        return bool;
    }

    public boolean isFriend(boolean Friend){
        return Friend;
    }
}
