package com.vanarragon.ben.locationapp.Volley;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by jamin on 2016-12-03.
 */

//http://stackoverflow.com/questions/37778372/volley-example-error-not-in-mainactivity
public class App extends Application {

    private static Context context;

    private static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }



    public static Context getContext() {
        return context;
    }

    /*public static RequestQueue getRequestQueue() {
        return requestQueue;
    }*/
}