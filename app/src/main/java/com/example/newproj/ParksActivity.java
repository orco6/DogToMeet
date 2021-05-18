package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.CustomAdapter;
import com.example.newproj.models.Parks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ParksActivity extends AppCompatActivity {
    private ListView parks;
    private FirebaseFirestore db;
    private ArrayList<Parks> parksList;

    @Override
    //get the parks from the database
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parks);
        parks = findViewById(R.id.park_listview);
        db = FirebaseFirestore.getInstance();
        parksList = new ArrayList<Parks>();
        CollectionReference user = db.collection("parks");
        user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task< QuerySnapshot > task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Parks park = new Parks();
                        park.setName(doc.get("Name").toString());
                        park.setArea(doc.get("ShapeArea").toString());
                        park.setLength(doc.get("ShapeLength").toString());
                        park.setImage(doc.get("Image").toString());
                        parksList.add(park);
                    }
                    fillList();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    //list of the parks
    private void fillList(){
        CustomAdapter arrayAdapter = new CustomAdapter(this, parksList);
        parks.setAdapter(arrayAdapter);
    }

    public int getNumberOfParks(){
        return parksList.size();
    }

    public void numberOfParks(){
        db = FirebaseFirestore.getInstance();
        parksList = new ArrayList<Parks>();
        CollectionReference user = db.collection("parks");
        user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task< QuerySnapshot > task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Parks park = new Parks();
                        park.setName(doc.get("Name").toString());
                        park.setArea(doc.get("ShapeArea").toString());
                        park.setLength(doc.get("ShapeLength").toString());
                        park.setImage(doc.get("Image").toString());
                        parksList.add(park);
                    }
                }
            }
        });

    }
}
