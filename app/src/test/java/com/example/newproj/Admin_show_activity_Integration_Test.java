package com.example.newproj;
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
public class Admin_show_activity_Integration_Test {
    @Test
    public void perform_click_to_show_user_Test() {
        MainActivity main = Robolectric.buildActivity(MainActivity.class).create().get();
        ((TextView)main.findViewById(R.id.EmailText)).setText("admin@gmail.com");
        ((TextView)main.findViewById(R.id.PasswordText)).setText("123456");
        main.setLoginUser(main.emailText.getText().toString(),main.passwordText.getText().toString(),"admin");
        ((Button)main.findViewById(R.id.login_btn)).performClick();
        main.CheckUserDetails();
        shadowOf(Looper.getMainLooper()).idle();
        assertEquals(main.isFinishing(),true);
        AdminAllUsers activity= Robolectric.buildActivity(AdminAllUsers.class).create().get();
        ArrayList<Users> usersList = new ArrayList<Users>();
        Users newuser1=new Users();
        newuser1.setName("liel1");
        newuser1.setEmail("liel@gmail.com");
        usersList.add(newuser1);
        activity.showResults(usersList);
        ((ListView)activity.findViewById(R.id.friends_listview)).performItemClick(activity.users.getSelectedView(),0,0);
        assertEquals(activity.user.getName(),"liel1");
    }
    @Test
    public void set_text_right() {
        AdminAllUsers activity= Robolectric.buildActivity(AdminAllUsers.class).create().get();
        assertEquals(((TextView)activity.findViewById(R.id.friends_label)).getText().toString(),"משתמשים");
    }
}
