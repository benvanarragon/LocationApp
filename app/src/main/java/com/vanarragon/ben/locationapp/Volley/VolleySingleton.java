package com.vanarragon.ben.locationapp.Volley;

/**
 * Created by jamin on 2016-12-03.
 */

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;

//https://www.youtube.com/watch?v=ZTare91T-JE
//https://developer.android.com/training/volley/request.html
//https://cypressnorth.com/mobile-application-development/setting-android-google-volley-imageloader-networkimageview/

public class VolleySingleton {
    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;
    private static Context mCtx;



    private VolleySingleton(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }


    public RequestQueue getRequestQueue(){

        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }else{}


        return mRequestQueue;
    }

    //get instance of this class
    public static synchronized VolleySingleton getmInstance(Context context){
        if(mInstance==null){
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> request){
        mRequestQueue.add(request);
    }

}
