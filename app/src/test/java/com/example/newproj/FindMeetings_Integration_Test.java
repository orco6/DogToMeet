package com.example.newproj;
import android.content.Intent;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.newproj.models.Meeting;
import com.example.newproj.models.Users;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;

import static org.robolectric.Shadows.shadowOf;
import static org.testng.Assert.assertEquals;
@Config(sdk = 21)
@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class FindMeetings_Integration_Test {

    @Test
    public void Search_Meetings_Test() {
        MainActivity main = Robolectric.buildActivity(MainActivity.class).create().get();
        ((TextView)main.findViewById(R.id.EmailText)).setText("nadav@gmail.com");
        ((TextView)main.findViewById(R.id.PasswordText)).setText("123456");
        main.setLoginUser(main.emailText.getText().toString(),main.passwordText.getText().toString(),"user");
        ((Button)main.findViewById(R.id.login_btn)).performClick();
        main.CheckUserDetails();
        shadowOf(Looper.getMainLooper()).idle();
        assertEquals(main.isFinishing(),true);
        FindMeetingsActivity activity= Robolectric.buildActivity(FindMeetingsActivity.class).create().get();
        activity.parksNames.add("פארק ה");
        activity.setParksSpinner();
        ((Spinner)activity.findViewById(R.id.choose_park)).setSelection(1);
        ((Spinner)activity.findViewById(R.id.choose_date)).setSelection(0);
        ((Spinner)activity.findViewById(R.id.choose_dog)).setSelection(4);
        ArrayList<Meeting> meetList = new ArrayList<Meeting>();
        Meeting meet1 = new Meeting();
        meet1.setDate("20/08/2021");
        meet1.setLocation("גן ארגנטינה");
        meet1.setDogType("ביגל");
        Meeting meet2 = new Meeting();
        meet2.setDate("22/08/2021");
        meet2.setLocation("פארק ה");
        meet2.setDogType("אמסטף");
        Meeting meet3 = new Meeting();
        meet3.setDate("28/08/2021");
        meet3.setLocation("פארק ה");
        meet3.setDogType("ביגל");
        activity.meetingsList.add(meet1);
        activity.meetingsList.add(meet2);
        activity.meetingsList.add(meet3);
        ((Button)activity.findViewById(R.id.search_meeting)).performClick();
        assertEquals(activity.result.get(0).getDate(),meet3.getDate());
    }
}
