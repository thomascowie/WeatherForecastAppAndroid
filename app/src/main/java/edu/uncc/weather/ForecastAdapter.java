package edu.uncc.weather;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
/*
Thomas Cowie
Group 12
ForecastAdapter.java
InClass8
 */
public class ForecastAdapter extends ArrayAdapter<ForecastData> {
    final String TAG = "Adapter";
    public ForecastAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ForecastData> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.forecast_layout, parent, false);
            ForecastAdapter.ViewHolder viewHolder = new ViewHolder();
            viewHolder.textViewDateOutput = convertView.findViewById(R.id.textViewDateOutput);
            viewHolder.textViewCurrTempOutput = convertView.findViewById(R.id.textViewCurrTempOutput);
            viewHolder.textViewMaxTempOutput = convertView.findViewById(R.id.textViewMaxTempOutput);
            viewHolder.textViewMinTempOutput = convertView.findViewById(R.id.textViewMinTempOutput);
            viewHolder.textViewHumidityForecast = convertView.findViewById(R.id.textViewHumidityForecast);
            viewHolder.textViewCloudCover = convertView.findViewById(R.id.textViewCloudCover);
            viewHolder.imageViewForecast = convertView.findViewById(R.id.imageViewForecast);
            convertView.setTag(viewHolder);
        }

        //Get the values for texts
        ForecastAdapter.ViewHolder viewHolder = (ForecastAdapter.ViewHolder)convertView.getTag();
        String date = getItem(position).date;
        String currTemp = getItem(position).currTemp;
        String maxTemp = getItem(position).maxTemp;
        String minTemp = getItem(position).minTemp;
        String humidity  =getItem(position).humidity;
        String desc = getItem(position).desc;
        Log.d(TAG, "getView: " + date + currTemp + maxTemp + minTemp + humidity + desc);

        viewHolder.textViewDateOutput.setText(date);
        viewHolder.textViewCurrTempOutput.setText(currTemp);
        viewHolder.textViewMaxTempOutput.setText(maxTemp);
        viewHolder.textViewMinTempOutput.setText(minTemp);
        viewHolder.textViewHumidityForecast.setText(humidity);
        viewHolder.textViewCloudCover.setText(desc);
        return convertView;
    }

    private static class ViewHolder {
        TextView textViewDateOutput;
        TextView textViewCurrTempOutput;
        TextView textViewMaxTempOutput;
        TextView textViewMinTempOutput;
        TextView textViewHumidityForecast;
        TextView textViewCloudCover;
        ImageView imageViewForecast;
    }


}
