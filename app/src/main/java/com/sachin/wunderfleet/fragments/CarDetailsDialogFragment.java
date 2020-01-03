package com.sachin.wunderfleet.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.sachin.wunderfleet.MainActivity;
import com.sachin.wunderfleet.R;
import com.sachin.wunderfleet.model.CarModel;
import com.sachin.wunderfleet.model.QuickRentResponseModel;
import com.sachin.wunderfleet.api.OnApiResponseListner;
import com.sachin.wunderfleet.api.RequestCode;
import com.sachin.wunderfleet.api.rxtask.ApiTaskInit;
import com.sachin.wunderfleet.utilities.AppUtilsMethods;
import com.sachin.wunderfleet.utilities.Constants;


public class CarDetailsDialogFragment extends DialogFragment implements OnApiResponseListner, View.OnClickListener {
    private Context mcontext;
    private int carId;
    private ProgressDialog mProgressDialog;
    private ImageView carImageView;
    private TextView carid_tv, title_tv, iscleant_tv, liceplate_tv, fuellevel_tv, vehsid_tv, pricingtime_tv, priceparking_tv, isactivated_tv, locationid_tv, add_tv, zipcode_tv, city_tv, latlong_tv, resvstate_tv, damagedesc_tv, iscardam_tv;
    private TextView vehicle_head_tv;
    private Button cancel_btn, qrent_btn;
    private RelativeLayout prg_rel;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext = context;
    }

    public static CarDetailsDialogFragment newInstance(int id) {
        CarDetailsDialogFragment frag = new CarDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putInt("car_id", id);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        carId = getArguments().getInt("car_id", 0);
        return inflater.inflate(R.layout.fragment_custom_dialog, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);

    }

    private void initialize(View view) {
        initProgressbar();
        prg_rel = (RelativeLayout) view.findViewById(R.id.prg_rel);
        carImageView = (ImageView) view.findViewById(R.id.car_imgv);
        carid_tv = (TextView) view.findViewById(R.id.carid_tv);
        title_tv = (TextView) view.findViewById(R.id.title_tv);
        iscleant_tv = (TextView) view.findViewById(R.id.iscleant_tv);
        fuellevel_tv = (TextView) view.findViewById(R.id.fuellevel_tv);
        vehsid_tv = (TextView) view.findViewById(R.id.vehsid_tv);
        pricingtime_tv = (TextView) view.findViewById(R.id.pricingtime_tv);
        priceparking_tv = (TextView) view.findViewById(R.id.priceparking_tv);
        isactivated_tv = (TextView) view.findViewById(R.id.isactivated_tv);
        locationid_tv = (TextView) view.findViewById(R.id.locationid_tv);
        add_tv = (TextView) view.findViewById(R.id.add_tv);
        zipcode_tv = (TextView) view.findViewById(R.id.zipcode_tv);
        city_tv = (TextView) view.findViewById(R.id.city_tv);
        latlong_tv = (TextView) view.findViewById(R.id.latlong_tv);
        resvstate_tv = (TextView) view.findViewById(R.id.resvstate_tv);
        damagedesc_tv = (TextView) view.findViewById(R.id.damagedesc_tv);
        iscardam_tv = (TextView) view.findViewById(R.id.iscardam_tv);
        liceplate_tv = (TextView) view.findViewById(R.id.liceplate_tv);
        cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
        qrent_btn = (Button) view.findViewById(R.id.qrent_btn);
        vehicle_head_tv = (TextView) view.findViewById(R.id.vehicle_head_tv);
        cancel_btn.setOnClickListener(this);
        qrent_btn.setOnClickListener(this);
        new ApiTaskInit().getApiTask().getCarObjectsById(carId, this);

    }

    public void initProgressbar() {
        mProgressDialog = new ProgressDialog(getActivity());
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
    public void onResponseComplete(Object clsGson, int requestCode, int responseCode) {
        stopProgress();
        prg_rel.setVisibility(View.GONE);
        if (requestCode == RequestCode.GET_CAR_OBJECTS_BY_ID) {
            new LoadDetailsInAsync().execute(clsGson);
        } else if (requestCode == RequestCode.POST_QUICK_RENT) {
            new LoadQuickRentResponseInAsync().execute(clsGson);
        }

    }

    @Override
    public void onResponseError(String errorMessage, int requestCode, int responseCode) {
        stopProgress();
        AppUtilsMethods.showAlertDialog(mcontext, "Something went wrong.", errorMessage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.cancel_btn:
                dismiss();
                break;

            case R.id.qrent_btn:
                showProgress("Please wait...");
                new ApiTaskInit().getApiTask().quickRent(carId, this);
                break;
        }
    }

    public class LoadDetailsInAsync extends AsyncTask<Object, Void, CarModel> {
        @Override
        protected CarModel doInBackground(Object... objects) {
            try {
                CarModel carModel = (CarModel) objects[0];
                return carModel;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(CarModel carModel) {
            super.onPostExecute(carModel);
            if (carModel != null) {
                Glide.with(mcontext)
                        .load(carModel.getVehicleTypeImageUrl())
                        .placeholder(ContextCompat.getDrawable(mcontext, R.mipmap.ic_launcher_foreground))
                        .into(carImageView);
                carid_tv.setText(Integer.toString(carModel.getCarId()));
                title_tv.setText(carModel.getTitle());
                iscleant_tv.setText(Boolean.toString(carModel.getIsClean()));
                fuellevel_tv.setText(Integer.toString(carModel.getFuelLevel()));
                vehsid_tv.setText(Integer.toString(carModel.getVehicleStateId()));
                pricingtime_tv.setText(carModel.getPricingTime());
                priceparking_tv.setText(carModel.getPricingParking());
                isactivated_tv.setText(Boolean.toString(carModel.getIsActivatedByHardware()));
                locationid_tv.setText(Integer.toString(carModel.getLocationId()));
                add_tv.setText(carModel.getAddress());
                zipcode_tv.setText(carModel.getZipCode());
                city_tv.setText(carModel.getCity());
                latlong_tv.setText(Double.toString(carModel.getLat()) + "/" + Double.toString(carModel.getLon()));
                resvstate_tv.setText(Integer.toString(carModel.getReservationState()));
                damagedesc_tv.setText(carModel.getDamageDescription());
                iscardam_tv.setText(Boolean.toString(carModel.getIsDamaged()));
                liceplate_tv.setText(carModel.getLicencePlate());
                vehicle_head_tv.setText(carModel.getTitle());
            } else {
                AppUtilsMethods.showAlertDialog(mcontext, "Something went wrong.", "Please try again...");
            }
        }
    }

    public class LoadQuickRentResponseInAsync extends AsyncTask<Object, Void, QuickRentResponseModel> {
        @Override
        protected QuickRentResponseModel doInBackground(Object... objects) {
            try {
                QuickRentResponseModel quickRentResponseModel = (QuickRentResponseModel) objects[0];
                return quickRentResponseModel;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(QuickRentResponseModel quickRentResponseModel) {
            super.onPostExecute(quickRentResponseModel);
            if (quickRentResponseModel != null) {
                showSuccessAlertDialog(mcontext, "Success!", "Quick-rent is confirmed. Your reservation id is " + quickRentResponseModel.getReservationId());
            } else {
                AppUtilsMethods.showAlertDialog(mcontext, "Something went wrong.", "Please try again to quick-rent...");
            }
        }
    }

    private void showSuccessAlertDialog(Context context, String title, String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            dismiss();
                            Intent intent = new Intent(Constants.LoadLocationBroadCast);
                            intent.putExtra("loadlist", true);
                            mcontext.sendBroadcast(intent);
                        }
                    });


            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.setTitle(title);
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
