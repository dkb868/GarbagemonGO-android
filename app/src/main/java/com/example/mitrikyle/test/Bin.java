package com.example.mitrikyle.test;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by mitrikyle on 9/18/2016.
 */
public class Bin {
    public LatLng latLng;
    public List<Integer> frequencies;

    public Bin(LatLng l, List<Integer> f){
        latLng = l;
        frequencies = f;
    }
}
