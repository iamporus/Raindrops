package com.room.raindrops.utils;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class UpdaterIntentService extends IntentService{

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *

     */

    public UpdaterIntentService() {
        super("UpdaterIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String url = intent.getStringExtra("url");
        ResultReceiver mReceiver = (ResultReceiver) intent.getParcelableExtra("listener");
        //do the heavy work
        Logger.log("HTTP call to : " + url);
        String response = HttpUtils.httpInsecureCall(url,null);
        Logger.log("Current weather Response: " + response);

        if(mReceiver!=null) {
            Bundle result = new Bundle();
            result.putString("result",response);
            mReceiver.send(0, result);
        }
        else{
            Logger.log("Listener is null");
        }

    }
}
