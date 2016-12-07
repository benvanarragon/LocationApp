package com.vanarragon.ben.locationapp.Fragments;

/**
 * Created by jamin on 2016-11-19.
 */

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.vanarragon.ben.locationapp.Activities.Base;
import com.vanarragon.ben.locationapp.Adapters.CustomCursorAdapter;
import com.vanarragon.ben.locationapp.Database.DBHandler;
import com.vanarragon.ben.locationapp.R;

public class MyLocationsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    View myView;
    private ListView listView;
    CustomCursorAdapter myCursorAdapter;
    private ArrayAdapter listAdapter;
    private String[] result_data;
    private SwipeRefreshLayout srl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_layout, container, false);

        //set list view to the listView2 in the layout
        listView = (ListView) myView.findViewById(R.id.lvNewsFeed);

        srl = (SwipeRefreshLayout)myView.findViewById(R.id.srl);
        srl.setColorSchemeResources(R.color.green);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor mycursor = (Cursor)listView.getItemAtPosition(position);

                //DATABASE STEP
                //http://stackoverflow.com/questions/24555417/how-to-send-data-from-one-fragment-to-another-fragment
                EditMyLocationsFragment ef = new EditMyLocationsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", mycursor.getString(0));
                bundle.putString("lat", mycursor.getString(1));
                bundle.putString("long", mycursor.getString(2));
                bundle.putString("action", mycursor.getString(3));
                bundle.putString("date", mycursor.getString(4));
                bundle.putString("privacy", mycursor.getString(5));
                bundle.putString("simpleLoc", mycursor.getString(6));
                bundle.putString("email", mycursor.getString(7));
                bundle.putString("name", mycursor.getString(8));
                bundle.putByteArray("profilepic", mycursor.getBlob(9));
                ef.setArguments(bundle);


                // Add the fragment to the 'fragment_container' FrameLayout
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, ef).addToBackStack("tag").commit();


                /*FragmentManager fragmentManager = getFragmentManager();
                fragmentManager .beginTransaction()
                        .replace(R.id.content_frame
                                ,   new EditFragment())
                        .commit();*/

            }
        });




        srl.setOnRefreshListener(this);

        /**
         * http://www.androidhive.info/2015/05/android-swipe-down-to-refresh-listview-tutorial/
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        srl.post(new Runnable() {
                     @Override
                     public void run() {
                         srl.setRefreshing(true);

                         populateListView();
                     }
                 }
        );


        //runs populate list view and populates the list view
        //populateListView();

        return myView;
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    //https://www.youtube.com/watch?v=c-7sW6UJHw0
    public void populateListView(){
        //set up a new db handler
        DBHandler db = new DBHandler(getActivity());
        db.getWritableDatabase();

        //String[] locationsFields = new String[]{DBHandler.KEY_SIMPLELOCATION, DBHandler.KEY_NAME, DBHandler.KEY_ACTION , DBHandler.KEY_DATETIME};
        //int[] toTextViews = new int[]{R.id.textViewSimpleLocation,R.id.textViewName,R.id.textViewAction,R.id.textViewDateTime};

        //DATABASE STEP
        Cursor cursor = db.getRecentLocations(Base.googleMail);
        myCursorAdapter = new CustomCursorAdapter(getActivity(),cursor);

        //grab listview from activity
        listView = (ListView) myView.findViewById(R.id.lvNewsFeed);
        listView.setAdapter(myCursorAdapter);

        // stopping swipe refresh
        srl.setRefreshing(false);
    }

    //this is called from main activity class to make it the default loaded screen
    public static MyLocationsFragment newInstance() {
        MyLocationsFragment fragment = new MyLocationsFragment();
        return fragment;
    }


    @Override
    public void onRefresh() {
        //runs populate list view and populates the list view
        populateListView();
    }
}
