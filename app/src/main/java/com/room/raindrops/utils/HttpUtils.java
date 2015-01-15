package com.room.raindrops.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.room.raindrops.receivers.WebServiceListener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpUtils {

    public static String httpInsecureCall(String url,byte[] data) {
        URL u;
        HttpURLConnection conn = null;
        try {
            u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");


            if (data != null) {
                OutputStream output = conn.getOutputStream();
                output.write(data);
                output.close();
            } else
                conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String messageText = new String(
                        readStream(conn.getInputStream()));

                return createResponse(messageText);

            } else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String createResponse(String responseMessage) {
        String response = null;

        try {

            String res = responseMessage;

            if (res == null) {
                Logger.log("HTTP response: " + "null");
                return null;
            }

            return res;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] readStream(InputStream is) throws IOException {
        byte[] buffer = new byte[512];
        int bytesRead = 0;
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (bytesRead != -1) {
            bytesRead = bis.read(buffer);
            if (bytesRead == -1) {
                break;
            }
            bos.write(buffer, 0, bytesRead);
        }
        buffer = bos.toByteArray();
        return buffer;
    }

    public static void getCitiesLocal(final Context context,
                                         final WebServiceListener mCityResponseListener) {

        Thread t = new Thread() {
            public void run() {

                AssetManager am = context.getAssets();

                try {
                    InputStream is = am.open("city_json.txt");

                    String messageText = new String(readStream(is));

                    mCityResponseListener.onResult(messageText);

                } catch (IOException e) {
                    e.printStackTrace();
                    mCityResponseListener.onError(-1, e.getMessage());
                }
            }
        };
        t.start();
    }

}
