//http://mobilesiri.com/android-sqlite-database-tutorial-using-android-studio/

package com.vanarragon.ben.locationapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.provider.Contacts.SettingsColumns.KEY;
import static android.provider.Telephony.Mms.Part.TEXT;
import static java.sql.Types.INTEGER;
import static java.text.Collator.PRIMARY;

public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "locationsDB";
    // Contacts table name
    private static final String TABLE_LOCATIONS = "locationsTable";
    // Shops Table Columns names
    //DATABASE STEP
    public static final String KEY_ID = "_id";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LONG = "long";
    public static final String KEY_ACTION = "action";
    public static final String KEY_DATETIME = "datetime";
    public static final String KEY_PRIVACYLEVEL = "privacylevel";
    public static final String KEY_SIMPLELOCATION = "simplelocation";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_PROFILEPIC = "profilepic";

    //DATABASE STEP
    public static final String[] ALL_KEYS = new String[]{KEY_ID,KEY_LAT,KEY_LONG,KEY_ACTION,KEY_DATETIME,KEY_PRIVACYLEVEL, KEY_SIMPLELOCATION,KEY_EMAIL, KEY_NAME, KEY_PROFILEPIC};

    private String latString = "";
    private String longString = "";
    private String actionString = "";
    private String dateTimeString = "";
    private String privacyLevelString = "";
    private String simplelocationString = "";




    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }

    //DATABASE STEP
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + KEY_LAT + " TEXT,"
        + KEY_LONG +  " TEXT, "
        + KEY_ACTION + " TEXT, "
        + KEY_DATETIME +  " TEXT, "
        + KEY_PRIVACYLEVEL + " TEXT, "
        + KEY_SIMPLELOCATION + " TEXT, "
                + KEY_EMAIL + " TEXT, "
                + KEY_NAME + " TEXT, "
                + KEY_PROFILEPIC + " BLOB"
        + ")";

        Log.d("log on create:", CREATE_LOCATIONS_TABLE);
        db.execSQL(CREATE_LOCATIONS_TABLE);
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
// Creating tables again
        onCreate(db);
    }
    // Adding new shop
    public void addLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PROFILEPIC, location.getProfilePic());//location
        values.put(KEY_NAME, location.getName());//location
        values.put(KEY_EMAIL, location.getEmail());//location
        values.put(KEY_SIMPLELOCATION, location.getSimpleLocation());//location
        values.put(KEY_PRIVACYLEVEL, location.getPrivacyLevel()); // Shop Name
        values.put(KEY_DATETIME, location.getDateTime()); // Shop Phone Number
        values.put(KEY_ACTION, location.getAction()); // Shop Name
        values.put(KEY_LONG, location.getLong()); // Shop Phone Number
        values.put(KEY_LAT, location.getLat()); // Shop Name

        Log.d("Log:",String.valueOf(values));


// Inserting Row
        db.insert(TABLE_LOCATIONS,null,values);
        db.close(); // Closing database connection
    }


    // Getting one shop
        public Location getLocation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOCATIONS, new String[]{

                KEY_LAT,
                KEY_LONG,
                KEY_ACTION,
                KEY_DATETIME,
                KEY_PRIVACYLEVEL,
                KEY_SIMPLELOCATION,
                KEY_EMAIL,
                KEY_NAME,
                KEY_PROFILEPIC}, KEY_ID + "=?",
        new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();


            //Integer.parseInt(cursor.getString(0)),
        Location location = new Location(
                Double.parseDouble(cursor.getString(1)),
                Double.parseDouble(cursor.getString(2)),
                String.valueOf(cursor.getString(3)),
                String.valueOf(cursor.getString(4)),
                String.valueOf(cursor.getString(5)),
                String.valueOf(cursor.getString(6)),
                String.valueOf(cursor.getString(7)),
                String.valueOf(cursor.getString(8)),
                cursor.getBlob(9));
        // return shop
        return location;
    }




    //+ KEY_LAT + ", "+ KEY_LONG + ", " +
    //", " + KEY_DATETIME +

//    Location location = new Location();
//    location.setId(Integer.parseInt(cursor.getString(0)));
//    location.setLat(cursor.getDouble(1));
//    location.setLong(cursor.getDouble(2));
//    location.setAction(cursor.getString(3));
//    location.setDateTime(cursor.getString(4));
//    location.setPrivacyLevel(cursor.getString(5));


    // Getting All Locations
    public List<Location> getAllLocations() {
        List<Location> locationList = new ArrayList<Location>();

        // Select All Query
        String selectQuery = "SELECT "+ KEY_LAT + ", "+ KEY_LONG + ", " + KEY_ACTION  + ", " + KEY_DATETIME + " FROM " + TABLE_LOCATIONS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try{
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {Location location = new Location();
                    location.setLat(cursor.getDouble(0));
                    location.setLong(cursor.getDouble(1));
                    location.setAction(cursor.getString(2));
                    location.setDateTime(cursor.getString(3));

                    // Adding contact to list
                    locationList.add(location);
                } while (cursor.moveToNext());
            }
        }finally{
            cursor.close();
        }
        db.close();
        // return contact list
        return locationList;
    }


    public Cursor getRecentLocations(){
        String where  = null;
        SQLiteDatabase db = this.getWritableDatabase();
        //String selectQuery = "SELECT "+ KEY_LAT + ", "+ KEY_LONG + ", " + KEY_ACTION  + ", " + KEY_DATETIME + " FROM " + TABLE_LOCATIONS;
        Cursor c = db.query(true, TABLE_LOCATIONS,ALL_KEYS, "privacylevel!=?",new String[]{"private"},null,null,KEY_DATETIME+" DESC" ,null);
        if(c!=null){
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getRecentLocations(String email){
        //String where  = KEY_EMAIL + " = " + email;
        SQLiteDatabase db = this.getWritableDatabase();
        //String selectQuery = "SELECT "+ KEY_LAT + ", "+ KEY_LONG + ", " + KEY_ACTION  + ", " + KEY_DATETIME + " FROM " + TABLE_LOCATIONS;
        Cursor c = db.query(true, TABLE_LOCATIONS,ALL_KEYS, "email=?" ,new String[]{email},null,null,KEY_DATETIME+" DESC" ,null);
        if(c!=null){
            c.moveToFirst();
        }
        return c;
    }

    //get a specific row (by rowId)
    public Cursor getRow(long rowId){
        String where  = " " + KEY_ID + " = " + rowId;
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_LOCATIONS + where;
        Cursor c = db.rawQuery(selectQuery, null);
        if(c!=null){
            c.moveToFirst();
        }
        return c;
    }


    // Getting shops Count
    public int getShopsCount() {
        String countQuery = "SELECT * FROM " + TABLE_LOCATIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    // Updating a shop
    public int updateShop(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, location.getId());
        values.put(KEY_LAT, location.getLat());
        values.put(KEY_LONG, location.getLong());
        values.put(KEY_ACTION, location.getAction());
        values.put(KEY_DATETIME, location.getDateTime());
        values.put(KEY_PRIVACYLEVEL, location.getPrivacyLevel());
        values.put(KEY_SIMPLELOCATION, location.getSimpleLocation());

// updating row
        return db.update(TABLE_LOCATIONS, values, KEY_ID + " = ?",
        new String[]{String.valueOf(location.getId())});
    }

    // Deleting a shop
    public void deleteShop(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATIONS, KEY_ID + " = ?",
        new String[] { String.valueOf(location.getId()) });
        db.close();
    }
}