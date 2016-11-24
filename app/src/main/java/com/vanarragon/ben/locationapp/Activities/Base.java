package com.vanarragon.ben.locationapp.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vanarragon.ben.locationapp.Main.LocationMainApp;
import com.vanarragon.ben.locationapp.R;


import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

/**
 * Created by jamin on 2016-11-23.
 */

public class Base extends AppCompatActivity {

    public static LocationMainApp	app = LocationMainApp.getInstance();
    private static final int REQUEST_FINE_LOCATION=0;

    /* Client used to interact with Google APIs. */
    public static GoogleApiClient mGoogleApiClient;
    public static boolean signedIn = false;
    public static String googleToken;
    public static String googleName;
    public static String googleMail;
    public static String googlePhotoURL;
    public static Bitmap googlePhoto;
    public static ProgressDialog dialog;
    public static int drawerID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = LocationMainApp.getInstance();
    }

    protected void goToActivity(Activity current,
                                Class<? extends Activity> activityClass,
                                Bundle bundle) {
        Intent newActivity = new Intent(current, activityClass);

        if (bundle != null) newActivity.putExtras(bundle);

        current.startActivity(newActivity);
    }

    public void openInfoDialog(Activity current) {
        Dialog dialog = new Dialog(current);
        dialog.setTitle("About Location App");
        dialog.setContentView(R.layout.info);

        TextView currentVersion = (TextView) dialog
                .findViewById(R.id.versionTextView);
        currentVersion.setText("1.0");

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void menuInfo(MenuItem m)
    {
        openInfoDialog(this);
    }

    public void menuHelp(MenuItem m)
    {

    }

    public void menuHome(MenuItem m)
    {
        goToActivity(this, MainActivity.class, null);
    }

    public void logout(MenuItem item) {
        Log.v("coffeemate","Logging out from: " + mGoogleApiClient);

        Base.signedIn = mGoogleApiClient.isConnected();

        if (Base.signedIn) {
            Log.v("coffeemate","Logging out from: " + mGoogleApiClient);
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            Base.googleToken = "";
            Base.signedIn = mGoogleApiClient.isConnected();
            mGoogleApiClient.connect();
            Log.v("coffeemate","googleClient Connected: " + Base.signedIn);
            Toast.makeText(this, "Signing out of Google", Toast.LENGTH_LONG).show();

            Log.v("coffeemate", "CoffeeMate App Terminated");
        }

        startActivity(new Intent(Base.this, Login.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }


}
