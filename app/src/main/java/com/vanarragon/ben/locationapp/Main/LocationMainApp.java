package com.vanarragon.ben.locationapp.Main;

import android.app.Application;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.vanarragon.ben.locationapp.Database.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationMainApp extends Application
{
    private RequestQueue mRequestQueue;
    private static LocationMainApp mInstance;
    public List <Location>  locationList = new ArrayList<Location>();

    public static final String TAG = LocationMainApp.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("checkin", "Location App Started");
        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static synchronized LocationMainApp getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void add(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancel() {
        mRequestQueue.cancelAll(TAG);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}