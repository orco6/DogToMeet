package com.example.newproj.models;

import com.google.firebase.firestore.auth.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Users {
    private String Name;
    private String LastName;
    private String Age;
    private String Email;
    private String UserType;
    private String Address;
    private String Password;
    private String Image;
    private String DogType;
    private String DogName;

    private List<String> Friends;
    private List<String> Requests;

    public Users(){}

    public Users(Users usr){
        this.Name=usr.Name;
        this.LastName=usr.LastName;
        this.Age=usr.Age;
        this.Email=usr.Email;
        this.UserType=usr.UserType;
    }

    public Users(String name,String lastName,String age ,String email,String password,String address){
        this.Name=name;
        this.LastName=lastName;
        this.Age=age;
        this.Email=email;
        this.UserType="user";
        this.Password=password;
        this.Address=address;
        this.DogName="";
        this.DogType="";
        this.Image="empty_profile.png";
        this.Friends=new ArrayList<String>();
        this.Requests=new ArrayList<String>();
    }

    public Users(String name,String lastName,String age ,String email,String password,String address,String dog_name,String dog_type,String image,ArrayList<String> friends,ArrayList<String> requests){
        this.Name=name;
        this.LastName=lastName;
        this.Age=age;
        this.Email=email;
        this.UserType="user";
        this.Password=password;
        this.Address=address;
        this.DogName=dog_name;
        this.DogType=dog_type;
        this.Image=image;
        this.Friends = (ArrayList<String>) friends.clone();
        this.Requests=(ArrayList<String>) requests.clone();
    }

    public void setFriends(List<String> friends) {
        Friends = friends;
    }

    public String getPassword() { return Password; }

    public void setPassword(String password) { Password = password; }

    public String getName() {
        return Name;
    }

    public String getLastName() {
        return LastName;
    }

    public String getAge() {
        return Age;
    }

    public List<String> getRequests() {
        return Requests;
    }

    public void setRequests(List<String> requests) {
        Requests = requests;
    }

    public String getEmail() {
        return Email;
    }

    public String getUserType() {
        return UserType;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setAge(String age) {
        Age = age;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setType(String type) {
        UserType = type;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) { Address = address; }

    public String getImage() {
        return Image;
    }

    public String getDogType() {
        return DogType;
    }

    public String getDogName() {
        return DogName;
    }
    public void setUserType(String userType) {
        UserType = userType;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setDogType(String dogType) {
        DogType = dogType;
    }
    public void setDogName(String dogName) {
        DogName = dogName;
    }

    public List<String> getFriends() {
        return Friends;
    }
}
