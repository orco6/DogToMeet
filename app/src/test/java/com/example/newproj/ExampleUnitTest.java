package com.example.newproj;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.Meeting;
import com.example.newproj.models.Parks;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    ArrayList<Parks> parksList;

    //Registeration-Tests
    @Test
    public void signUpValidation_nameLengthFaild() {
        RegisterActivity regActivity = new RegisterActivity();
        assertEquals(false,regActivity.signUpValidation("n","cohen","27","nadavc@gmail.com","123456"));
    }

    @Test
    public void signUpValidation_lastNamePatternFaild() {
        RegisterActivity regActivity = new RegisterActivity();
        assertEquals(false,regActivity.signUpValidation("nadav","123","27","nadavc@gmail.com","123456"));
    }

    @Test
    public void signUpValidation_ageEmptyFailed() {
        RegisterActivity regActivity = new RegisterActivity();
        assertEquals(false,regActivity.signUpValidation("nadav","cohen","","nadavc@gmail.com","123456"));
    }

    @Test
    public void signUpValidation_emailPatternFailed() {
        RegisterActivity regActivity = new RegisterActivity();
        assertEquals(false,regActivity.signUpValidation("nadav","cohen","","nadavc#gmail.com","123456"));
    }
    @Test
    public void signUpValidation_passwordLengthFailed() {
        RegisterActivity regActivity = new RegisterActivity();
        assertEquals(false,regActivity.signUpValidation("nadav","cohen","27","nadavc@gmail.com","1234"));
    }

    @Test
    public void signUpValidation_correct() {
        RegisterActivity regActivity = new RegisterActivity();
        assertEquals(true,regActivity.signUpValidation("nadav","cohen","27","nadavc@gmail.com","123456"));
    }

    //Edit-profile-Tests
    @Test
    public void updateValidation_correct() {
        EditProfileActivity editActivity = new EditProfileActivity();
        assertEquals(true,editActivity.updateValidation("nadav","cohen","27","beer sheva","dog","Husky"));
    }

    @Test
    public void updateValidation_namePatternFailed() {
        EditProfileActivity editActivity = new EditProfileActivity();
        assertEquals(false,editActivity.updateValidation("nad3av","cohen","27","beer sheva","dog","Husky"));
    }

    @Test
    public void updateValidation_lastNameLengthFailed() {
        EditProfileActivity editActivity = new EditProfileActivity();
        assertEquals(false,editActivity.updateValidation("nadav","c","27","beer sheva","dog","Husky"));
    }

    @Test
    public void updateValidation_ageEmptyFailed() {
        EditProfileActivity editActivity = new EditProfileActivity();
        assertEquals(false,editActivity.updateValidation("nadav","cohen","","beer sheva","dog","Husky"));
    }

    @Test
    public void updateValidation_dogNamePatternFailed() {
        EditProfileActivity editActivity = new EditProfileActivity();
        assertEquals(false,editActivity.updateValidation("nadav","cohen","27","beer sheva","choco777","Husky"));
    }

    @Test
    public void updateValidation_dogTypeLengthFailed() {
        EditProfileActivity editActivity = new EditProfileActivity();
        assertEquals(false,editActivity.updateValidation("nadav","cohen","27","beer sheva","dog","A"));
    }

    //login-Tests
    @Test
    public void loginValidation_emptyEmail() {
        MainActivity main = new MainActivity();
        assertEquals(false,main.existInput("","123456"));
    }
    @Test
    public void loginValidation_emptyPassword() {
        MainActivity main = new MainActivity();
        assertEquals(false,main.existInput("nadav@gmail.com",""));
    }

    //search-friends-Tests
    @Test
    public void isFriends_Pass(){
        SearchUsersActivity search = new SearchUsersActivity();
        CurrentUser.currentUserFriends = new ArrayList<String>();
        CurrentUser.currentUserFriends.add("liel@gmail.com");
        CurrentUser.currentUserFriends.add("or@gmail.com");
        assertEquals(true,search.isFriend("liel@gmail.com"));
    }

    @Test
    public void isFriendsValidation(){
        SearchUsersActivity search = new SearchUsersActivity();
        CurrentUser.currentUserFriends = new ArrayList<String>();
        CurrentUser.currentUserFriends.add("liel@gmail.com");
        CurrentUser.currentUserFriends.add("or@gmail.com");
        assertEquals(false,search.isFriend("aviram@gmail.com"));
    }

    //CreateMeetingTests
    @Test
    public void getTimeForamt_pass(){
        String time="9";
        CreateMeetingActivity createMeeting=new CreateMeetingActivity();
        time=createMeeting.getFixTimeForamt(time);
        assertEquals("09",time);
    }
    @Test
    public void getTimeFormat_fail(){
        String time="9";
        CreateMeetingActivity createMeeting=new CreateMeetingActivity();
        time=createMeeting.getFixTimeForamt(time);
        assertNotEquals("9",time);
    }

    @Test
    public void setDateTime_pass(){
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.YEAR, 2020);
        myCalendar.set(Calendar.MONTH, 11);
        myCalendar.set(Calendar.DAY_OF_MONTH, 1);
        CreateMeetingActivity createMeeting=new CreateMeetingActivity();
        String date=createMeeting.updateLabel(myCalendar);
        assertEquals("01/12/2020",date);
    }

    @Test
    public void setDateTime_fail(){
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.YEAR, 2020);
        myCalendar.set(Calendar.MONTH, 10);
        myCalendar.set(Calendar.DAY_OF_MONTH, 2);
        CreateMeetingActivity createMeeting=new CreateMeetingActivity();
        String date=createMeeting.updateLabel(myCalendar);
        assertNotEquals("01/12/2020",date);
    }

    //AddFriends Tests
    @Test
    public void isFriend_arefriends(){
        boolean isfriend=true;
        UserProfileActivity profileActivity=new UserProfileActivity();
        boolean result=profileActivity.isFriend(isfriend);
        assertEquals(true,result);
    }

    @Test
    public void isFriend_notfriend(){
        boolean isfriend=false;
        UserProfileActivity profileActivity=new UserProfileActivity();
        boolean result=profileActivity.isFriend(isfriend);
        assertEquals(false,result);
    }

    @Test
    public void isRequested_inRequset(){
        ArrayList<String> requestList=new ArrayList<String>();
        requestList.add("aviram@gmail.com");
        CurrentUser.currentUserEmail="aviram@gmail.com";
        UserProfileActivity userProfile=new UserProfileActivity();
        boolean isInArray = userProfile.isRequested(requestList);
        assertEquals(true,isInArray);
    }
    @Test
    public void isRequested_notinrequst(){
        ArrayList<String> requestList=new ArrayList<String>();
        requestList.add("aviram@gmail.com");
        CurrentUser.currentUserEmail="or@gmail.com";
        UserProfileActivity userProfile=new UserProfileActivity();
        boolean isInArray = userProfile.isRequested(requestList);
        assertNotEquals(true,isInArray);
    }

    //myFriends Tests
    @Test
    public void fill_friend_Ditails_Test(){
        MyFriendsActivity myFriends=new MyFriendsActivity();
        Users newUser=myFriends.setUserFields("avi","kuku","avi@gmail.com","beer sheva","24","bolt","white-wolf","avi@gmail.com/img");
        assertEquals("avi",newUser.getName());
        assertEquals("kuku",newUser.getLastName());
        assertEquals("avi@gmail.com",newUser.getEmail());
        assertEquals("beer sheva",newUser.getAddress());
        assertEquals("24",newUser.getAge());
        assertEquals("bolt",newUser.getDogName());
        assertEquals("white-wolf",newUser.getDogType());
        assertEquals("avi@gmail.com/img",newUser.getImage());
    }

    //future meetings i created
    @Test
    public void iAmOwner_owner(){
        Meeting meeting = new Meeting("פארק ה", "04/05/2020", "18:00", "הכל", "", "nadav@gmail.com", "park_e.png","nadav@gmail.com/img");
        CurrentUser.currentUserEmail = "nadav@gmail.com";
        UpcomingMeetingsActivity upComing = new UpcomingMeetingsActivity();
        boolean result = upComing.iAmOwnerOfMeeting(meeting.getOwner(),CurrentUser.currentUserEmail);
        assertEquals(true,result);
    }

    @Test
    public void iAmOwner_notOwner(){
        Meeting meeting = new Meeting("פארק ה", "04/05/2020", "18:00", "הכל", "", "liel@gmail.com", "park_e.png","nadav@gmail.com/img");
        CurrentUser.currentUserEmail = "nadav@gmail.com";
        UpcomingMeetingsActivity upComing = new UpcomingMeetingsActivity();
        boolean result = upComing.iAmOwnerOfMeeting(meeting.getOwner(),CurrentUser.currentUserEmail);
        assertNotEquals(true,result);
    }

    //Admin - add new users
    @Test
    public void adminSignUp_emailPatternFailed() {
        AdminRegisterationActivity adminReg = new AdminRegisterationActivity();
        assertEquals(false,adminReg.ValidText("nadav","cohen","27","nadavc#gmail.com","123456"));
    }

    @Test
    public void adminSignUp_passwordLengthFailed() {
        AdminRegisterationActivity adminReg = new AdminRegisterationActivity();
        assertEquals(false,adminReg.ValidText("nadav","cohen","27","nadavc@gmail.com","123"));
    }

    //Admin - edit registered user profile
    @Test
    public void adminEditUser_firstNamePatternFailed() {
        AdminEditUserProfileActivity adminEdit = new AdminEditUserProfileActivity();
        assertEquals(false,adminEdit.editValidation("na3av","cohen","27","123456"));
    }

    @Test
    public void adminEditUser_ageEmptyFailed() {
        AdminEditUserProfileActivity adminEdit = new AdminEditUserProfileActivity();
        assertEquals(false,adminEdit.editValidation("nadav","cohen","","123456"));
    }

    @Test
    public void adminRemoveUser_isParticipate(){
        AdminToUserProfileActivity activity = new AdminToUserProfileActivity();
        ArrayList<String> list = new ArrayList<String>();
        list.add("nadav@gmail.com");
        activity.participantsList = list;
        assertEquals(activity.isParticipate("nadav@gmail.com"),true);
    }
    @Test
    public void adminRemoveUser_isNotParticipate(){
        AdminToUserProfileActivity activity = new AdminToUserProfileActivity();
        ArrayList<String> list = new ArrayList<String>();
        list.add("nadav@gmail.com");
        activity.participantsList = list;
        assertEquals(activity.isParticipate("liel@gmail.com"),false);
    }

    @Test
    public void adminAllUsers_isAdmin(){
        AdminAllUsers activity = new AdminAllUsers();
        assertEquals(activity.isAdmin("admin"),true);
    }

    @Test
    public void adminAllUsers_isNotAdmin(){
        AdminAllUsers activity = new AdminAllUsers();
        assertEquals(activity.isAdmin("user"),false);
    }

    //UdateMeetingTests
    @Test
    public void getTimeForamt_update_pass(){
        String time="7";
        CreateMeetingActivity createMeeting=new CreateMeetingActivity();
        time=createMeeting.getFixTimeForamt(time);
        assertEquals("07",time);
    }
    @Test
    public void getTimeFormat_update_fail(){
        String time="7";
        CreateMeetingActivity createMeeting=new CreateMeetingActivity();
        time=createMeeting.getFixTimeForamt(time);
        assertNotEquals("7",time);
    }

    @Test
    public void setDateTime_update_pass(){
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.YEAR, 2020);
        myCalendar.set(Calendar.MONTH,7);
        myCalendar.set(Calendar.DAY_OF_MONTH, 2);
        CreateMeetingActivity createMeeting=new CreateMeetingActivity();
        String date=createMeeting.updateLabel(myCalendar);
        assertEquals("02/08/2020",date);
    }

    @Test
    public void setDateTime_update_fail(){
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.YEAR, 2020);
        myCalendar.set(Calendar.MONTH, 8);
        myCalendar.set(Calendar.DAY_OF_MONTH, 2);
        CreateMeetingActivity createMeeting=new CreateMeetingActivity();
        String date=createMeeting.updateLabel(myCalendar);
        assertNotEquals("01/12/2020",date);
    }

    //Admin Edit Profile
    @Test
    public void adminUpdateValidation_namePatternFailed() {
        AdminEditProfileActivity editActivity = new AdminEditProfileActivity();
        assertEquals(false,editActivity.editValidation("nad3av","cohen","123456"));
    }

    @Test
    public void adminUpdateValidation_lastNameLengthFailed() {
        AdminEditProfileActivity editActivity = new AdminEditProfileActivity();
        assertEquals(false,editActivity.editValidation("nadav","c","123456"));
    }
    @Test
    public void adminUpdateValidation_passwordPatternFailed() {
        AdminEditProfileActivity editActivity = new AdminEditProfileActivity();
        assertEquals(false,editActivity.editValidation("nadav","cohen","123"));
    }

    //Find Meetings
    @Test
    public void isOwner_check(){
        FindMeetingsActivity searchActvity = new FindMeetingsActivity();
        CurrentUser.currentUserEmail = "nadavcohen@gmail.com";
        Meeting mt = new Meeting();
        mt.setOwner("nadavcohen@gmail.com");
        assertEquals(true,searchActvity.isOwner(mt));
    }

    @Test
    public void isOwner_checkFailed(){
        FindMeetingsActivity searchActvity = new FindMeetingsActivity();
        CurrentUser.currentUserEmail = "liel@gmail.com";
        Meeting mt = new Meeting();
        mt.setOwner("nadavcohen@gmail.com");
        assertNotEquals(true,searchActvity.isOwner(mt));
    }

    @Test
    public void isMember_check(){
        FindMeetingsActivity searchActvity = new FindMeetingsActivity();
        CurrentUser.currentUserEmail = "nadav@gmail.com";
        Meeting mt = new Meeting();
        mt.setOwner("nadav@gmail.com");
        ArrayList<String> members = new ArrayList<String>();
        members.add("nadav@gmail.com");
        members.add("liel@gmail.com");
        mt.setParticipants(members);
        assertEquals(true,searchActvity.isMember(mt));
    }

    @Test
    public void isMember_notMember_check(){
        FindMeetingsActivity searchActvity = new FindMeetingsActivity();
        CurrentUser.currentUserEmail = "nadav@gmail.com";
        Meeting mt = new Meeting();
        mt.setOwner("liel@gmail.com");
        ArrayList<String> members = new ArrayList<String>();
        members.add("or@gmail.com");
        members.add("liel@gmail.com");
        mt.setParticipants(members);
        assertEquals(false,searchActvity.isMember(mt));
    }

    //join to a meeting
    @Test
    public void checkJoinButton(){
        MeetingDetailsActivity joinActivity = new MeetingDetailsActivity();
        assertEquals("בטל הצטרפות",joinActivity.checkButtonText(true,false));
    }

    @Test
    public void checkLeaveButton(){
        MeetingDetailsActivity joinActivity = new MeetingDetailsActivity();
        assertEquals("הצטרף למפגש",joinActivity.checkButtonText(false,false));
    }

    @Test
    public void checkEditButton(){
        MeetingDetailsActivity joinActivity = new MeetingDetailsActivity();
        assertEquals("ערוך פגישה",joinActivity.checkButtonText(false,true));
    }

    //Admin history meetings
    @Test
    public void checkHistory_correct() throws ParseException {
        AdminMeetingsActivity history = new AdminMeetingsActivity();
        Meeting mt1 = new Meeting();
        mt1.setDate("25/05/2020");
        Meeting mt2 = new Meeting();
        mt2.setDate("20/05/2020");
        Meeting mt3 = new Meeting();
        mt3.setDate("10/05/2020");
        ArrayList<Meeting> list = new ArrayList<Meeting>();
        list.add(mt1);
        list.add(mt2);
        list.add(mt3);
        assertEquals(3,history.getAmountOfHistoryMeetings(list));
    }
    @Test
    public void checkHistory_correct2() throws ParseException {
        AdminMeetingsActivity history = new AdminMeetingsActivity();
        Meeting mt1 = new Meeting();
        mt1.setDate("25/05/2021");
        Meeting mt2 = new Meeting();
        mt2.setDate("20/06/2021");
        Meeting mt3 = new Meeting();
        mt3.setDate("29/05/2021");
        ArrayList<Meeting> list = new ArrayList<Meeting>();
        list.add(mt1);
        list.add(mt2);
        list.add(mt3);
        assertEquals(0,history.getAmountOfHistoryMeetings(list));
    }

    //Admin all Meetings
    @Test
    public void checkRelevant_correct() throws ParseException {
        AdminMeetingsActivity history = new AdminMeetingsActivity();
        Meeting mt1 = new Meeting();
        mt1.setDate("25/05/2021");
        Meeting mt2 = new Meeting();
        mt2.setDate("20/06/2021");
        Meeting mt3 = new Meeting();
        mt3.setDate("29/05/2021");
        ArrayList<Meeting> list = new ArrayList<Meeting>();
        list.add(mt1);
        list.add(mt2);
        list.add(mt3);
        assertEquals(3,history.getAmountOfRelevantMeetings(list));
    }
    //Admin Today's meetings
    @Test
    public void checkToday_correct() throws ParseException {
        AdminMeetingsActivity history = new AdminMeetingsActivity();
        Meeting mt1 = new Meeting();
        mt1.setDate("25/04/2020");
        Meeting mt2 = new Meeting();
        mt2.setDate("20/04/2020");
        Meeting mt3 = new Meeting();
        mt3.setDate("29/04/2020");
        ArrayList<Meeting> list = new ArrayList<Meeting>();
        list.add(mt1);
        list.add(mt2);
        list.add(mt3);
        assertEquals(0,history.getAmountOfTodayMeetings(list));
    }

    //view the participants list
    @Test
    public void checkAmountOfParticipants(){
        MeetingDetailsActivity details = new MeetingDetailsActivity();
        ArrayList<String> list = new ArrayList<String>();
        list.add("liel@gmail.com");
        list.add("nadav@gmail.com");
        assertEquals(2,details.getAmountOfMembers(list));
    }
    //Delete meeting
    @Test
    public void checkAllowDelete(){
        MeetingDetailsActivity details = new MeetingDetailsActivity();
        Users owner = new Users();
        owner.setEmail("nadav@gmail.com");
        CurrentUser.currentUserEmail="nadav@gmail.com";
        assertEquals(true,details.allowDelete(owner));
    }
    @Test
    public void checkNotAllowDelete(){
        MeetingDetailsActivity details = new MeetingDetailsActivity();
        Users owner = new Users();
        owner.setEmail("nadav@gmail.com");
        CurrentUser.currentUserEmail="liel@gmail.com";
        assertEquals(false,details.allowDelete(owner));
    }
}