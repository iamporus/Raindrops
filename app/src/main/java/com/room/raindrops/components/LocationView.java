package com.room.raindrops.components;

/**
 * @author Purushottam Pawar
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.room.raindrops.R;
import com.room.raindrops.models.MyLocation;
import com.room.raindrops.utils.Logger;

public class LocationView extends RelativeLayout{
    private CustomTextView temperatureTextView;
    private TextView infoTextView;
    private ImageView weatherImageView;
    private ProgressBar progressBar;

    public LocationView(Context context){
        super(context);
        init(context,null);
    }
    public LocationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public LocationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LocationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.location_view, this, true);

        temperatureTextView = (CustomTextView) findViewById(R.id.temperatureTextView);
        infoTextView = (TextView) findViewById(R.id.infoTextView);
        weatherImageView = (ImageView)findViewById(R.id.weatherIconImageView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);

    }

    public void setLocation(MyLocation location){
        if(location!=null){
            try{
                if(location.getTemperature()!=null) {
                    float temp = Float.parseFloat(location.getTemperature());
                    temperatureTextView.setText(String.format("%.2f", temp) + (char) 0x00B0);
                }else{
                    temperatureTextView.setText("..");
                }
                Logger.log("Message: " + location.getMessage());
                infoTextView.setText(location.getMessage());
                weatherImageView.setImageResource(R.drawable.ds_light_rain_day_night);
                progressBar.setVisibility(View.GONE);
                invalidate();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
