package com.vanarragon.ben.locationapp.Interfaces;

import android.app.Fragment;
import android.location.Location;

/**
 * Created by jamin on 2016-11-20.
 */

public class FragmentInteraction extends Fragment{

    //interface to pass location to maps fragment
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Location location);

    }
}
