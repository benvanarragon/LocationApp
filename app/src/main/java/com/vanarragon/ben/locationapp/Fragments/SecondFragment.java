package com.vanarragon.ben.locationapp.Fragments;

/**
 * Created by jamin on 2016-11-19.
 */


import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vanarragon.ben.locationapp.Activities.Base;
import com.vanarragon.ben.locationapp.Database.DBHandler;
import com.vanarragon.ben.locationapp.R;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SecondFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        LocationListener{


    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    Location mLastLocation;
    LatLng latLng;
    GoogleMap mGoogleMap;
    //SupportMapFragment mFragment;
    Marker currLocationMarker;

    FloatingActionButton fab;

    //permissions stuff
    private static final int PERMISSION_REQUEST_CODE=0;


    //permissions stuff
    //private static final int REQUEST_FINE_LOCATION=0;


//    private final int[] MAP_TYPES = {
//            GoogleMap.MAP_TYPE_SATELLITE,
//            GoogleMap.MAP_TYPE_NORMAL,
//            GoogleMap.MAP_TYPE_HYBRID,
//            GoogleMap.MAP_TYPE_TERRAIN,
//            GoogleMap.MAP_TYPE_NONE
//
//    };

    private int curMapTypeIndex = 0;

    //my view
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.second_layout, container, false);


        if(checkPlayServices()){
            buildGoogleApiClient();
        }

        //creates a new instance of a map fragment with a reference to the fragment in second_layout.xml
        //MapFragment mapFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        //the this here is the reference to the onmapreadycallback interface

        return myView;
    }


    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
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


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment mapFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //creates a new instance of a map fragment with a reference to the fragment in second_layout.xml
        //MapFragment mapFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        //the this here is the reference to the onmapreadycallback interface

        /*SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && mapFragment.getMapAsync(this) != null) {
                googleMap=mapFragment.getMapAsync(this);
                //after this you do whatever you want with the map
            }
        }*/

        setHasOptionsMenu(true);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mGoogleApiClient!= null && mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if(!checkPermission())
            requestPermission();
        else
        mGoogleMap.setMyLocationEnabled(true);
        //developers.google.com/maps/documentation/android-api/map#map_padding
        mGoogleMap.setPadding(10,10,100,10);
        //(left, top, right, 4th is bottom)

        //set on click listeners once map is ready
        //mGoogleMap.setOnMapClickListener(this);
        //mGoogleMap.setOnMarkerClickListener(this);

        //buildGoogleApiClient();

        mGoogleApiClient.connect();

    }

    //https://ddrohan.github.io/mad-2016/topic04-google-services/talk-3-google-3/01.maps.pdf
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    //https://ddrohan.github.io/mad-2016/topic04-google-services/talk-3-google-3/01.maps.pdf
    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted
                    Toast.makeText(getActivity(),"Permissions Granted", Toast.LENGTH_SHORT).show();
                }
                else{
                    // no granted
                }
                return;
            }

        }

    }

    //custom method
    /*public void getLastLocation(Location location){
        String Lat = String.valueOf(location.getLatitude());
        String Long = String.valueOf(location.getLongitude());

        String mLastLocation = Lat + "," + Long;

    }*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        //getMapAsync(this);

        if(!checkPermission())
            requestPermission();
        else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Base.lastKnownLocation = mLastLocation;

            if (mLastLocation != null) {
                //place marker at current position
                //mGoogleMap.clear();
                latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                currLocationMarker = mGoogleMap.addMarker(markerOptions);
            }


            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(500); //half second
            mLocationRequest.setFastestInterval(250); //3 seconds
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

            //populate with everything in the database
            getAllLocations();


            //zoom to current position:
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(13).build();

            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));



            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }



    //build google api client
        protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }



        @Override
        public void onConnectionSuspended(int i) {
            //Toast.makeText(getActivity(),"onConnectionSuspended", Toast.LENGTH_SHORT).show();
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Toast.makeText(getActivity(),"Connection Failed",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLocationChanged(Location location) {

            //place marker at current position
            //mGoogleMap.clear();
            /*if (currLocationMarker != null) {
                currLocationMarker.remove();
            }
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);

            //Toast.makeText(getActivity(),"Location Changed",Toast.LENGTH_SHORT).show();

            //zoom to current position:
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(18).build();

            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));*/

            //If you only need one location, unregister the listener
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        }

        public void getAllLocations(){
            //set up a new db handler
            DBHandler db = new DBHandler(getActivity());
            db.getWritableDatabase();

            int cPos; // for cursor position
            Cursor cursor;

            //store it in a new locatin object
            ArrayList<com.vanarragon.ben.locationapp.Database.Location> locations = new ArrayList<com.vanarragon.ben.locationapp.Database.Location>();
            com.vanarragon.ben.locationapp.Database.Location location;
            cursor = db.getRecentLocations();
            try {

                //DATABASE STEP
                if (cursor.moveToFirst()) { // move cursor to first row because implicitly
                    do { // cursor is position before first row
                        location = new com.vanarragon.ben.locationapp.Database.Location(); // for each row create new Foo
                        location.setId(cursor.getInt(0));
                        location.setLat(cursor.getDouble(1));
                        location.setLong(cursor.getDouble(2));
                        location.setAction(cursor.getString(3));
                        location.setDateTime(cursor.getString(4));
                        location.setPrivacyLevel(cursor.getString(5));
                        location.setSimpleLocation(cursor.getString(6));
                        location.setEmail(cursor.getString(7));
                        location.setName(cursor.getString(8));
                        location.setProfilePic(cursor.getBlob(9));
                        locations.add(location);
                    } while (cursor.moveToNext()); // it moves cursor to next row
                }
            }
            finally{
            if (cursor != null);
                cursor.close();
            }if (db != null) {
                db.close();
            }

            //populate markers with array
            populateMarkers(locations);
        }

        //http://stackoverflow.com/questions/13855049/how-to-show-multiple-markers-on-mapfragment-in-google-map-api-v2
        public void populateMarkers(ArrayList<com.vanarragon.ben.locationapp.Database.Location> locations){
            ArrayList<Marker> markersArray = new ArrayList<Marker>();
            for(int i = 0; i <locations.size();i++){

                Location loc = new Location("");
                loc.setLatitude(locations.get(i).getLat());
                loc.setLongitude(locations.get(i).getLong());

                Double lat, longString;
                lat = locations.get(i).getLat();
                longString = locations.get(i).getLong();

                String action, datetime;
                action = locations.get(i).getAction();
                 datetime= locations.get(i).getDateTime();


                String distance = getDistanceBetween(mLastLocation, lat, longString);

                LatLng ll = new LatLng(lat,longString);

                markersArray.add(mGoogleMap.addMarker(new MarkerOptions()
                        .position(ll)
                        .title(action)
                        .snippet(datetime + ", " + distance)
                        .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED))));

            }




        }

    //custom parent method for multiple fragments
    public String getDistanceBetween(android.location.Location location, Double lat, Double longString){
        android.location.Location currentLoc = new android.location.Location("");
        currentLoc.setLatitude(lat);
        currentLoc.setLongitude(longString);
        String distanceFormatted;

        Float distanceInMeters = currentLoc.distanceTo(location);
        if(distanceInMeters >= 1000){
            distanceInMeters /= 1000;//distance in kilometers
            distanceFormatted = String.format("%.1f",distanceInMeters) + "Km away";
        }
        else{
            distanceFormatted = String.format("%.0f", distanceInMeters) + "m away";
        }


        return distanceFormatted;
    }

}

