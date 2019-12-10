package com.example.roommatefinder;

import java.io.Serializable;

public class Payment implements Serializable {
    public String postID, pid, secondUserID, firstUserID, status, amount;

    public Payment(String postID, String pid, String secondUserID, String firstUserID, String status, String amount) {
        this.postID = postID;
        this.pid = pid;
        this.secondUserID = secondUserID;
        this.firstUserID = firstUserID;
        this.status = status;
        this.amount = amount;
    }
    public Payment()
    {

    }
}
