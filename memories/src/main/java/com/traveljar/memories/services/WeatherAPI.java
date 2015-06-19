package com.traveljar.memories.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abhi on 03/06/15.
 */
public class WeatherAPI {

    private static final String TAG = "<WeatherAPI>";
    private double lat;
    private double longi;
    private Weather mWeather;

    private Context context;

    // Reference of class implementing WeatherInfo interface
    private WeatherInfo mListener;

    public WeatherAPI(Context context, WeatherInfo listener) {
        this.context = context;
        mListener = listener;
    }

    public void getWeather() {

        GPSTracker gps = new GPSTracker(context);
        if (gps.canGetLocation()) {
            lat = gps.getLatitude(); // returns latitude
            longi = gps.getLongitude(); // returns longitude
        } else {
            Log.d(TAG, "not able to connect to the internet");
        }

        String url = Constants.URL_WEATHER_OWM_GET_CURRENT_WEATHER + "?units=metric&lat=" + lat + "&lon=" + longi;

        // Request a string response from the provided URL.
        CustomJsonRequest signUpReg = new CustomJsonRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "=====" + response.toString());
                        mListener.getWeatherInfo(parseJSONResponse(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Unable to get weather situation with volley error -> " + error);
            }
        });

        // Add the request to the RequestQueue.
        AppController.getInstance().getRequestQueue().add(signUpReg);
    }

    private Weather parseJSONResponse(JSONObject res) {
        Weather weatherItem = new Weather();

        try {
            weatherItem.tempInCelcius = res.getJSONObject("main").getString("temp");
            weatherItem.weatherDescp = ((JSONObject) res.getJSONArray("weather").get(0)).getString("description");
            weatherItem.weatherMain = ((JSONObject) res.getJSONArray("weather").get(0)).getString("main");
            weatherItem.weatherIconCode = ((JSONObject) res.getJSONArray("weather").get(0)).getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weatherItem;
    }

    public interface WeatherInfo {
        void getWeatherInfo(Weather weather);
    }

    public class Weather {
        private String tempInCelcius;
        private String weatherDescp;
        private String weatherMain;
        private String weatherIconCode;

        public String getTempInCelcius() {
            return tempInCelcius;
        }

        public String getWeatherDescp() {
            return weatherDescp;
        }

        public String getWeatherMain() {
            return weatherMain;
        }

        public String getWeatherIconCode() {
            return weatherIconCode;
        }

    }

}
