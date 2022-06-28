package edu.uncc.weather;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.uncc.weather.databinding.FragmentCurrentWeatherBinding;
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
CurrentWeatherFragment.java
InClass8
 */
public class CurrentWeatherFragment extends Fragment {
    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    private DataService.City mCity;
    private final String TAG = "currWeatherFragment";
    private final OkHttpClient client = new OkHttpClient();
    FragmentCurrentWeatherBinding binding;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    public static CurrentWeatherFragment newInstance(DataService.City city) {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = (DataService.City) getArguments().getSerializable(ARG_PARAM_CITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    public String kelvinConvert(String num) {
        double doubleNum = Double.valueOf(num);
        double farNum = doubleNum - 273.15 * (9/5) + 32;
        return String.valueOf(farNum);
    }

    String lat;
    String lon;
    String temp;
    String tempMax;
    String tempMin;
    String desc;
    String humidity;
    String windSpeed;
    String windDeg;
    String cloudiness;

    TextView cityOut;
    TextView tempOut;
    TextView tempMaxOut;
    TextView tempMinOut;
    TextView descOut;
    TextView humidityOut;
    TextView windSpeedOut;
    TextView windDegOut;
    TextView cloudOut;
    Button checkForecastBtn;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Current Weather");
        checkForecastBtn = view.findViewById(R.id.buttonCheckForecast);
        cityOut = view.findViewById(R.id.textViewCity);
        tempOut = view.findViewById(R.id.textViewTempOutput);
        tempMaxOut = view.findViewById(R.id.textViewTempMaxOutput);
        tempMinOut = view.findViewById(R.id.textViewTempMinOutput);
        descOut = view.findViewById(R.id.textViewDescriptionOutput);
        humidityOut = view.findViewById(R.id.textViewHumidityOutput);
        windSpeedOut = view.findViewById(R.id.textViewWindSpeedOutput);
        windDegOut = view.findViewById(R.id.textViewWindDegreeOutput);
        cloudOut = view.findViewById(R.id.textViewCloudinessOutput);

        String locationToQuery = mCity.getCity() + "," + mCity.getCountry();

        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org").newBuilder() //Example of how to create a url
                .addPathSegment("geo")
                .addPathSegment("1.0")
                .addPathSegment("direct")
                .addQueryParameter("q", locationToQuery)
                .addQueryParameter("appid", "a586767be6d7b3206345b25583e38a20")
                .build();
        Log.d(TAG, "onCreateView: " + url);
        Request request = new Request.Builder()
                .url(url) //You'd put url here
                .build();

        //  ****GET LAT AND LONG****
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    ArrayList<String> locationArr = new ArrayList<>();
                    String data;
                    JSONArray jsonArr = new JSONArray(response.body().string());

                    JSONObject json = jsonArr.getJSONObject(0);
                    lat = json.getString("lat");
                    lon = json.getString("lon");
                    Log.d(TAG, "onResponse: " + lat + lon);

                    //***GET WEATHER***
                    HttpUrl url2 = HttpUrl.parse("https://api.openweathermap.org").newBuilder() //Example of how to create a url
                            .addPathSegment("data")
                            .addPathSegment("2.5")
                            .addPathSegment("weather")
                            .addQueryParameter("lat", lat)
                            .addQueryParameter("lon", lon)
                            .addQueryParameter("appid", "a586767be6d7b3206345b25583e38a20")
                            .build();
                    Log.d(TAG, "onCreateView: " + url2);

                    Request request = new Request.Builder()
                            .url(url2) //You'd put url here
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override public void onResponse(Call call, Response response) throws IOException {
                            try (ResponseBody responseBody = response.body()) {
                                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                                try {
                                    JSONObject mainObj = new JSONObject(response.body().string());
                                    JSONArray weather = mainObj.getJSONArray("weather");
                                    JSONObject weatherObj = weather.getJSONObject(0);
                                    JSONObject main = mainObj.getJSONObject("main");
                                    JSONObject wind = mainObj.getJSONObject("wind");
                                    JSONObject clouds = mainObj.getJSONObject("clouds");

                                    temp = main.getString("temp");
                                    temp = kelvinConvert(temp);
                                    tempMax = main.getString("temp_max");
                                    tempMax = kelvinConvert(tempMax);
                                    tempMin = main.getString("temp_min");
                                    tempMin = kelvinConvert(tempMin);

                                    desc = weatherObj.getString("description");
                                    humidity = main.getString("humidity");
                                    windSpeed = wind.getString("speed");
                                    windDeg = wind.getString("deg");
                                    cloudiness = clouds.getString("all");


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                getActivity().runOnUiThread(new Runnable() {  //This is how you change ui with stuff done on the thread.
                                    @Override
                                    public void run() {
                                        cityOut.setText(mCity.getCity());
                                        DecimalFormat df = new DecimalFormat("0.00##");
                                        temp = df.format(Double.valueOf(temp));
                                        tempOut.setText(temp + " F");
                                        tempMax = df.format(Double.valueOf(tempMax));
                                        tempMaxOut.setText(tempMax + " F");
                                        tempMin = df.format(Double.valueOf(tempMin));
                                        tempMinOut.setText(tempMin + " F");
                                        descOut.setText(desc);
                                        humidityOut.setText(humidity + "%");
                                        windSpeedOut.setText(windSpeed + " mph");
                                        windDegOut.setText(windDeg + " degrees");
                                        cloudOut.setText(cloudiness + "%");

                                        checkForecastBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                mCurrentWeatherFragment.checkForecastTap(lat, lon, mCity.getCity(), desc);
                                            }
                                        });
                                    }
                                });

                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {        //Ensure that the class is implemented.
        super.onAttach(context);
        if(context instanceof ICurrentWeatherFragment) {
            mCurrentWeatherFragment = (ICurrentWeatherFragment) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement ICurrentWeatherFragment");
        }
    }

    ICurrentWeatherFragment mCurrentWeatherFragment;
    public interface ICurrentWeatherFragment {
        void checkForecastTap(String lat, String lon, String city, String desc);
    }
}