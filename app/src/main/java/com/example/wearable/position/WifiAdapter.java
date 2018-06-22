package com.example.wearable.position;


import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.wearable.position.BeaconAdapter.decimalFormat;
import static java.lang.Math.pow;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {
    private List<ScanResult> results = new ArrayList<>();
    double coff1 = 27.55;
    double coff2 = 20.0;

    @Override
    public WifiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wifi, parent, false);
        WifiAdapter.ViewHolder vh = new WifiAdapter.ViewHolder(linearLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(results.get(position).SSID + "");
        holder.level.setText(results.get(position).level + "dB");
        holder.distance.setText(getDistance(results.get(position).frequency, results.get(position).level) + "m");

    }

    public String getDistance(int frequency, int level) {
        double exp = (coff1 - (coff2 * Math.log10(frequency)) + Math.abs(level)) / coff2;
        float distance = (float) pow(10.0, exp);
        return decimalFormat.format(distance);

    }


    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name, level, distance;

        public ViewHolder(LinearLayout v) {
            super(v);
            name = v.findViewById(R.id.wifi_name);
            level = v.findViewById(R.id.wifi_level);
            distance = v.findViewById(R.id.wifi_distance);
        }
    }

    public List<ScanResult> getResults() {
        return results;
    }

    public void setResults(List<ScanResult> results) {
        this.results = results;
    }
}
