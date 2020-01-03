package com.sachin.wunderfleet.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sachin.wunderfleet.R;
import com.sachin.wunderfleet.model.CarModel;
import com.sachin.wunderfleet.api.OnApiResponseListner;
import com.sachin.wunderfleet.api.RequestCode;
import com.sachin.wunderfleet.api.rxtask.ApiTaskInit;
import com.sachin.wunderfleet.utilities.AppUtilsMethods;
import com.sachin.wunderfleet.utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MapPinFragment extends Fragment implements  View.OnClickListener, OnApiResponseListner, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private int PERMISSION_ID = 1001;
    private GoogleMap mMap;
    private Context mcontext;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLngBounds.Builder builder;
    private Location userLocation;
    private ProgressDialog mProgressDialog;
    private HashMap<Integer, Marker> mapMarker;
    private int click = 0;
    private Marker userMarker;
    private ImageView ref_btn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapview, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        ref_btn = (ImageView) view.findViewById(R.id.ref_btn);
        ref_btn.setOnClickListener(this);
        mapFragment.getMapAsync(this);
        mcontext.registerReceiver(loadLocationBroadCast, new IntentFilter(Constants.LoadLocationBroadCast));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mcontext);
        initProgressbar();
        getLastLocation();
    }

    @Override
    public void onResponseComplete(Object clsGson, int requestCode, int responseCode) {
        if (requestCode == RequestCode.GET_CAR_OBJECTS_ALL) {
            click = 0;
            stopProgress();
            Observable.fromIterable((ArrayList<CarModel>) clsGson)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<CarModel>() {


                        @Override
                        public void onSubscribe(Disposable d) {
                            mapMarker = new HashMap<>();
                            builder = new LatLngBounds.Builder();
                            if (userLocation != null) {
                                userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                                        userLocation.getLatitude(), userLocation.getLongitude())).title("You are here")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                userMarker.setTag(000);
                                mapMarker.put(000, userMarker);
                                builder.include(userMarker.getPosition());
                                if (mFusedLocationClient != null) {
                                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                                }
                            }
                        }

                        @Override
                        public void onNext(CarModel carModel) {
                            if (carModel != null && carModel.getLon() != 0) {
                                Marker fleetAdd = mMap.addMarker(new MarkerOptions().position(new LatLng(
                                        carModel.getLat(), carModel.getLon())).title(carModel.getTitle()));
                                fleetAdd.setTag(carModel.getCarId());
                                mapMarker.put(carModel.getCarId(), fleetAdd);
                                builder.include(fleetAdd.getPosition());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            int padding = 150;
                            LatLngBounds bounds = builder.build();
                            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                @Override
                                public void onMapLoaded() {
                                    mMap.animateCamera(cu);

                                }
                            });

                        }
                    });
        }
    }

    @Override
    public void onResponseError(String errorMessage, int requestCode, int responseCode) {
        stopProgress();
        AppUtilsMethods.showAlertDialog(mcontext, "Error fetching api", errorMessage);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.setOnMarkerClickListener(this);
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        requestPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(mcontext, "User location permission denied", Toast.LENGTH_SHORT).show();
                initializeApiCall();
            }
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mcontext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    userLocation = location;
                                    initializeApiCall();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(mcontext, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mcontext);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            if (mLastLocation != null) {
                userLocation = mLastLocation;
                initializeApiCall();
            }
        }
    };

    public void initializeApiCall() {
        if (AppUtilsMethods.isNetworkAvailable(mcontext)) {
            showProgress("Please wait...");
            new ApiTaskInit().getApiTask().getAllCarObjects(this);
        } else {
            AppUtilsMethods.showAlertDialog(mcontext, "No internet available", "Please check your internet connection and try again.");
        }
    }

    public void initProgressbar() {
        mProgressDialog = new ProgressDialog(mcontext);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public void showProgress(String msg) {
        try {
            if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                mProgressDialog.setMessage(msg);
                mProgressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopProgress() {

        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null) {
            if ((Integer) marker.getTag() == 000) {
                return false;
            }
        }
        if (click == 0) {
            Map<Integer, Marker> map = mapMarker;
            map.remove((Integer) marker.getTag());
            map.remove(000);
            for (Marker value : map.values()) {
                value.remove();
            }
            click = 1;
            if (userMarker != null) {
                mMap.addPolyline(new PolylineOptions()
                        .add(userMarker.getPosition(), marker.getPosition())
                        .width(5)
                        .color(Color.RED));
            }
        } else {
            FragmentManager fm = getChildFragmentManager();
            CarDetailsDialogFragment carDetailsDialogFragment = CarDetailsDialogFragment.newInstance((Integer) marker.getTag());
            carDetailsDialogFragment.show(fm, "custom_dialog");
        }
        return false;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mcontext.unregisterReceiver(loadLocationBroadCast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BroadcastReceiver loadLocationBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getBooleanExtra("loadlist", false) == true) {
                mMap.clear();
                initializeApiCall();
        }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ref_btn:
                getLastLocation();
                break;

        }
    }
}
