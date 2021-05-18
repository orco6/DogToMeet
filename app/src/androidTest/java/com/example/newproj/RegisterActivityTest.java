package com.example.newproj;

import android.view.View;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

//Test of register screen that checks that all fields are not null
public class RegisterActivityTest {
    @Rule
    public ActivityTestRule<RegisterActivity> mActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);
    public RegisterActivity regActivity;
    @Before
    public void setUp() throws Exception {
        regActivity=mActivityTestRule.getActivity();
    }

    @Test
    public void name() {

        View regName=regActivity.findViewById(R.id.regName);
        View regLastName=regActivity.findViewById(R.id.regLastName);
        View regAge=regActivity.findViewById(R.id.regAge);
        View regEmail=regActivity.findViewById(R.id.regEmail);
        View regPassword=regActivity.findViewById(R.id.regPassword);
        View regButton=regActivity.findViewById(R.id.reg_btn);
        View regBackToLogin=regActivity.findViewById(R.id.login_label);
        TextView regAdderss=regActivity.findViewById(R.id.regAddress);

        assertNotNull(regName);
        assertNotNull(regLastName);
        assertNotNull(regEmail);
        assertNotNull(regAge);
        assertNotNull(regPassword);
        assertNotNull(regButton);
        assertNotNull(regBackToLogin);
        assertNotNull(regAdderss);
    }

    @After
    public void tearDown() throws Exception {
    }


}