package com.example.newproj;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
public class Friend_Activity_Integration_tests{
    private ArrayList<Users> usersList;
    @Test
    public void perform_click_on_friend_Test() {
        MainActivity main = Robolectric.buildActivity(MainActivity.class).create().get();
        ((TextView)main.findViewById(R.id.EmailText)).setText("nadav@gmail.com");
        ((TextView)main.findViewById(R.id.PasswordText)).setText("123456");
        main.setLoginUser(main.emailText.getText().toString(),main.passwordText.getText().toString(),"user");
        ((Button)main.findViewById(R.id.login_btn)).performClick();
        main.CheckUserDetails();
        assertEquals(main.isFinishing(),true);
        MyFriendsActivity activity=Robolectric.buildActivity(MyFriendsActivity.class).create().get();
        usersList = new ArrayList<Users>();
        Users newuser1=activity.setUserFields("liel1","san","liel@gmail.com","beer sheva","15","shimi","shimi","none");
        Users newuser2=activity.setUserFields("liel2","san","liel@gmail.com","beer sheva","15","shimi","shimi","none");
        Users newuser3=activity.setUserFields("liel3","san","liel@gmail.com","beer sheva","15","shimi","shimi","none");
        usersList.add(newuser1);
        usersList.add(newuser2);
        usersList.add(newuser3);
        activity.setUsersList(usersList);
        activity.fillList();
        ((ListView)activity.findViewById(R.id.friends_listview)).performItemClick(activity.friends.getSelectedView(),0,0);
        assertEquals(activity.friend.getName(),"liel1");

    }


    @Test
    public void messages_bewtween_activities_Test() {
        CurrentUser.currentUserEmail="nadav@gmail.com";
        MyFriendsActivity activity=Robolectric.buildActivity(MyFriendsActivity.class).create().get();
        Users newuser=activity.setUserFields("liel","san","liel@gmail.com","beer sheva","15","shimi","shimi","none");
        activity.setFriend(newuser);
        activity.showFriendProfile();
        assertEquals(activity.intent.getExtras().getString("name"),"liel");
    }
}
