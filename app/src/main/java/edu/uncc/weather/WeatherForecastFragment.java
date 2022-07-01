package edu.uncc.weather;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
/*
Thomas Cowie
Group 12
WeatherForecastFragment.java
InClass8
 */
public class WeatherForecastFragment extends Fragment {

    final String TAG = "WeatherForecastFrag";
    private final OkHttpClient client = new OkHttpClient();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private String mLat;
    private String mLon;
    private String mCity;
    private String mDesc;

    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    public static WeatherForecastFragment newInstance(String lat, String lon, String city, String desc) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, lat);
        args.putString(ARG_PARAM2, lon);
        args.putString(ARG_PARAM3, city);
        args.putString(ARG_PARAM4, desc);
        fragment.setArguments(args);
        return fragment;
    }
    public String kelvinConvert(String num) {
        double doubleNum = Double.valueOf(num);
        double farNum = doubleNum - 273.15 * (9/5) + 32;
        return String.valueOf(farNum);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLat = getArguments().getString(ARG_PARAM1);
            mLon = getArguments().getString(ARG_PARAM2);
            mCity = getArguments().getString(ARG_PARAM3);
            mDesc = getArguments().getString(ARG_PARAM4);
        }
    }

    ListView listview;
    ForecastAdapter adapter;
    TextView forecastCity;

    String date;
    String currTemp;
    String tempMax;
    String tempMin;
    String desc;
    String humidity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_weather_forecast, container, false);

        forecastCity = view.findViewById(R.id.textViewForecastCirty);
        forecastCity.setText(mCity);


        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org").newBuilder() //Example of how to create a url
                .addPathSegment("data")
                .addPathSegment("2.5")
                .addPathSegment("forecast")
                .addQueryParameter("lat", mLat)
                .addQueryParameter("lon", mLon)
                .addQueryParameter("appid", "a586767be6d7b3206345b25583e38a20")
                .build();
        Log.d(TAG, "onCreateView: " + url);

        Request request = new Request.Builder()
                .url(url) //You'd put url here
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    ArrayList<ForecastData> forecastDataArrList = new ArrayList<>();
                    try {
                        DecimalFormat df = new DecimalFormat("0.00##");
                        JSONObject mainObj = new JSONObject(response.body().string());
                        Log.d(TAG, "onResponse: " + mainObj.toString());
                        JSONArray list = mainObj.getJSONArray("list");
                        Log.d(TAG, "onResponse: " + list.toString());
                        JSONObject main;

                        for(int i = 0; i < 5; i++) {
                            main = list.getJSONObject(i).getJSONObject("main");
                            Log.d(TAG, "onResponse: " + main);
                            date = "Wouldnt work bc object isnt named so wont let me get it";
                            Log.d(TAG, "onResponse: " + date);
                            currTemp = main.getString("temp");
                            currTemp = kelvinConvert(currTemp);
                            currTemp = df.format(Double.valueOf(currTemp));
                            tempMax = main.getString("temp_max");
                            tempMax = kelvinConvert(tempMax);
                            tempMax = df.format(Double.valueOf(tempMax));
                            tempMin = main.getString("temp_min");
                            tempMin = kelvinConvert(tempMin);
                            tempMin = df.format(Double.valueOf(tempMin));
                            humidity = main.getString("humidity");
                            desc = mDesc;
                            Log.d(TAG, "onResponse: " + date + currTemp + tempMin + tempMax + humidity + desc);
                            forecastDataArrList.add(new ForecastData(date, currTemp, tempMax, tempMin, humidity, desc));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    getActivity().runOnUiThread(new Runnable() {  //This is how you change ui with stuff done on the thread.
                        @Override
                        public void run() {
                            listview = view.findViewById(R.id.listView);
                            adapter = new ForecastAdapter(getContext(), R.layout.forecast_layout, forecastDataArrList);
                            listview.setAdapter(adapter);
                        }
                    });

                }
            }
        });

        return view;
    }
}