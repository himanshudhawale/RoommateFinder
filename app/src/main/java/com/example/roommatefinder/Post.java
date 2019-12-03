package com.example.roommatefinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Post  {

    public String pid, gender, date, city, additional, time, userID;

    public List<String> imageList = new ArrayList<>();

    @Override
    public String toString() {
        return "Post{" +
                "pid='" + pid + '\'' +
                ", gender='" + gender + '\'' +
                ", date='" + date + '\'' +
                ", city='" + city + '\'' +
                ", additional='" + additional + '\'' +
                ", time='" + time + '\'' +
                ", userID='" + userID + '\'' +
                ", imageList=" + imageList +
                '}';
    }

    public Post(String pid, String gender, String date, String city, String additional, String time, String userID, List<String> imageList) {
        this.pid = pid;
        this.gender = gender;
        this.date = date;
        this.city = city;
        this.additional = additional;
        this.time = time;
        this.userID = userID;
        this.imageList = imageList;
    }

    public Post(){

    }
}
