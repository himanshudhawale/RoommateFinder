package com.example.roommatefinder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Messages implements Serializable {

    public String message, userName, timeStamp, userID, messageID, url, postID;
//    public int likes, dislikes;



    public Messages(String message, String userName, String timeStamp, String userID, String messageID, String url, String postID) {
        this.message = message;
        this.userName = userName;
        this.timeStamp = timeStamp;
        this.userID=userID;
        this.messageID=messageID;
        this.url=url;
        this.postID = postID;
    }

    public Messages(String message, String userName, String timeStamp, String userID, String messageID, String url) {
        this.message = message;
        this.userName = userName;
        this.timeStamp = timeStamp;
        this.userID=userID;
        this.messageID=messageID;
        this.url=url;
    }





    public Messages()
    {

    }


}