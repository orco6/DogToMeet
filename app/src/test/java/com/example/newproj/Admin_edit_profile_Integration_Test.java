package com.example.newproj;
import android.content.Intent;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import static org.robolectric.Shadows.shadowOf;
import static org.testng.Assert.assertEquals;
@Config(sdk = 21)
@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class Admin_edit_profile_Integration_Test {


    @Test
    public void put_ditails_of_user_in_fields_test() {
        MainActivity main = Robolectric.buildActivity(MainActivity.class).create().get();
        ((TextView)main.findViewById(R.id.EmailText)).setText("admin@gmail.com");
        ((TextView)main.findViewById(R.id.PasswordText)).setText("123456");
        main.setLoginUser(main.emailText.getText().toString(),main.passwordText.getText().toString(),"admin");
        ((Button)main.findViewById(R.id.login_btn)).performClick();
        main.CheckUserDetails();
        shadowOf(Looper.getMainLooper()).idle();
        assertEquals(main.isFinishing(),true);
        AdminEditUserProfileActivity activity= Robolectric.buildActivity(AdminEditUserProfileActivity.class).create().get();
        Intent intent=new Intent(activity,AdminToUserProfileActivity.class);
        ((EditText)activity.findViewById(R.id.editFirstName)).setText("Liel");
        try {
            activity.putextras(intent);
        }
        catch(Exception e){

        }
        assertEquals(intent.getExtras().getString("name").toString(),"Liel");
    }

    @Test
    public void get_new_ditails_after_changed_test() {
        AdminEditUserProfileActivity activity= Robolectric.buildActivity(AdminEditUserProfileActivity.class).create().get();
        activity.getIntent().putExtra("name","Liel");
        activity.setTextsToButtons();
        assertEquals(activity.firstNameText.getText().toString(),"Liel");
    }
}
