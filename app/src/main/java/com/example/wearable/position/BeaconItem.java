package com.example.wearable.position;

public class BeaconItem {

    private String address;
    private int rssi;
    private int txPower;
    private double distance;
    private String name;

    public BeaconItem(String address, int rssi, double distance) {
        this.address = address;
        this.rssi = rssi;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getRssi() {
        return rssi;
    }

    public int getTxPower() {
        return txPower;
    }

    public double getDistance() {
        return distance;
    }

    public void setName(String name) {
        this.name = name;
    }

}

