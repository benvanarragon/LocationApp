package com.vanarragon.ben.locationapp.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.location.LocationServices;
import com.vanarragon.ben.locationapp.Fragments.MyLocationsFragment;
import com.vanarragon.ben.locationapp.Volley.App;
import com.vanarragon.ben.locationapp.Volley.VolleySingleton;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.vanarragon.ben.locationapp.Database.DBHandler;
import com.vanarragon.ben.locationapp.Fragments.FirstFragment;
import com.vanarragon.ben.locationapp.Fragments.HomeFragment;
import com.vanarragon.ben.locationapp.R;
import com.vanarragon.ben.locationapp.Fragments.SecondFragment;
import com.vanarragon.ben.locationapp.Fragments.SettingsFragment;
import com.vanarragon.ben.locationapp.Fragments.ThirdFragment;

public class MainActivity extends Base
        implements NavigationView.OnNavigationItemSelectedListener{

    private ImageView googlePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();*/

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager .beginTransaction()
                        .replace(R.id.content_frame
                                ,   new FirstFragment())
                        .commit();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //SetUp GooglePhoto and Email for Drawer here
        googlePhoto = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.googlephoto);
        getGooglePhoto(Base.googlePhotoURL,googlePhoto);

        TextView googleName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.googlename);
        googleName.setText(Base.googleName);

        TextView googleMail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.googlemail);
        googleMail.setText(Base.googleMail);

        dialog = new ProgressDialog(this,1);

        //calendar for getting current date to insert into db for test
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
        String formattedDate = sdf.format(c.getTime());


        //SEEDING
        //set up a new db handler
        /*DBHandler db = new DBHandler(this);

        db.getWritableDatabase();
        Log.d("writeable obtained",":");
*/
        // Inserting Shop/Rows
        /*Log.d("Insert: ", "Inserting ..");
        db.addLocation(new Location(52.245,-7.138,"Hockey",formattedDate,"friends", "Ireland"));
        db.addLocation(new Location(52.345,-7.238,"Hockey2",formattedDate,"friends", "Ireland"));
        db.addLocation(new Location(52.445,-7.338,"Hockey3",formattedDate,"private", "Ireland"));
        db.addLocation(new Location(52.545,-7.438,"Hockey4",formattedDate,"public", "Ireland"));
        db.addLocation(new Location(52.645,-7.538,"Hockey5",formattedDate,"public", "Ireland"));*/

        // Reading all shops
        /*Log.d("Reading: ", "Reading all shops..");
        List<Location> locations = db.getAllLocations();

        for (Location location : locations) {
                String log = "Id: " +
                    location.getId() + " ,Lat: " +
                    location.getLat() + " ,Long: " +
                    location.getLong() + " , Action: " +
                    location.getAction() + ", DateTime: " +
                    location.getDateTime() + ", Privacy: " +
                    location.getPrivacyLevel();

        // Writing shops to log
            Log.d("Location: : ", log);
        }
        //close the database
        db.close();*/

        //set default action bar title
        getSupportActionBar().setTitle("Home");




        //create a new fragment transaction object

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        //loads our home fragment by default ***
        HomeFragment fragment = HomeFragment.newInstance();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    //This is our navigation menu, options are selected then fragments will be loaded for what that option was
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_home_layout) {
            fragmentManager .beginTransaction()
                    .replace(R.id.content_frame
                            ,   new HomeFragment())
                    .commit();
            getSupportActionBar().setTitle("Home");
        }else if (id == R.id.nav_first_layout) {
            fragmentManager .beginTransaction()
                    .replace(R.id.content_frame
                    ,   new FirstFragment())
                    .commit();
            getSupportActionBar().setTitle("Add Location");
        } else if (id == R.id.nav_second_layout) {
            fragmentManager .beginTransaction()
                    .replace(R.id.content_frame
                            ,   new SecondFragment())
                    .commit();
            getSupportActionBar().setTitle("Map");
        } else if (id == R.id.nav_third_layout) {
            fragmentManager .beginTransaction()
                    .replace(R.id.content_frame
                            ,   new MyLocationsFragment())
                    .commit();
            getSupportActionBar().setTitle("My Locations");
        } else if (id == R.id.nav_settings) {
            fragmentManager .beginTransaction()
                    .replace(R.id.content_frame
                            ,   new SettingsFragment())
                    .commit();

        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void getGooglePhoto(String url,final ImageView googlePhoto) {

        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Base.googlePhoto = response;
                        googlePhoto.setImageBitmap(Base.googlePhoto);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Something went wrong!");
                error.printStackTrace();
            }
        });
        //App.getRequestQueue().add(imgRequest);

        VolleySingleton.getmInstance(Base.app.getContext()).addToRequestQueue(imgRequest);
    }






    //must be implemented from our first fragment interface that we implemented in this class
    /*@Override
    public void onFragmentInteraction(Location location) {
        FirstFragment fragment1 = FirstFragment.newInstance();
        fragment1.getLastLocation();

        SecondFragment secondFragment = (SecondFragment)getFragmentManager().findFragmentById(R.id.map);
        if(location != null) {
            secondFragment.getLastLocation(location);
        }
    }*/
}
