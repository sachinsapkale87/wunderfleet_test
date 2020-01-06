package com.sachin.wunderfleet.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapPinFragment extends Fragment implements View.OnClickListener, OnApiResponseListner, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int REQUEST_CHECK_SETTINGS = 1002;
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
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

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
        mFusedLocationClient = getFusedLocationProviderClient(mcontext);
        mSettingsClient = LocationServices.getSettingsClient(mcontext);
        initProgressbar();
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        getLastLocation();
    }


    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    userLocation = locationResult.getLastLocation();
                    initializeApiCall();
                }

            }
        };
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
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
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
                startLocationUpdates();
            } else {
                startLocationUpdates();
            }
        } else {
            requestPermissions();
        }
    }


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
                initializeApiCall();
                break;

        }
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    e.printStackTrace();
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Toast.makeText(mcontext, "Unable to fetch user location", Toast.LENGTH_SHORT).show();
                                initializeApiCall();
                        }


                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() { // starting the listern on a new thread witha  delay of half second in case the process is very fast for detecting location
                                startLocationUpdates();
                            }
                        }, 500);
                        break;
                    case Activity.RESULT_CANCELED:

                        Toast.makeText(mcontext, "Permission denied", Toast.LENGTH_SHORT).show();
                        initializeApiCall();
                        break;
                }
                break;
        }
    }
}
