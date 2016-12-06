package com.vanarragon.ben.locationapp.Fragments;

/**
 * Created by jamin on 2016-11-19.
 */

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.vanarragon.ben.locationapp.Activities.Base;
import com.vanarragon.ben.locationapp.Database.DBHandler;
import android.location.Location;

import com.vanarragon.ben.locationapp.Database.DbBitmapUtility;
import com.vanarragon.ben.locationapp.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FirstFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener{

    private static final String TAG = FirstFragment.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    private boolean mRequestLocationUpdates = false;

    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 10000;
    private static int FASTEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;

    private TextView lblLocation;
    private TextView lblDate;
    private EditText activityTextBox;
    private Button btnShowLocation, btnStartLocationUpdates, btnSaveLocation;
    private RadioGroup rg;
    private RadioButton rb, rbDefault;

    //variables for saving to the database
    private Double currentLat, currentLong;
    private String  currentActivity,currentDateTime, currentPrivacyLevel, currentSimpleLocation;

    //permissions stuff
    private static final int REQUEST_FINE_LOCATION=0;


    //if we extened fragmentInteraction.java interface variable
    //private OnFragmentInteractionListener mListener;

    //view for fragment
    View myView;


    //this is called from main activity class to make a reference in the beginning of the app
    public static FirstFragment newInstance() {
        FirstFragment fragment = new FirstFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        myView = inflater.inflate(R.layout.first_layout, container, false);

        lblLocation = (TextView) myView.findViewById(R.id.lblLocation);
        lblDate = (TextView) myView.findViewById(R.id.lblDate) ;
        btnShowLocation = (Button) myView.findViewById(R.id.btnShowLocation);
        btnStartLocationUpdates = (Button) myView.findViewById(R.id.btnStartLocationUpdates);
        btnSaveLocation = (Button) myView.findViewById(R.id.btnSave);
        activityTextBox = (EditText) myView.findViewById(R.id.activityTextBox);
        rg = (RadioGroup) myView.findViewById(R.id.rgPrivacy);
        rbDefault = (RadioButton) myView.findViewById(R.id.rb_public);
        rbDefault.setChecked(true);//sets the default radio button to avoid getting a reference on null object later

        if(checkPlayServices()){
            buildGoogleApiClient();
        }

        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocation();
            }
        });

        btnStartLocationUpdates.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePeriodicLocationUpdates();
            }
        }));

        //SAVES THE RECORD
        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(activityTextBox.getText() != null) {
                    currentActivity = activityTextBox.getText().toString();
                    Log.d("Current activity: ", currentActivity);
                }



                //http://stackoverflow.com/questions/18179124/android-getting-value-from-selected-radiobutton
                int selectedId = rg.getCheckedRadioButtonId();



                Log.d("Selected ID", String.valueOf(selectedId));
                // find the radiobutton by returned id
                rb = (RadioButton) rg.findViewById(selectedId);
                String rbText = rb.getText().toString();
                currentPrivacyLevel = rbText.toLowerCase();

                //CALLS THE SAVE LOCATION METHOD AND INSERTS THE RECORD IN THE DATABASE
                boolean saved = saveLocation(v);

                if(saved) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame
                                    , new HomeFragment())
                            .commit();
                }

            }
        });

        return myView;
    }

    //permissions stuff
    private void loadPermissions(String perm,int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), perm)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{perm},requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted
                    displayLocation();
                }
                else{
                    // not granted
                    /*Snackbar snackbar = Snackbar
                            .make(view, "Please enable your location services", Snackbar.LENGTH_LONG)
                            .setAction("SETTINGS", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                                }
                            });
                    snackbar.show();*/


                }
                return;
            }

        }

    }

    @Override
    public void onStart(){
        super.onStart();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        checkPlayServices();
        if(mGoogleApiClient.isConnected() && mRequestLocationUpdates){
            startLocationUpdates();
        }
    }

    @Override
    public void onStop(){
        super.onStop();

        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void displayLocation(){

        getLastLocation();


        if(mLastLocation != null) {
            Double latitude = mLastLocation.getLatitude();
            Double longitude = mLastLocation.getLongitude();

            //set variables to save to the database
            currentLat = latitude;
            currentLong = longitude;

            LatLng latLng = new LatLng(latitude, longitude);

            String address = getAddressFromLatLng(latLng);
            lblLocation.setText("Address: " + address);


        }else{
            lblLocation.setText("Couldn't get a location. Make sure your location services are enabled");
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
        String formattedDate = sdf.format(c.getTime());
        currentDateTime = formattedDate;
        lblDate.setText("Current time: " + formattedDate);


        //String formattedDate = sdf.format(c.getTime());
        //lblDate.setText("Date: " + formattedDate);

        //if we extend FragmentInteraction for the interface
        //interface setting variables of locations to pass
        //if(mListener != null){
        //    mListener.onFragmentInteraction(mLastLocation);
        //}

    }

    //gets physical address location from a latitude and a longitude
    private String getAddressFromLatLng(LatLng latlng){

        //it converts to irish not english...fix this later, ask david
        Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);

        String strAddress = "";
        Address address;

        try{
            address = geocoder
                    .getFromLocation(latlng.latitude, latlng.longitude,1)
                    .get(0);

           /* if(address.getAddressLine(0) != null) {
                strAddress = address.getAddressLine(0);
            }else if(address.getAddressLine(1) != null) {
                strAddress += ", " +address.getAddressLine(1);
            }else if(address.getAddressLine(2) != null) {
                strAddress += ", " +address.getAddressLine(2);
            }*/

            strAddress = address.getAddressLine(0) +
                    ", "  +address.getAddressLine(1) +
                    ", " +address.getAddressLine(2);


            currentSimpleLocation = address.getAddressLine(0);

        }
        catch (IOException e){

        }
        return strAddress;
    }

    private void togglePeriodicLocationUpdates(){


        if(!mRequestLocationUpdates){
            btnStartLocationUpdates.setText(getString(R.string.btn_stop_location_updates));

            mRequestLocationUpdates = true;

            startLocationUpdates();
        }else{

            btnStartLocationUpdates.setText(getString(R.string.btn_start_location_updates));

            mRequestLocationUpdates = false;

            stopLocationUpdates();
        }
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
    }

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private boolean checkPlayServices(){
        int resultCode =GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else {
                Toast.makeText(getActivity().getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
            }
        return true;
        }

    protected void startLocationUpdates(){
        //call permissions stuff
        //loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION,REQUEST_FINE_LOCATION);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_FINE_LOCATION);
            }
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        createLocationRequest();
        displayLocation();

        if(mRequestLocationUpdates)
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed:" + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        Toast.makeText(getActivity().getApplicationContext(), "Location Changed", Toast.LENGTH_SHORT).show();

        displayLocation();
    }



    public void getLastLocation(){
        //call permissions stuff
        //loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION,REQUEST_FINE_LOCATION);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_FINE_LOCATION);
            }
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Base.lastKnownLocation = mLastLocation;
    }


    public boolean saveLocation(View view){
        //flag for successful save
        boolean saved = false;

        //set up a new db handler
        DBHandler db = new DBHandler(getActivity());
        db.getWritableDatabase();

        if(currentLat != null && currentLong != null) {
            if(currentActivity != null && !currentActivity.isEmpty() && !currentActivity.equals("null")){
                if(currentDateTime != null) {
                    if(currentSimpleLocation !=null) {
                        //SAVES THE RECORD TO THE DATABASE
                        byte[] googlePhoto = DbBitmapUtility.getBytes(Base.googlePhoto);
                        db.addLocation(new com.vanarragon.ben.locationapp.Database.Location(currentLat, currentLong, currentActivity, currentDateTime, currentPrivacyLevel, currentSimpleLocation, Base.googleMail,Base.googleName,googlePhoto));
                        saved = true;
                        Snackbar snackbar = Snackbar
                                .make(view, "Successfully Added!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }else{
                        //simple location is null
                        saved = false;
                        Snackbar snackbar = Snackbar
                                .make(view, "Please enable your location services", Snackbar.LENGTH_LONG)
                                .setAction("SETTINGS", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                                    }
                                });
                        snackbar.show();
                    }
                }else{
                    //datetime is null
                    saved = false;
                Snackbar snackbar = Snackbar
                        .make(view, "Error, please restart app", Snackbar.LENGTH_SHORT);
                snackbar.show();
                }
            }else {
                //if activity is null
                saved = false;//marker set to false
                Snackbar snackbar = Snackbar
                        .make(view, "Please type in an Activity", Snackbar.LENGTH_SHORT);
                snackbar.show();

                //request focus on the actiivty textbox
                activityTextBox.requestFocus();
                //http://stackoverflow.com/questions/3072173/how-to-call-a-method-after-a-delay-in-android
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //open keyboard in activityTextBox after 2 seconds
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(activityTextBox, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 2000);
            }
        }else{//if lat or long is null
            saved = false;
            Snackbar snackbar = Snackbar
                    .make(view, "Please enable your location services", Snackbar.LENGTH_LONG)
                    .setAction("SETTINGS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        }
                    });
            snackbar.show();
        }
        return saved;
    }
}
