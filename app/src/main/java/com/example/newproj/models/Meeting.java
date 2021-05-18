package com.example.newproj.models;

import com.example.newproj.UserScreenActivity;

import java.util.ArrayList;
import java.util.List;

public class Meeting {


    private String Location;
    private String Date;
    private String Hour;
    private String DogType;
    private String Discription;
    private String Owner;
    private List<String> Participants;
    private String ParkImage,UserImage;
    private String ID;

    public Meeting(){}

    public Meeting(String location, String date, String hour, String dogType, String discription, String owner,String parkImage,String userImage) {
        Location = location;
        Date = date;
        Hour = hour;
        DogType = dogType;
        Discription = discription;
        Owner = owner;
        Participants = new ArrayList<String>();
        ParkImage = parkImage;
        UserImage = userImage;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setHour(String hour) {
        Hour = hour;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setDogType(String dogType) {
        DogType = dogType;
    }

    public void setDiscription(String discription) {
        Discription = discription;
    }

    public String getLocation() {
        return Location;
    }

    public String getDate() {
        return Date;
    }

    public String getHour() {
        return Hour;
    }

    public String getDogType() {
        return DogType;
    }

    public String getDiscription() {
        return Discription;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public List<String> getParticipants() {
        return Participants;
    }

    public String getParkImage() {
        return ParkImage;
    }

    public void setParkImage(String parkImage) {
        ParkImage = parkImage;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public void setParticipants(List<String> participants) {
        Participants = participants;
    }
}
