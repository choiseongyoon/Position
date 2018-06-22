package com.example.wearable.position;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String BEACON_PARSER = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";

    public String coff1;
    public String coff2;
    public String period;

    ///////////////////와이파이

    Handler handler = new Handler();

    public List<ScanResult> results = new ArrayList<>();

    WifiListReceiver mWifiListReceiver;

    WifiAdapter wifiAdapter = new WifiAdapter();

    WifiManager mWifiManager;

    boolean wifiWasEnabled;

    String wifi_15_floor = "8a:36:6c:ce:4b:34"; //testlab
    String wifi_13_floor = "88:36:6c:0c:37:4e"; //wearable


    //////////////////블루투스
    BluetoothAdapter mBluetoothAdapter;

    BeaconAdapter beaconAdapter;

    BeaconManager mBeaconManager;

    Vector<BeaconItem> items;

    RecyclerView beaconListView, wifiListView;

    RecyclerView.LayoutManager layoutManager;

    LinearLayoutManager manager_beacon, manager_wifi;

    Button go_move_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        coff1 = intent.getStringExtra("coff1");
        coff2 = intent.getStringExtra("coff2");
        period = intent.getStringExtra("period");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, 100);
        } else {
            mBeaconManager = BeaconManager.getInstanceForApplication(this);
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BEACON_PARSER));
        }

        try {
            mBeaconManager.setForegroundScanPeriod(2200l);
            mBeaconManager.setForegroundBetweenScanPeriod(0l);
            mBeaconManager.updateScanPeriods();
        } catch (RemoteException e) {
        }

        mBeaconManager.bind(MainActivity.this);
        beaconListView = findViewById(R.id.beaconListView);
        wifiListView = findViewById(R.id.wifiListView);

        layoutManager = new LinearLayoutManager(this);

        manager_beacon = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        beaconListView.setLayoutManager(manager_beacon);

        beaconListView.setItemAnimator(new DefaultItemAnimator());
        beaconListView.setAdapter(beaconAdapter);

//와이파이

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiListReceiver = new WifiListReceiver();

        wifiWasEnabled = mWifiManager.isWifiEnabled();
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

        wifiListView = findViewById(R.id.wifiListView);
        manager_wifi = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        wifiListView.setLayoutManager(manager_wifi);
        wifiListView.setItemAnimator(new DefaultItemAnimator());
        wifiListView.setAdapter(wifiAdapter);

        go_move_activity = findViewById(R.id.go_move_activity);
        go_move_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                int N = mWifiManager.getScanResults().size();
                for (int i = 0; i < N; i++) {
                    if (mWifiManager.getScanResults().get(i).BSSID.equals(wifi_15_floor)) {
                        intent = new Intent(MainActivity.this, Floor_15Activity.class);

                    } else if (mWifiManager.getScanResults().get(i).BSSID.equals(wifi_13_floor)) {
                        intent = new Intent(MainActivity.this, Floor_13Activity.class);

                    }
                }
                intent.putExtra("coff1", coff1);
                intent.putExtra("coff2", coff2);
                intent.putExtra("period", period);

                startActivity(intent);
            }
        });

    }

    public void refresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWifiManager.startScan();
            }
        }, 1);
    }

    @Override
    protected void onResume() {
        registerReceiver(mWifiListReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        refresh();
        super.onResume();
    }

    class WifiListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = mWifiManager.getScanResults();
            Collections.sort(results, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult scanResult, ScanResult scanResult2) {
                    if (scanResult.level > scanResult2.level) {
                        return -1;
                    } else if (scanResult.level < scanResult2.level) {
                        return 1;
                    }
                    return 0;
                }
            });
            wifiAdapter.setResults(results);
            wifiAdapter.notifyDataSetChanged();
            refresh();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
        if (!wifiWasEnabled) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            mBeaconManager = BeaconManager.getInstanceForApplication(this);
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BEACON_PARSER));
        }

    }

    public void addDevice(Vector<BeaconItem> items) {
        Collections.sort(items, new Comparator<BeaconItem>() {
            @Override
            public int compare(BeaconItem it1, BeaconItem it2) {
                if (it1.getDistance() > it2.getDistance()) {
                    return 1;
                } else if (it2.getDistance() > it1.getDistance()) {
                    return -1;
                }
                return 0;
            }
        });


    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Iterator<Beacon> iterator = beacons.iterator();
                    items = new Vector<>();
                    while (iterator.hasNext()) {
                        Beacon beacon = iterator.next();
                        String address = beacon.getBluetoothAddress();
                        int rssi = beacon.getRssi();
                        double distance = beacon.getDistance();
                        items.add(new BeaconItem(address, rssi, distance));
                        addDevice(items);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            beaconAdapter = new BeaconAdapter(items, MainActivity.this);
                            beaconListView.setAdapter(beaconAdapter);
                            beaconAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.addMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region) {
            }

            @Override
            public void didExitRegion(Region region) {
            }


            @Override
            public void didDetermineStateForRegion(int state, Region region) {
            }
        });
        try {
            mBeaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}