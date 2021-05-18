package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.test.rule.ActivityTestRule;

import com.example.newproj.models.Parks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ParksActivityTest {

    ArrayList<Parks> parksList;

    @Rule
    public ActivityTestRule<ParksActivity> mActivityTestRule = new ActivityTestRule<>(ParksActivity.class);

    public ParksActivity parksActivity;

    @Before
    public void setUp() throws Exception {
        parksActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void numberOfParks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                    assertEquals(parksList.size(),9);
                }
            }
        });

    }

    @After
    public void tearDown() throws Exception {
    }


}