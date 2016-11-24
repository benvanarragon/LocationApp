//http://mobilesiri.com/android-sqlite-database-tutorial-using-android-studio/

package com.vanarragon.ben.locationapp.Database;

import java.sql.Blob;

import java.sql.Date;

/**
 * Created by jamin on 2016-11-21.
 */

public class Location {



        private int id;
        private double Lat;
        private double Long;
        private String Action;
        private String DateTime;
        private String PrivacyLevel;
        private String SimpleLocation;
        private String email,name;
        private byte[] profilePic;

    //IF YOU ADD NEW COLUMNS, YOU MUST UN INSTALL THE APP AND RECREATE THE DATABASE



        public Location()
        {

        }
        public Location(double Lat,double Long, String Action, String DateTime, String PrivacyLevel, String SimpleLocation, String email, String name, byte[] profilePic)
        {
            //this.id=id;
            this.Lat=Lat;
            this.Long=Long;
            this.Action = Action;
            this.DateTime = DateTime;
            this.PrivacyLevel = PrivacyLevel;
            this.SimpleLocation = SimpleLocation;
            this.email = email;
            this.name = name;
            this.profilePic = profilePic;
        }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public String getSimpleLocation() {
        return SimpleLocation;
    }

    public void setSimpleLocation(String simpleLocation) {
        SimpleLocation = simpleLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getPrivacyLevel() {
        return PrivacyLevel;
    }

    public void setPrivacyLevel(String privacyLevel) {
        PrivacyLevel = privacyLevel;
    }
}



