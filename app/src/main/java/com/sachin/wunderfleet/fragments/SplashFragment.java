package com.sachin.wunderfleet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sachin.wunderfleet.R;
import com.sachin.wunderfleet.MainActivity;
import com.sachin.wunderfleet.utilities.AppUtilsMethods;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class SplashFragment extends Fragment {
    CompositeDisposable compositeDisposable;
    Context mcontext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getView() != null ? getView() : inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!AppUtilsMethods.isNetworkAvailable(mcontext)) {
            AppUtilsMethods.showAlertDialog(mcontext, "No internet", "Please check internet connection");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
            compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(Observable
                    .timer(1200, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            ((MainActivity) getActivity()).loadMapView();
                        }
                    }));

    }

    @Override
    public void onPause() {
        super.onPause();
        compositeDisposable.dispose();
    }
}
