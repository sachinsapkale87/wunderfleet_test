package com.sachin.wunderfleet;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class UtilLocationTest {

    private String latLng;

    @Parameterized.Parameters
    public static Collection<String> testParams(){
        String[] list= new String[]{
                "10.07526,53.59301,"
                ,"9.99622,53.54847"
                ,"9.97417,53.61274"
                ,"10.07838,53.56388"

        };

        return Arrays.asList(list);
    }
    public UtilLocationTest(String latLng)
    {
        this.latLng=latLng;
    }
    @Test
    public void isValidLatLng()
    {
        Assert.assertTrue(toLatLng(latLng) != null);

    }


    public LatLng toLatLng(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        String[] pieces = value.split(",");
        float lat = Float.valueOf(pieces[0]);
        float lon = Float.valueOf(pieces[1]);
        return new LatLng(lat, lon);
    }
}
