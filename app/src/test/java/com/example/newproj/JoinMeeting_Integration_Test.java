package com.example.newproj;
import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.Meeting;
import com.example.newproj.models.Users;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;

import static org.robolectric.Shadows.shadowOf;
import static org.testng.Assert.assertEquals;
@Config(sdk = 21)
@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class JoinMeeting_Integration_Test {

    @Test
    public void joinToMeeting_Test(){
        MainActivity main = Robolectric.buildActivity(MainActivity.class).create().get();
        ((TextView)main.findViewById(R.id.EmailText)).setText("nadav@gmail.com");
        ((TextView)main.findViewById(R.id.PasswordText)).setText("123456");
        main.setLoginUser(main.emailText.getText().toString(),main.passwordText.getText().toString(),"user");
        ((Button)main.findViewById(R.id.login_btn)).performClick();
        main.CheckUserDetails();
        shadowOf(Looper.getMainLooper()).idle();
        assertEquals(main.isFinishing(),true);
        CurrentUser.currentUserEmail = "nadav@gmail.com";
        CurrentUser.dogType = "ביגל";
        ArrayList<String> array = new ArrayList<String>();
        array.add("liel@gmail.com");
        Intent intent = new Intent();
        intent.putExtra("owner","liel@gmail.com");
        intent.putExtra("location","פארק ה");
        intent.putExtra("image","liel@gmail.com");
        intent.putExtra("participants",array);
        intent.putExtra("description","");
        intent.putExtra("isMember",false);
        intent.putExtra("isOwner",false);
        intent.putExtra("dogType","ביגל");
        intent.putExtra("activityscreen","Test");
        intent.putExtra("id","123");
        intent.putExtra("date","22/02/2222");
        intent.putExtra("hour","00:00");
        MeetingDetailsActivity meetingDetails = Robolectric.buildActivity(MeetingDetailsActivity.class,intent).create().get();
        meetingDetails.isMember = false;
        meetingDetails.isOwner = false;
        meetingDetails.participants.add("liel@gmail.com");
        meetingDetails.participants.add("aviram@gmail.com");
        meetingDetails.participants.add("or@gmail.com");
        ((Button)meetingDetails.findViewById(R.id.join_meeting_button)).performClick();
        //shadowOf(Looper.getMainLooper()).idle();
        assertEquals(meetingDetails.participants.contains("nadav@gmail.com"),true);
    }
}
