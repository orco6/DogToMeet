package com.example.newproj.models;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class SortByDate implements Comparator<Meeting> {
    @Override
    public int compare(Meeting o1, Meeting o2) {
        Date date1;
        Date date2;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(o1.getDate().toString());
            date2 = new SimpleDateFormat("dd/MM/yyyy").parse(o2.getDate().toString());
        }
        catch (Exception e){
            return 0;
        }
        return  date1.compareTo(date2);
    }
}
