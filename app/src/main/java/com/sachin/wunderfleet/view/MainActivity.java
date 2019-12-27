package com.sachin.wunderfleet.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sachin.wunderfleet.R;
import com.sachin.wunderfleet.fragments.MapFragment;
import com.sachin.wunderfleet.fragments.SplashScreenFragment;
import com.sachin.wunderfleet.net.rxtask.ApiTask;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FrameLayout frameLayoutContainer;
    private FragmentTransaction fragmentTransaction;
    private Stack<Fragment> fragmentStack;
    private boolean doubleBackToExitPressedOnce = false;
    private Context mcontext;

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
        Fragment splashFragment = new SplashScreenFragment();
        loadFragments(splashFragment, true, false, null);
    }

    public void loadFragments(Fragment fragment, boolean addtobackstack, boolean replace, Bundle bundle) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if(!(fragment instanceof SplashScreenFragment)){
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.hold);
        }
        if(fragment instanceof MapFragment){
            fragmentStack.clear();
        }

        if (replace) {
            fragmentTransaction.replace(R.id.container, fragment);
        } else {
            fragmentTransaction.add(R.id.container, fragment);
        }
        if (bundle != null)
            fragment.setArguments(bundle);
        if (addtobackstack) {
            fragmentTransaction.addToBackStack("FRAG");
        }
        if (fragmentStack.size() > 0) {
            fragmentStack.lastElement().onPause();
            fragmentTransaction.hide(fragmentStack.lastElement());
        }
        fragmentStack.push(fragment);

        fragmentTransaction.commit();
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
}
