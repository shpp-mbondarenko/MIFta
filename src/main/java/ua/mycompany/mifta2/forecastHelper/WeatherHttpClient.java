package ua.mycompany.mifta2.forecastHelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Maxim on 25.05.2016.
 */
public class WeatherHttpClient {

    private static String CURRENT_DAY_BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&q=";
    private static String TAIL_FOR_URL = "&APPID=9f7469192b24a6bc25e063b35f700587";
    private static String IMG_URL = "http://openweathermap.org/img/w/";


    public String getCurrentDayWeatherData(String location) {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            Log.d("myLog", "URL is CURRENT " +CURRENT_DAY_BASE_URL + location + TAIL_FOR_URL);
            con = (HttpURLConnection) ( new URL(CURRENT_DAY_BASE_URL + location + TAIL_FOR_URL)).openConnection();

            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (  (line = br.readLine()) != null )
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
        return null;
    }

    public String getForecastWeatherData(String location) {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            Log.d("myLog", "URL is FORECAST - " + FORECAST_BASE_URL + location + TAIL_FOR_URL);
            con = (HttpURLConnection) ( new URL(FORECAST_BASE_URL + location + TAIL_FOR_URL)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (  (line = br.readLine()) != null )
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
        return null;
    }

    /**
     * Get bitmap from URL.
     */
    public Bitmap getWeatherIcon(String src) {
        try {
            java.net.URL url = new java.net.URL(IMG_URL + src + ".png");
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap resBitmap = BitmapFactory.decodeStream(input);

            Log.d("myLog", "Picture height - " + resBitmap.getHeight() + " width - " + resBitmap.getWidth());
            //resize bitmap
             Bitmap resized = Bitmap.createScaledBitmap(resBitmap, 100, 100, true);
            return resized;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("myLog", "Picture not loaded");
            return null;
        }
    }


}
