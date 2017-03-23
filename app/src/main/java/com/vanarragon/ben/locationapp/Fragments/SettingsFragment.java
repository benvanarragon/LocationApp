package com.vanarragon.ben.locationapp.Fragments;

/**
 * Created by jamin on 2016-11-19.
 */

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.vanarragon.ben.locationapp.Adapters.CustomRecycleAdapter;
import com.vanarragon.ben.locationapp.Database.Location;
import com.vanarragon.ben.locationapp.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment{

    View myView;
    private static final String TAG = "RecyclerViewExample";
    private List<Location> locationList;
    private RecyclerView mRecyclerView;
    private CustomRecycleAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.settings_layout, container, false);
        //instantiate
        locationList = new ArrayList<Location>();
        Location l = new Location();

        locationList  = l.getResults();

        CustomRecycleAdapter cra = new CustomRecycleAdapter(getActivity(), locationList);

        mRecyclerView = (RecyclerView)myView.findViewById(R.id.rv);
        mRecyclerView.hasFixedSize();

        mRecyclerView.setAdapter(cra);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        return myView;
    }


}
