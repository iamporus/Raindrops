package com.room.raindrops.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.room.raindrops.R;
import com.room.raindrops.components.CustomTextView;
import com.room.raindrops.utils.RaindropsPrefs;

import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends Activity implements View.OnClickListener {

    int i = 0;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (new RaindropsPrefs().isFirstTime(this))
            setContentView(R.layout.splash_layout);
        else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        initAnimation();
        initViews();

    }

    private void initViews() {

        CustomTextView addMyCityTextView = (CustomTextView) findViewById(R.id.addMyCityTextView);
        CustomTextView selectCityGPSTextView = (CustomTextView) findViewById(R.id.selectCityGPSTextView);

        addMyCityTextView.setOnClickListener(this);
        selectCityGPSTextView.setOnClickListener(this);
    }

    private void initAnimation() {
        final ImageView iv1 = (ImageView) findViewById(R.id.imageView);
        timer = new Timer();

        try {
            BitmapDrawable frame1 = (BitmapDrawable) getResources().getDrawable(
                    R.drawable.ds_rain_day_night);
            BitmapDrawable frame2 = (BitmapDrawable) getResources().getDrawable(
                    R.drawable.ds_light_rain_day_night);
            BitmapDrawable frame3 = (BitmapDrawable) getResources().getDrawable(
                    R.drawable.ds_scattered_showers_day_night);

            final BitmapDrawable[] frames = {frame1, frame2, frame3};

            final Handler handler = new Handler();


            TimerTask task = new TimerTask() {
                @Override
                public void run() {


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                iv1.setImageDrawable(frames[i]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    i++;
                    if (i >= 3)
                        i = 0;

                }
            };


            timer.scheduleAtFixedRate(task, 500, 200);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.addMyCityTextView:

                startActivity(new Intent(this, AddCityActivity.class));
                break;
            case R.id.selectCityGPSTextView:

                startActivity(new Intent(this, CurrentLocationActivity.class));

                break;
        }
    }


}