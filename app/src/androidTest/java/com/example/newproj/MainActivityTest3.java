package com.example.newproj;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

//test for login screen to check that password email and button are not null
public class MainActivityTest3 {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    public MainActivity mainActivity;
    @Before
    public void setUp() throws Exception {
        mainActivity=mActivityTestRule.getActivity();
    }
    @Test
    public void name() {
        View email=mainActivity.findViewById(R.id.EmailText);
        assertNotNull(email);

        View password=mainActivity.findViewById(R.id.PasswordText);
        assertNotNull(password);

        View loginbutton=mainActivity.findViewById(R.id.login_btn);
        assertNotNull(loginbutton);
    }
    @After
    public void tearDown() throws Exception {
    }


}