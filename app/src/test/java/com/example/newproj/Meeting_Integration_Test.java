package com.example.newproj;
import android.content.Intent;
import android.os.Looper;
import android.widget.Button;
import android.widget.ListView;
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
public class Meeting_Integration_Test {

    @Test
    public void perform_click_Meeting_Test() {
        MainActivity main = Robolectric.buildActivity(MainActivity.class).create().get();
        ((TextView)main.findViewById(R.id.EmailText)).setText("nadav@gmail.com");
        ((TextView)main.findViewById(R.id.PasswordText)).setText("123456");
        main.setLoginUser(main.emailText.getText().toString(),main.passwordText.getText().toString(),"user");
        ((Button)main.findViewById(R.id.login_btn)).performClick();
        main.CheckUserDetails();
        shadowOf(Looper.getMainLooper()).idle();
        assertEquals(main.isFinishing(),true);
        UpcomingMeetingsActivity activity= Robolectric.buildActivity(UpcomingMeetingsActivity.class).create().get();
        ArrayList<Users> usersList = new ArrayList<Users>();
        ArrayList<Meeting> meetingsList=new ArrayList<Meeting>();;
        Users newuser1=new Users();
        newuser1.setName("liel1");
        newuser1.setEmail("liel@gmail.com");
        usersList.add(newuser1);
        activity.setUsersList(usersList);
        Meeting mt=new Meeting();
        mt.setLocation("Stam-park");
        meetingsList.add(mt);
        activity.fillList_adapter(meetingsList);
        try {
            ((ListView) activity.findViewById(R.id.meetings_listview)).performItemClick(activity.meetingsListView.getSelectedView(), 0, 0);
        }
        catch (Exception e){

        }
        assertEquals(activity.clickedMeeting.getLocation().toString(),"Stam-park");
    }

    @Test
    public void show_Meeting_ditails() {
        UpcomingMeetingsActivity activity= Robolectric.buildActivity(UpcomingMeetingsActivity.class).create().get();
        Intent intent=new Intent(activity,MeetingDetailsActivity.class);
        Meeting Clicked=new Meeting();
        Clicked.setLocation("park-stam");
        Clicked.setDate("20/04/2020");
        Clicked.setDiscription("");
        Clicked.setDogType("shimi");
        Clicked.setOwner("liel@gmail.com");
        Clicked.setHour("16:00");
        Clicked.setParkImage("");
        activity.clickedMeeting=Clicked;

        try {
            activity.putextras(intent);
        }
        catch (Exception e){

        }
        assertEquals(intent.getExtras().getString("owner"),"liel@gmail.com");
    }
}
