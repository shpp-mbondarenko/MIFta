package ua.mycompany.mifta2.forecastHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Maxim on 24.05.2016.
 */
public class JSONWeatherParser {

    private static final String CURRENT_W = "current";
    private static final String FORECAST_W = "forecast";

    public static OneDayWeather getOneDayWeather(String data) throws JSONException {
        OneDayWeather weather = new OneDayWeather();

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);

        JSONObject sysObj = getObject("sys", jObj);
        weather.setCountry(getString("country", sysObj));
        weather.setCity(getString("name", jObj));
        weather.setDate(getString("dt", jObj));

        // Get weather info (This is an array)
        JSONArray jArr = jObj.getJSONArray("weather");

        // We use only the first value
        JSONObject JSONWeather = jArr.getJSONObject(0);

        weather.setDescription(getString("description", JSONWeather));
        weather.setCondition(getString("main", JSONWeather));
        weather.setIcon(getString("icon", JSONWeather));

        JSONObject mainObj = getObject("main", jObj);
        weather.setHumidity(getFloat("humidity", mainObj));
        weather.setPressure(getInt("pressure", mainObj));
        weather.setMaxTemp((int) (getInt("temp_max", mainObj) - 273.15));
        weather.setMinTemp((int) (getInt("temp_min", mainObj) - 273.15));
        weather.setTemp((int) (getFloat("temp", mainObj)- 273.15));

        // Wind
        JSONObject wObj = getObject("wind", jObj);
        weather.setWindSpeed(getFloat("speed", wObj));
        weather.setWindDirection(getFloat("deg", wObj));

        //set Type
        weather.setTypeOfWeather(CURRENT_W);


        return weather;
    }

    public static OneDayWeather[] getForecastWeather(String data) throws JSONException {
        OneDayWeather[] weather = new OneDayWeather[7];

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);

        //get all forecast data for  days
        JSONArray forecastArr = jObj.getJSONArray("list");

        for (int i = 0; i < 7; i++) {
            OneDayWeather odw  = new OneDayWeather();

            // get forecast for one day from forecastArr
            JSONObject oneDayData = forecastArr.getJSONObject(i);

            //extract date from object
            odw.setDate(getString("dt", oneDayData));

            //get main data such as icon, main weather situation, description
            JSONArray oneDayWeather = oneDayData.getJSONArray("weather");
            odw.setIcon(getString("icon", oneDayWeather.getJSONObject(0)));
            odw.setDescription(getString("description", oneDayWeather.getJSONObject(0)));
            odw.setCondition(getString("main", oneDayWeather.getJSONObject(0)));

            //get temperature
            JSONObject oneDayTemperature = oneDayData.getJSONObject("temp");
            odw.setMaxTemp((int) (getFloat("max", oneDayTemperature) - 273.15));
            odw.setMinTemp((int) (getFloat("min", oneDayTemperature) - 273.15));
            odw.setTemp((int) (getFloat("day", oneDayTemperature) - 273.15));
            odw.setTypeOfWeather(FORECAST_W);

            // Send in the log what retrieved, be sure that i get something
//            Log.d("myLog", "DATE " + i + " - " + odw.getDate());
//            Log.d("myLog", "ICON " + i + " - " + odw.getIcon());
//            Log.d("myLog", "DESC " + i + " - " + odw.getDescription());
//            Log.d("myLog", "DAY MIN " + i + " - " + odw.getMinTemp());
//            Log.d("myLog", "DAY MAX " + i + " - " + odw.getMaxTemp());

            weather[i] = odw;
        }

        return weather;
    }


    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

}
