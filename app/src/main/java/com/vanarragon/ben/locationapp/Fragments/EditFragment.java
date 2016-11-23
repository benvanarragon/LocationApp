package com.vanarragon.ben.locationapp.Fragments;

/**
 * Created by jamin on 2016-11-19.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vanarragon.ben.locationapp.R;

public class EditFragment extends Fragment{
    String id,lat,longString,action,date,privacy,simpleLoc;
    View myView;
    private TextView tvTest;

    //layout stuff
    private TextView lblLocation;
    private TextView lblDate;
    private EditText activityTextBox;
    private RadioGroup rg;
    private RadioButton rb, rbDefault;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.layout_edit, container, false);

        lblLocation = (TextView) myView.findViewById(R.id.lblLocation);
        lblDate = (TextView) myView.findViewById(R.id.lblDate) ;
        activityTextBox = (EditText) myView.findViewById(R.id.activityTextBox);
        rg = (RadioGroup) myView.findViewById(R.id.rgPrivacy);
        rbDefault = (RadioButton) myView.findViewById(R.id.rb_public);


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
            //Toast.makeText(getActivity(), "text: " + id , Toast.LENGTH_LONG).show();


            lblDate = (TextView) myView.findViewById(R.id.lblDate) ;
            activityTextBox = (EditText) myView.findViewById(R.id.activityTextBox);
            rg = (RadioGroup) myView.findViewById(R.id.rgPrivacy);
            rbDefault = (RadioButton) myView.findViewById(R.id.rb_public);

            lblLocation.setText(simpleLoc);
            lblDate.setText(date);
            activityTextBox.setText(action);
        }

        return myView;
    }
}
