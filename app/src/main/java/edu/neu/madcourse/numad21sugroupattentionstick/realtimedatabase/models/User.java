package edu.neu.madcourse.numad21sugroupattentionstick.realtimedatabase.models;

import edu.neu.madcourse.numad21sugroupattentionstick.utils.Utils;

/**
 * Created by aniru on 2/18/2017.
 */

public class User {

    public String username;
    public String score;
    public String datePlayed;
    public String senders;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String score, String senders) {
        this.username = username;
        this.score = score;
        this.datePlayed = Utils.date();
        this.senders = senders;
    }

    /*
    public User(String username, String score, String date) {
        this.username = username;
        this.score = score;
        this.datePlayed = date;
    }
    */
}
