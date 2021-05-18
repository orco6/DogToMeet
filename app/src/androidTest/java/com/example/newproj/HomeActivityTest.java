package com.example.newproj;

import androidx.test.rule.ActivityTestRule;

import com.example.newproj.models.CurrentUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class HomeActivityTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityTestRule = new ActivityTestRule<>(HomeActivity.class);

    HomeActivity home;

    @Before
    public void setUp() throws Exception {
        CurrentUser.currentUserEmail = "nadav@gmail.com";
    }

    @Test
    public void logOut() {
        home = mActivityTestRule.getActivity();
        home.logOut();
        assertEquals(true,home.isFinishing());
    }

    @After
    public void tearDown() throws Exception {
    }


}