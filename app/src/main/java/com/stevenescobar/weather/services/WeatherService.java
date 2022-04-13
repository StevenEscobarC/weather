package com.stevenescobar.weather.services;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stevenescobar.weather.services.model.Root;

import java.net.URL;

import cafsoft.foundation.HTTPURLResponse;
import cafsoft.foundation.URLComponents;
import cafsoft.foundation.URLQueryItem;
import cafsoft.foundation.URLSession;

public class WeatherService {

    public interface OnResponse {
        public abstract void run(int statusCode, Root root);
    }

    private String theAPIKey = "";

    public WeatherService(String newAPIKey) {
        theAPIKey = newAPIKey;
    }

    public void requestWeatherData(String cityName, String countryISOCode, OnDataResponse delegate) {
        URL url = null;
        URLComponents components = new URLComponents();

        components.setScheme("https");
        components.setHost("api.openweathermap.org");
        components.setPath("/data/2.5/weather");
        components.setQueryItems(new URLQueryItem[]{
                new URLQueryItem("appid", theAPIKey),
                new URLQueryItem("units", "metric"),
                new URLQueryItem("Lang", "es"),
                new URLQueryItem("q", cityName + "," + countryISOCode)
    });

        url = components.getURL();
        Log.d("Url: ",url.toString());
        URLSession.getShared().dataTask(url, (data, response, error) -> {
            HTTPURLResponse resp = (HTTPURLResponse) response;
            Root root = null;
            int statusCode = -1;

            if (error == null && resp.getStatusCode() == 200) {
                String text = data.toText();
                Log.d("salida", text);
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
                Gson gson = gsonBuilder.create();
                root = gson.fromJson(text, Root.class);
                statusCode = resp.getStatusCode();
            }

            if (delegate != null) {
                delegate.OnChange(error != null, statusCode, root);

            }
        }).resume();
    }

    public interface OnDataResponse {
        public abstract void OnChange(boolean isNetworkError, int statusCode,Root root);
    }


}
