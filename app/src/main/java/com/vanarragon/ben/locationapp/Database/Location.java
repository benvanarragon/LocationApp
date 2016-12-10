//http://mobilesiri.com/android-sqlite-database-tutorial-using-android-studio/

package com.vanarragon.ben.locationapp.Database;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.vanarragon.ben.locationapp.Activities.Base;
import com.vanarragon.ben.locationapp.Volley.App;

import java.sql.Blob;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

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


    //http://stackoverflow.com/questions/5482402/android-load-values-from-sqlite-database-to-an-arraylist
    public List<Location> getResults() {

        DBHandler db = new DBHandler(App.getContext()); //my database helper file
        db.getWritableDatabase();

        List<Location> resultList = new ArrayList<Location>();


        Cursor cursor = db.getRecentLocations(); //function to retrieve all values from a table- written in MyDb.java file
        try {
            if (cursor.moveToFirst()) {
                do {
                    byte[] profilePicByte = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHandler.KEY_PROFILEPIC));
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.KEY_ID));
                    String action = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_ACTION));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_NAME));
                    String simpleLocation = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_SIMPLELOCATION));
                    String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_DATETIME));
                    String lat = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_LAT));
                    String longString = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_LONG));


                    Location location = new Location();
                    location.setId(id);
                    location.setAction(action);
                    location.setName(name);
                    location.setSimpleLocation(simpleLocation);
                    location.setDateTime(dateTime);
                    location.setLat(Double.parseDouble(lat));
                    location.setLong(Double.parseDouble(longString));
                    location.setProfilePic(profilePicByte);
                    resultList.add(location);
                } while (cursor.moveToNext());
            }
        }catch (Exception e) {
                Log.e("Location.java: ", "Error " + e.toString());
            }



        cursor.close();

        db.close();
        return resultList;
    }
}



