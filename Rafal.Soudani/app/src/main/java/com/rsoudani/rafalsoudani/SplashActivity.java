/*
 * Copyright (c) 2014. The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.rsoudani.rafalsoudani;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.WindowManager;

/**
 *  Created by Rafal Soudani on 12-01-2015.
 */
public class SplashActivity extends Activity {


    private int splashTime = 5000; //in milliseconds
    private Handler mHandler;
    private Runnable mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set up full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setVariables();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("splashTime", splashTime);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        splashTime = savedInstanceState.getInt("splashTime");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mTimer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mTimer);
    }

    private void setVariables() {
        mHandler = new Handler();
        mTimer = new Runnable() {
            @Override
            public void run() {
                if (splashTime > 0) {
                    splashTime -= 1000;
                    mHandler.postDelayed(this, 1000);
                } else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        };
    }
}
