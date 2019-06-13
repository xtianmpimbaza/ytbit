/*
 * Copyright (c) 2016. Truiton (http://www.truiton.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Mohit Gupt (https://github.com/mohitgupt)
 *
 */

package com.truiton.bnuwallet.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.truiton.bnuwallet.HomeFragment;
import com.truiton.bnuwallet.R;
import com.truiton.bnuwallet.RequestFragment;
import com.truiton.bnuwallet.SendFragment;
import com.truiton.bnuwallet.helper.BottomNavigationBehavior;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_id);
//        toolbar.setTitle(R.string.toolbar_title);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        Intent intent = getIntent();
        //check if we are from scanning the address
        if (intent.hasExtra("editTextValue")) {
            Bundle bundle = new Bundle();
            bundle.putString("editTextValue", intent.getStringExtra("editTextValue"));
            Log.e("Scanned data", intent.getStringExtra("editTextValue"));
            SendFragment fragobj = new SendFragment();
            fragobj.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragobj).commit();

        }else {
            // load the store fragment by default
            toolbar.setTitle("Home");
            loadFragment(new HomeFragment());
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_item_home:
//                    toolbar.setTitle("Home");
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.action_item_receive:
//                    toolbar.setTitle("Receive");
                    fragment = new RequestFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.action_item_send:
//                    toolbar.setTitle("Send");
                    fragment = new SendFragment();
                    loadFragment(fragment);
                    return true;
            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (isInHomeFragment() && !doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Double tap to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else if (isInHomeFragment()) {
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isInHomeFragment() {
        for (Fragment item : getSupportFragmentManager().getFragments()) {
            if (item.isVisible() && "HomeFragment".equals(item.getClass().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

}
