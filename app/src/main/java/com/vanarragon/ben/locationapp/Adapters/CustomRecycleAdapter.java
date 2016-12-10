package com.vanarragon.ben.locationapp.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.vanarragon.ben.locationapp.Activities.Base;
import com.vanarragon.ben.locationapp.Database.DBHandler;
import com.vanarragon.ben.locationapp.Database.DbBitmapUtility;
import com.vanarragon.ben.locationapp.Database.Location;
import com.vanarragon.ben.locationapp.Fragments.EditFragment;
import com.vanarragon.ben.locationapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jamin on 2016-12-05.
 */
//http://stacktips.com/tutorials/android/android-recyclerview-example
public class CustomRecycleAdapter extends RecyclerView.Adapter<CustomRecycleAdapter.CustomViewHolder> {

    private List<Location> locationList;
    private Context mContext;
    String timeAgo;

    public CustomRecycleAdapter(Context context, List<Location> locationList) {
            this.locationList = locationList;
            this.mContext = context;
            }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfilePic;
        TextView textViewAction;
        TextView textViewName;
        TextView textViewSimpleLocation;
        TextView textViewDateTime;
        CardView cv;

        public CustomViewHolder(View view) {
            super(view);
            this.imageViewProfilePic = (ImageView) view.findViewById(R.id.profilePic);
            this.textViewAction = (TextView) view.findViewById(R.id.textViewAction);
            this.textViewName = (TextView) view.findViewById(R.id.textViewName);
            this.textViewSimpleLocation = (TextView) view.findViewById(R.id.textViewSimpleLocation);
            this.textViewDateTime = (TextView) view.findViewById(R.id.textViewDateTime);
            this.cv = (CardView) view.findViewById(R.id.cv);


        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_layout, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
            }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        //https://www.youtube.com/watch?v=8vvpP8mWgtE
        YoYo.with(Techniques.FadeIn).playOn(customViewHolder.cv);

        Location location = locationList.get(i);



        byte[] profilePicByte = location.getProfilePic();
        String action = location.getAction();
        String name = location.getName();
        String simpleLocation = location.getSimpleLocation();
        String dateTime = location.getDateTime();
        String lat = String.valueOf(location.getLat());
        String longString = String.valueOf(location.getLong());



        Bitmap profilePicBmp = DbBitmapUtility.getImage(profilePicByte);

        //convert to timeAgo Library
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



            //Setting text view title
        customViewHolder.imageViewProfilePic.setImageBitmap(profilePicBmp);
        customViewHolder.textViewAction.setText(action);
        customViewHolder.textViewName.setText(name);
        customViewHolder.textViewSimpleLocation.setText(distanceBetween);
        customViewHolder.textViewDateTime.setText(timeAgo);
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }




}
