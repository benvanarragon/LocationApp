package com.vanarragon.ben.locationapp.Fragments;

/**
 * Created by jamin on 2016-11-19.
 */

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


import com.vanarragon.ben.locationapp.Database.DBHandler;
import com.vanarragon.ben.locationapp.R;

import java.util.List;


public class ThirdFragment extends Fragment{

    View myView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.third_layout, container, false);




        return myView;
    }






}
