package com.example.wearable.position;


import android.net.wifi.ScanResult;

import static java.lang.Math.pow;

public class WifiItem implements Comparable<WifiItem> {
    private String bssid;
    private String ssid;
    private String capabilities;
    private int frequency;
    private int level;
    private long timestamp;
    private double distance;

    public WifiItem(ScanResult result) {
        bssid = result.BSSID;
        ssid = result.SSID;
        capabilities = result.capabilities;
        frequency = result.frequency;
        level = result.level;
        timestamp = System.currentTimeMillis();
    }


    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(WifiItem another) {
        return another.level - this.level;
    }

}
