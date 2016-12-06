package com.vanarragon.ben.locationapp.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.vanarragon.ben.locationapp.Activities.Base;
import com.vanarragon.ben.locationapp.Database.DBHandler;
import com.vanarragon.ben.locationapp.Database.DbBitmapUtility;
import com.vanarragon.ben.locationapp.Fragments.EditFragment;
import com.vanarragon.ben.locationapp.Fragments.FirstFragment;
import com.vanarragon.ben.locationapp.R;

import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jamin on 2016-12-05.
 */
//https://guides.codepath.com/android/Populating-a-ListView-with-a-CursorAdapter
public class CustomCursorAdapter extends CursorAdapter {

    String timeAgo;

    //default constructor
    public CustomCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.location_layout, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        ImageView imageViewProfilePic = (ImageView) view.findViewById(R.id.profilePic);
        TextView textViewAction = (TextView) view.findViewById(R.id.textViewAction);
        TextView textViewName = (TextView) view.findViewById(R.id.textViewName);
        TextView textViewSimpleLocation = (TextView) view.findViewById(R.id.textViewSimpleLocation);
        TextView textViewDateTime = (TextView) view.findViewById(R.id.textViewDateTime);

        // Extract properties from cursor
        byte[] profilePicByte = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHandler.KEY_PROFILEPIC));
        String action = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_ACTION));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_NAME));
        String simpleLocation = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_SIMPLELOCATION));
        String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_DATETIME));
        //not using in listview
        String lat = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_LAT));
        String longString = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_LONG));

        //DATA TRANSFORMATIONS BEFORE LOADING TO LISTVIEW
        //convert profilePic from bytearray to bitmap
        Bitmap profilePicBmp = DbBitmapUtility.getImage(profilePicByte);

        //translate time
        //http://stackoverflow.com/questions/19419374/android-convert-date-and-time-to-milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
        try {
            Date date = sdf.parse(dateTime);
            long time = date.getTime();
            timeAgo = TimeAgo.using(time, new TimeAgoMessages.Builder().defaultLocale().build());
        }catch(java.text.ParseException e){
            e.printStackTrace();
        }

        //convert distance between
        EditFragment ef = new EditFragment();

        String distanceBetween = ef.getDistanceBetween(Base.lastKnownLocation, lat, longString);

        //populate fields with extraced properties
        imageViewProfilePic.setImageBitmap(profilePicBmp);
        textViewAction.setText(action);
        textViewName.setText(name);
        textViewSimpleLocation.setText(distanceBetween);
        textViewDateTime.setText(timeAgo);



    }

}
