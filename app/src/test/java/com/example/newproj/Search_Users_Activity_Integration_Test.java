package com.example.newproj;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.newproj.models.Users;
import com.google.firebase.firestore.auth.User;

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
public class Search_Users_Activity_Integration_Test {
    @Test
    public void Search_Users_by_Name_Test() {
        MainActivity main = Robolectric.buildActivity(MainActivity.class).create().get();
        ((TextView)main.findViewById(R.id.EmailText)).setText("nadav@gmail.com");
        ((TextView)main.findViewById(R.id.PasswordText)).setText("123456");
        main.setLoginUser(main.emailText.getText().toString(),main.passwordText.getText().toString(),"user");
        ((Button)main.findViewById(R.id.login_btn)).performClick();
        main.CheckUserDetails();
        shadowOf(Looper.getMainLooper()).idle();
        assertEquals(main.isFinishing(),true);
        SearchUsersActivity activity= Robolectric.buildActivity(SearchUsersActivity.class).create().get();
        ((TextView)activity.findViewById(R.id.search_text)).setText("Liel");
        ArrayList<Users> userlist=new ArrayList<>();
        Users user1=new Users();
        Users user2=new Users();
        user1.setName("Liel");
        user1.setLastName("san");
        user2.setName("Nadav");
        user2.setLastName("co");
        userlist.add(user1);
        userlist.add(user2);
        activity.setUsersList(userlist);
        int index=((RadioButton)activity.findViewById(R.id.owner_name)).getId();
        ((RadioGroup)activity.findViewById(R.id.search_options)).check(index);
        ((ImageButton)activity.findViewById(R.id.search_btn)).performClick();
        assertEquals(activity.getResults().get(0).getName().toString(),user1.getName().toString());

    }
    @Test
    public void Search_Users_by_dog_type_Test() {
        MainActivity main = Robolectric.buildActivity(MainActivity.class).create().get();
        ((TextView)main.findViewById(R.id.EmailText)).setText("nadav@gmail.com");
        ((TextView)main.findViewById(R.id.PasswordText)).setText("123456");
        main.setLoginUser(main.emailText.getText().toString(),main.passwordText.getText().toString(),"user");
        ((Button)main.findViewById(R.id.login_btn)).performClick();
        main.CheckUserDetails();
        shadowOf(Looper.getMainLooper()).idle();
        assertEquals(main.isFinishing(),true);
        SearchUsersActivity activity= Robolectric.buildActivity(SearchUsersActivity.class).create().get();
        ((TextView)activity.findViewById(R.id.search_text)).setText("shimi");
        ArrayList<Users> userlist=new ArrayList<>();
        Users user1=new Users();
        Users user2=new Users();
        user1.setName("Liel");
        user1.setDogType("shimi");
        user2.setName("Nadav");
        user2.setDogType("shim2");
        userlist.add(user1);
        userlist.add(user2);
        activity.setUsersList(userlist);
        int index=((RadioButton)activity.findViewById(R.id.dog_type)).getId();
        ((RadioGroup)activity.findViewById(R.id.search_options)).check(index);
        ((ImageButton)activity.findViewById(R.id.search_btn)).performClick();
        assertEquals(activity.getResults().get(0).getName().toString(),user1.getName().toString());

    }
}
