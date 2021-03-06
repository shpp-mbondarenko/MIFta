package ua.mycompany.mifta2;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import ua.mycompany.mifta2.forecastHelper.JSONWeatherParser;
import ua.mycompany.mifta2.forecastHelper.OneDayWeather;
import ua.mycompany.mifta2.forecastHelper.WeatherHttpClient;

public class ForecastActivity extends Activity {

    private  LinearLayout forecastBottomLL;
    private LinearLayout forecastParent;

    private TextView tvCityName;
    private TextView tvCurrentTemp;
    private TextView tvCurrentWeatherCondition;
    private TextView tvCurrentWeatherDescription;
    private TextView tvCurrentPressure;
    private TextView tvCurrentHumidity;
    private TextView tvCurrentMinMaxTemp;

    private Button btnUpdate;

    private Spinner spinner;

    private ImageView ivCurrentWeatherImage;

    private String city;

    private String[] listOfСities;

    private JSONWeatherTask task;

    private Realm activityRealm;
    private Realm asyncRealm;

    //saving data if have no internet
    private static final String CURRENT_W = "current";
    private static final String FORECAST_W = "forecast";
    private final String WEATHER_TYPE = "typeOfWeather";
    private OneDayWeather weather;
    private OneDayWeather[] forecastWeather;
    private String currentDayWeatherData;
    private String forecastWeatherData;
    private RealmConfiguration realmConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast_layout);
        forecastBottomLL = (LinearLayout) findViewById(R.id.forecastLinearLayout);
        Log.d("myLog", "onCreate forecast");
        listOfСities = getResources().getStringArray(R.array.City);
        city = listOfСities[0];

        // Create the Realm configuration
        realmConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfig);
        activityRealm = Realm.getInstance(realmConfig);

        findViewObjects();
        configureCitySpinner();

        weather = new OneDayWeather();
        forecastWeather = new OneDayWeather[7];

        RealmResults<OneDayWeather> checkDB =
                activityRealm.where(OneDayWeather.class).equalTo(WEATHER_TYPE, CURRENT_W).findAll();
        Log.d("myLog", "RESULTS - " + checkDB.isEmpty() + " size - " + checkDB.size());

        //Important. At first launch: If we have Internet connection - download data from internet.
        //If don't have Internet connection, but have data in REALM DB - retrieve data from REALM DB.
        //If application is launching at first time and we dont have internet connection and don't have data in
        //DB - go to errorActivity.
        if (isNetworkAvailable()){
            task = new JSONWeatherTask();
            task.execute(city);
            Log.d("myLog", "RESULTS EXECUTE DOWNLOADING");
        } else if (!(checkDB.isEmpty()) /*&& !(isNetworkAvailable())*/){
            loadWeatherData(activityRealm);
            buildCurrentWeather();
            buildWeatherForecast();
            Log.d("myLog", "RESULTS is not EMPTY");
        } else {
            Log.d("myLog", "GO to ERROR_A");
            Intent goToERR = new Intent(getApplicationContext(), ErrorActivity.class);
            startActivity(goToERR);
        }
    }



    private class JSONWeatherTask extends AsyncTask<String, Void, OneDayWeather> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected OneDayWeather doInBackground(String... params) {

            asyncRealm = Realm.getDefaultInstance();

            currentDayWeatherData = ((new WeatherHttpClient()).getCurrentDayWeatherData(params[0]));
            forecastWeatherData = ((new WeatherHttpClient()).getForecastWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getOneDayWeather(currentDayWeatherData);
                if (currentDayWeatherData != null) {
                    publishProgress();
                }
                forecastWeather = JSONWeatherParser.getForecastWeather(forecastWeatherData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            saveCurrentWeatherData(asyncRealm, weather);
            saveForecastWeatherData(asyncRealm, forecastWeather);

            asyncRealm.close();
            return weather;

        }

        //Show information about current day (Top part of screen)
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            buildCurrentWeather();
        }

        @Override
        protected void onPostExecute(OneDayWeather weather) {
            super.onPostExecute(weather);
            buildWeatherForecast();
        }
    }

    //Show user forecast information. Build bottom part of activity.
    private void buildWeatherForecast() {
        Log.d("myLog", "In buildWeatherForecast ");
        for (int i = 0; i < 7; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            params.setMargins(2, 2, 2, 2);
            OneDayWeather oneDayWeather = forecastWeather[i];
            LinearLayout oneForecast = new LinearLayout(getApplicationContext());
            oneForecast.setLayoutParams(params);
            oneForecast.setOrientation(LinearLayout.VERTICAL);
            oneForecast.setMinimumHeight(100);

            oneForecast.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_for_linear_layout));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = new Date();
            try {
                myDate = simpleDateFormat.parse(oneDayWeather.getDate());
                Log.d("time", myDate.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            TextView tvDate = new TextView(getApplicationContext());
            tvDate.setText(myDate.toString().substring(0, 10));
            tvDate.setTextColor(Color.BLACK);
            oneForecast.addView(tvDate);

            ImageView forecastIcon = new ImageView(getApplicationContext());
            forecastIcon.setImageDrawable(setCurrentWeatherImage(oneDayWeather.getIcon()));
            forecastIcon.setMaxHeight(100);
            forecastIcon.setMaxWidth(100);
            oneForecast.addView(forecastIcon);

            TextView tvTemp = new TextView(getApplicationContext());
            tvTemp.setText("Temp " + oneDayWeather.getMinTemp() + "/" + oneDayWeather.getMaxTemp() + "°C");
            tvTemp.setTextColor(Color.BLACK);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                oneForecast.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                tvDate.setPadding(20, 0, 0, 0);
                tvTemp.setPadding(20, 0, 0, 0);
                tvDate.setTextSize((int) getResources().getDimension(R.dimen.font_size_forecast));
                tvTemp.setTextSize((int) getResources().getDimension(R.dimen.font_size_forecast));
            } else {
                tvDate.setPadding((int) getResources().getDimension(R.dimen.font_padding), 0, 0, 0);
                tvTemp.setPadding((int) getResources().getDimension(R.dimen.font_padding), 0, 0, 0);
                tvDate.setTextSize((int) getResources().getDimension(R.dimen.font_size_forecast));
                tvTemp.setTextSize((int) getResources().getDimension(R.dimen.font_size_forecast));
            }

            oneForecast.addView(tvTemp);

            forecastBottomLL.addView(oneForecast);
        }
    }

    //Show information about current weather to user. SetText to textViews.
    private void buildCurrentWeather() {
        tvCityName.setText(weather.getCity() + ", " + weather.getCountry());
        tvCurrentTemp.setText(weather.getTemp() + " °C");
        tvCurrentWeatherCondition.setText(weather.getCondition());
        tvCurrentWeatherDescription.setText(weather.getDescription());
        tvCurrentPressure.setText("Pressure " + Integer.toString((int) weather.getPressure()) + "hPa");
        tvCurrentHumidity.setText("Humidity " + Integer.toString((int) weather.getHumidity()) + "%");
        tvCurrentMinMaxTemp.setText("Temp Min/Max " + weather.getMinTemp() + "°C/" + weather.getMaxTemp() + "°C");
        ivCurrentWeatherImage.setImageDrawable(setCurrentWeatherImage(weather.getIcon()));
    }

    //Saving data in case if user don't have internet connection
    private void saveForecastWeatherData(Realm realm, OneDayWeather[] wthr) {
        Log.d("myLog", "In saveForecastWeatherData");
        final RealmResults<OneDayWeather> results =
                realm.where(OneDayWeather.class).equalTo(WEATHER_TYPE, FORECAST_W).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });

        realm.beginTransaction();
        for (int i = 0; i < 7; i++) {
            OneDayWeather fw = realm.copyToRealm(wthr[i]);
            fw.setDate(wthr[i].getDate());
        }
        realm.commitTransaction();
    }

    private void loadWeatherData(Realm realm){
        //load current weather
        RealmResults<OneDayWeather> results =
                realm.where(OneDayWeather.class).equalTo(WEATHER_TYPE, CURRENT_W).findAll();
        weather = results.get(0);

        //load forecast
        final RealmResults<OneDayWeather> forecastRes =
                realm.where(OneDayWeather.class).equalTo(WEATHER_TYPE, FORECAST_W).findAll();
        forecastWeather = new OneDayWeather[7];
        int b = 0;
        for (OneDayWeather odw : forecastRes){
            forecastWeather[b] = forecastRes.get(b);
            b++;
        }
        Log.d("myLog", "In LoadWeatherData");
    }


    //    private void saveCurrentWeatherData(Realm realm, OneDayWeather wthr) {
    private void saveCurrentWeatherData(Realm realm, OneDayWeather wthr) {
        final RealmResults<OneDayWeather> results =
                realm.where(OneDayWeather.class).equalTo(WEATHER_TYPE, CURRENT_W).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
        realm.beginTransaction();
        OneDayWeather tmp = realm.copyToRealm(wthr);
        realm.commitTransaction();
        Log.d("myLog", "SAVING data in saveCurrentWeatherData. Results is NULL");
    }

    //check network connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityRealm.close();
    }

    //Set adapter to spinner. Choose city for weather forecast
    private void configureCitySpinner() {
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ForecastActivity.this,
                android.R.layout.simple_spinner_item, listOfСities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = listOfСities[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    //Finding all Views on forecast screen
    private void findViewObjects() {
        forecastParent = (LinearLayout) findViewById(R.id.forecastParent);
        tvCityName = (TextView) findViewById(R.id.tvCityName);
        tvCurrentTemp = (TextView) findViewById(R.id.tvCurrentTemp);
        tvCurrentWeatherCondition = (TextView) findViewById(R.id.tvCurrentWeatherCondition);
        tvCurrentWeatherDescription = (TextView) findViewById(R.id.tvCurrentWeatherDescription);
        tvCurrentPressure = (TextView) findViewById(R.id.tvCurrentPressure);
        tvCurrentHumidity = (TextView) findViewById(R.id.tvCurrentHumidity);
        tvCurrentMinMaxTemp = (TextView) findViewById(R.id.tvCurrentMinMaxTemp);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        ivCurrentWeatherImage = (ImageView) findViewById(R.id.ivCurrentWeatherImage);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnUpdate) {
                    forecastBottomLL.removeAllViews();
                    if (isNetworkAvailable()) {
                        task = new JSONWeatherTask();
                        task.execute(city);
                    } else {
                        loadWeatherData(activityRealm);
                        buildCurrentWeather();
                        buildWeatherForecast();

                    }
                }
            }
        });
    }

    //Select right image, to show on weather image
    private Drawable setCurrentWeatherImage(String icon) {
        String wIcon = "w" + icon;
        switch (wIcon) {
            case "w01d":
                return getResources().getDrawable(R.drawable.w01d);
            case "w02d":
                return getResources().getDrawable(R.drawable.w02d);
            case "w03d":
                return getResources().getDrawable(R.drawable.w03d);
            case "w04d":
                return getResources().getDrawable(R.drawable.w04d);
            case "w09d":
                return getResources().getDrawable(R.drawable.w09d);
            case "w10d":
                return getResources().getDrawable(R.drawable.w10d);
            case "w11d":
                return getResources().getDrawable(R.drawable.w11d);
            case "w13d":
                return getResources().getDrawable(R.drawable.w13d);
            case "w50d":
                return getResources().getDrawable(R.drawable.w50d);
            default:
                return getResources().getDrawable(R.drawable.w00d);

        }

    }
}


