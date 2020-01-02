package com.sachin.wunderfleet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sachin.wunderfleet.fragments.MapPinFragment;
import com.sachin.wunderfleet.fragments.SplashFragment;
import com.sachin.wunderfleet.api.ApiClientSingleton;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayoutContainer;
    private Stack<Fragment> fragmentStack;
    private boolean doubleBackToExitPressedOnce = false;
    private Context mcontext;
    private SplashFragment splashFragment;
    private MapPinFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mcontext = this;

        initializeView();
    }

    private void initializeView() {
        frameLayoutContainer = (FrameLayout) findViewById(R.id.container);
        fragmentStack = new Stack<Fragment>();
        loadSplashScreen();
    }

    public void loadSplashScreen() {
        if (splashFragment == null) {
            splashFragment = new SplashFragment();
        }
        if (splashFragment.isAdded()) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(frameLayoutContainer.getId(), splashFragment);
        if (fragmentStack.size() > 0) {
            fragmentStack.lastElement().onPause();
            ft.hide(fragmentStack.lastElement());
        }
        fragmentStack.push(splashFragment);
        ft.commitAllowingStateLoss();
    }

    public void loadMapView() {
        mapFragment = new MapPinFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentStack.size() > 0) {
            ft.remove(fragmentStack.lastElement());
        }
        fragmentStack.clear();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.hold);
        ft.add(frameLayoutContainer.getId(), mapFragment);
        fragmentStack.push(mapFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            super.onBackPressed();
            return;
        }
        if (fragmentStack.size() > 1) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = fragmentStack.pop();
            ft.setCustomAnimations(R.anim.hold, R.anim.exit_to_right);
            Fragment lastFragment = fragmentStack.lastElement();
            lastFragment.onPause();
            ft.remove(fragment);
            lastFragment.onResume();
            ft.show(lastFragment);
            ft.commit();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(mcontext, "Click again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiClientSingleton.resetRetrofitInstance();
    }
}
