package com.vanarragon.ben.locationapp.Fragments;

/**
 * Created by jamin on 2016-11-19.
 */

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.renderscript.Double2;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vanarragon.ben.locationapp.Database.DbBitmapUtility;
import com.vanarragon.ben.locationapp.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class EditFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    String id,lat,longString,action,date,privacy,simpleLoc,name,email;
    byte[] profilePic;
    View myView;
    private TextView tvTest;

    //layout stuff
    private TextView lblLocation;
    private TextView lblDate;
    private EditText activityTextBox;
    private RadioGroup rg;
    private RadioButton rb, rbDefault;
    private ImageView iv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.layout_edit, container, false);

        lblLocation = (TextView) myView.findViewById(R.id.lblLocation);
        lblDate = (TextView) myView.findViewById(R.id.lblDate) ;
        activityTextBox = (EditText) myView.findViewById(R.id.activityTextBox);
        rg = (RadioGroup) myView.findViewById(R.id.rgPrivacy);
        rbDefault = (RadioButton) myView.findViewById(R.id.rb_public);
        iv = (ImageView)myView.findViewById(R.id.imgProfilePicture);




        //check google play services and build google client
        if(checkPlayServices()){
            buildGoogleApiClient();
        }

        //DATABASE STEP
        Bundle args = getArguments();
        if (args == null) {
            Toast.makeText(getActivity(), "arguments is null " , Toast.LENGTH_LONG).show();
        } else {

            id = args.getString("id");
            lat = args.getString("lat");
            longString = args.getString("long");
            action = args.getString("action");
            date = args.getString("date");
            privacy = args.getString("privacy");
            simpleLoc = args.getString("simpleLoc");
            email = args.getString("email");
            name = args.getString("name");
            profilePic = args.getByteArray("profilepic");
            //Toast.makeText(getActivity(), "text: " + id , Toast.LENGTH_LONG).show();

            //convert profilePic from bytearray to bitmap
            Bitmap profilePicture = DbBitmapUtility.getImage(profilePic);



            lblLocation.setText(simpleLoc);
            iv.setImageBitmap(profilePicture);
            lblDate.setText(date);
            activityTextBox.setText(action);
        }

        return myView;
    }

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    LatLng latLng;
    GoogleMap mGoogleMap;
    //SupportMapFragment mFragment;
    Marker currLocationMarker;

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
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

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


        String distance = getDistanceBetween(location, lat, longString);

        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(longString));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(action);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerOptions.snippet(action + ", " + date + ", " + distance);
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        //Toast.makeText(getActivity(),"Location Changed",Toast.LENGTH_SHORT).show();

        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(10).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        //If you only need one location, unregister the listener
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    //custom parent method for multiple fragments
    public String getDistanceBetween(android.location.Location location, String lat, String longString){
        android.location.Location currentLoc = new android.location.Location("");
        currentLoc.setLatitude(Double.parseDouble(lat));
        currentLoc.setLongitude(Double.parseDouble(longString));
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
