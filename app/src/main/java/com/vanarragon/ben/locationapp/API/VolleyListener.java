package com.vanarragon.ben.locationapp.API;

import com.vanarragon.ben.locationapp.Database.Location;

import java.util.List;



/**
 * Created by ddrohan on 19/07/2016.
 */
public interface VolleyListener {
    void setList(List list);
    void setLocation(Location location);
}
