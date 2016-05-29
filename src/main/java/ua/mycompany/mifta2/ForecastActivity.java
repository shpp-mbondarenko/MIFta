package ua.mycompany.mifta2;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import ua.mycompany.mifta2.forecastHelper.JSONWeatherParser;
import ua.mycompany.mifta2.forecastHelper.OneDayWeather;
import ua.mycompany.mifta2.forecastHelper.WeatherHttpClient;

public class ForecastActivity extends Activity {

    LinearLayout forecastLL;

    TextView tvCityName;
    TextView tvCurrentTemp;
    TextView tvCurrentWeatherCondition;
    TextView tvCurrentWeatherDescription;
    TextView tvCurrentPressure;
    TextView tvCurrentHumidity;
    TextView tvCurrentMinMaxTemp;

    Button btnUpdate;
    Button btnSelectCity;

    ImageView ivCurrentWeatherImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast_layout);
        forecastLL = (LinearLayout) findViewById(R.id.forecastLinearLayout);
        Log.d("myLog", "onCreate forecast");
        String city = "Kirovohrad,UA";

        findViewObjects();

        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{city});
    }

    //finding all Views on forecast screen
    private void findViewObjects() {
        tvCityName = (TextView) findViewById(R.id.tvCityName);
        tvCurrentTemp = (TextView) findViewById(R.id.tvCurrentTemp);
        tvCurrentWeatherCondition = (TextView) findViewById(R.id.tvCurrentWeatherCondition);
        tvCurrentWeatherDescription = (TextView) findViewById(R.id.tvCurrentWeatherDescription);
        tvCurrentPressure = (TextView) findViewById(R.id.tvCurrentPressure);
        tvCurrentHumidity = (TextView) findViewById(R.id.tvCurrentHumidity);
        tvCurrentMinMaxTemp = (TextView) findViewById(R.id.tvCurrentMinMaxTemp);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnSelectCity = (Button) findViewById(R.id.btnSelectCity);

        ivCurrentWeatherImage = (ImageView) findViewById(R.id.ivCurrentWeatherImage);
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, OneDayWeather> {

        OneDayWeather weather;
        OneDayWeather[] forecastWeather;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected OneDayWeather doInBackground(String... params) {
            weather = new OneDayWeather();
            forecastWeather = new OneDayWeather[7];
            String currentDayWeatherData = ( (new WeatherHttpClient()).getCurrentDayWeatherData(params[0]));
            String forecastWeatherData = ( (new WeatherHttpClient()).getForecastWeatherData(params[0]));
            try {
                if (currentDayWeatherData == null && forecastWeatherData == null) {
                    Log.d("myLog", "DATA IS NULL");
                } else {
                    Log.d("myLog","DATA NOT NULL");}

                weather = JSONWeatherParser.getOneDayWeather(currentDayWeatherData);
                publishProgress();
                forecastWeather = JSONWeatherParser.getForecastWeather(forecastWeatherData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }

        //Show information about current day (Top part of screen)
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            tvCityName.setText(weather.getCity() + ", " + weather.getCountry());
            tvCurrentTemp.setText(weather.getTemp() + " °C");
            tvCurrentWeatherCondition.setText(weather.getCondition());
            tvCurrentWeatherDescription.setText(weather.getDescription());
            tvCurrentPressure.setText(Integer.toString((int) weather.getPressure()));
            tvCurrentHumidity.setText(Integer.toString((int) weather.getHumidity()));
            tvCurrentMinMaxTemp.setText("Temp Min/Max " + weather.getMinTemp() + "/" + weather.getMaxTemp());
        }

        @Override
        protected void onPostExecute(OneDayWeather weather) {
            super.onPostExecute(weather);

            for (int i = 0; i < 7; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
                params.setMargins(0, 0, 2, 0);
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
                    Log.d("Time", myDate.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                TextView tvDate = new TextView(getApplicationContext());
                tvDate.setText(myDate.toString().substring(0, 10));
                oneForecast.addView(tvDate);

                ImageView forecastIcon = new ImageView(getApplicationContext());
                forecastIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.w02d));
                forecastIcon.setMaxHeight(100);
                forecastIcon.setMaxWidth(100);
                oneForecast.addView(forecastIcon);

                TextView temp = new TextView(getApplicationContext());
                temp.setText("Temp " + oneDayWeather.getMinTemp() + "/" + oneDayWeather.getMaxTemp() + "°C");
                oneForecast.addView(temp);

                forecastLL.addView(oneForecast);
            }
        }
    }
}


