package com.example.roommatefinder;
import java.io.Serializable;

public class User implements Serializable {

    public String first, last, email, uID, password, gender, age, city, imageURL;
    public String status = "none";
    public Double latitude,longitude;

    public User(String uID, String first, String last, String email, String password, String gender,String city, String age, String image, String status) {
        this.first = first;
        this.last = last;
        this.email = email;
        this.uID = uID;
        this.password = password;
        this.city = city;
        this.gender = gender;
        this.age = age;
        this.imageURL=image;
        this.status = status;
    }

    public User()
    {

    }

    public User(String uID, String first, String last, String email, String password, String gender,String city, String age, String image, String status,Double latitude,Double longitude) {
        this.first = first;
        this.last = last;
        this.email = email;
        this.uID = uID;
        this.password = password;
        this.city = city;
        this.gender = gender;
        this.imageURL=image;
        this.age = age;
        this.status = status;
        this.latitude=latitude;
        this.longitude=longitude;
    }

}