package com.example.newproj.models;

public class Parks {
    String Name;
    String Area;
    String Length;
    String Image;

    public Parks(String name,String area, String length){
        this.Name=name;
        this.Area=area;
        this.Length=length;
    }

    public Parks(){}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getLength() {
        return Length;
    }

    public void setLength(String length) {
        Length = length;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}


