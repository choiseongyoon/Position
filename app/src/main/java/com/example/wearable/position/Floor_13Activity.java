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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.distance.AndroidModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import static com.example.wearable.position.BeaconAdapter.decimalFormat;
import static java.lang.Math.pow;

public class Floor_13Activity extends AppCompatActivity implements BeaconConsumer {
    public static final String BEACON_PARSER = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    double coff1 = 0;
    double coff2 = 0;
    String wifi_13_floor = "88:36:6c:0c:37:4e"; //wearable
    public ImageView iv, wifi;
    private WifiManager mainWifi;
    private WifiListReceiver receiverWifi;
    private final Handler handler = new Handler();
    public List<ScanResult> results = new ArrayList<>();
    private WifiAdapter wifiAdapter = new WifiAdapter();
    private boolean wifiWasEnabled;
    double wifi_distance;

    int wifi_num = 0;
    float wifi_point[] = {1500, 800};
    float iv_point[] = {1100, 800};
    float x_ratio = (float) 0.7;
    float y_ratio = (float) 0.4;


    public float d1;

    String candy1 = "DC:14:7B:CF:B4:B1";
    // String candy2 = "F3:E2:3C:5F:77:14";
    String candy3 = "E9:6F:9C:B7:B0:C7";

    int r_candy1[] = {1500, 800};
    //int r_candy2[]= {1500,1000};
    int r_candy3[] = {1300, 2100};


    public int period = 0;

    //////////////////블루투스
    BluetoothAdapter mBluetoothAdapter;

    BeaconAdapter beaconAdapter;

    BeaconManager mBeaconManager;

    Vector<BeaconItem> items_b;

    int p[] = {0, 0, 0, 0, 0, 0};
    String name[] = {" ", " ", " "};

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
    long Time = System.currentTimeMillis();
    String day_s;
    String time_s;
    SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timetime = new SimpleDateFormat("HH:mm:ss");

    double x_distance = 0;
    double y_distance = 0;

    AndroidModel am = AndroidModel.forThisDevice();
    String user = am.getModel();
    int wifi_rssi, wifi_fre;


    TextView tv_distance, tv_beacon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_13);
        Intent intent = getIntent();
        coff1 = Double.parseDouble(intent.getStringExtra("coff1"));
        coff2 = Double.parseDouble(intent.getStringExtra("coff2"));
        period = Integer.parseInt((intent.getStringExtra("period")));
        ;

        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiListReceiver();

        wifiWasEnabled = mainWifi.isWifiEnabled();
        if (!mainWifi.isWifiEnabled()) {
            mainWifi.setWifiEnabled(true);
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, 100);
        } else {
            mBeaconManager = BeaconManager.getInstanceForApplication(this);
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BEACON_PARSER));
        }
        try {
            mBeaconManager.setForegroundScanPeriod(1100l);
            mBeaconManager.setForegroundBetweenScanPeriod(0l);
            mBeaconManager.updateScanPeriods();
        } catch (RemoteException e) {
        }
        mBeaconManager.bind(Floor_13Activity.this);


        iv = findViewById(R.id.position);
        wifi = findViewById(R.id.wifi);

        wifi.setX((float) wifi_point[0] * x_ratio);
        wifi.setY((float) wifi_point[1] * y_ratio);

        iv.setX((float) iv_point[0] * x_ratio);
        iv.setY((float) iv_point[1] * y_ratio);
        iv.setVisibility(View.VISIBLE);

        tv_distance = findViewById(R.id.textView);
        tv_beacon = findViewById(R.id.textView_B);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            mBeaconManager = BeaconManager.getInstanceForApplication(this);
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BEACON_PARSER));
        }

    }

    public void addDevice(Vector<BeaconItem> items_b) {
        Collections.sort(items_b, new Comparator<BeaconItem>() {
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

    List<Double> distance_list = new ArrayList<Double>();
    List<Double> beacon_list = new ArrayList<Double>();

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                final Iterator<Beacon> iterator = beacons.iterator();
                items_b = new Vector<>();
                while (iterator.hasNext()) {
                    Beacon beacon = iterator.next();
                    String address = beacon.getBluetoothAddress();
                    int rssi = beacon.getRssi();
                    double distance = beacon.getDistance();
                    items_b.add(new BeaconItem(address, rssi, distance));
                    addDevice(items_b);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (items_b.size() >= 1 && items_b.get(0).getDistance() < 1.5) {
                            match(items_b);
                            beacon_list.add(items_b.get(0).getDistance());
                            if ((beacon_list.size() >= period) && (beacon_list.size() % period == 0)) {
                                x_distance = (p[0] + (100 * items_b.get(0).getDistance())) * x_ratio;
                                y_distance = (p[1]) * y_ratio;
                                tv_beacon.setText(decimalFormat.format(items_b.get(0).getDistance()) + name[0] + " ");
                                tv_distance.setText(" ");
                            }
                        } else {
                            distance_list.add(wifi_distance);
                            if ((distance_list.size() >= period) && (distance_list.size() % period == 0)) {
                                double d = Double.parseDouble(decimalFormat.format(getAverage(distance_list, period)));
                                x_distance = iv_point[0] * x_ratio;
                                y_distance = (iv_point[1] + d * 100) * y_ratio;
                                tv_beacon.setText("Outside");
                                tv_distance.setText(decimalFormat.format(d) + "m");
                            }
                        }

                        beaconAdapter = new BeaconAdapter(items_b, Floor_13Activity.this);
                        beaconAdapter.notifyDataSetChanged();
                    }
                });


                iv.setX((float) x_distance);
                iv.setY((float) y_distance);
                iv.setVisibility(View.VISIBLE);

                Time = System.currentTimeMillis();
                day_s = dayTime.format(new Date(Time));
                time_s = timetime.format(new Date(Time));

                if (x_distance != 0 && y_distance != 0 && x_distance > 0 && y_distance > 0) {
                    databaseReference.child(user).child(day_s).child(time_s).child("x").setValue(x_distance);
                    databaseReference.child(user).child(day_s).child(time_s).child("y").setValue(y_distance);
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

    public double getAverage(List<Double> list, int period) {
        double sum = 0;
        double average = 0;
        int n = list.size();
        for (int i = 1; i < period + 1; i++) {
            sum += list.get(n - i);
        }
        average = sum / (period * 1.0);
        return average;
    }

    class WifiListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            results = mainWifi.getScanResults();
            wifiAdapter.setResults(results);
            wifiAdapter.notifyDataSetChanged();
            refresh();
        }
    }

    public void refresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainWifi.startScan();

                int N = mainWifi.getScanResults().size();
                for (int i = 0; i < N; i++) {
                    if (mainWifi.getScanResults().get(i).BSSID.equals(wifi_13_floor)) wifi_num = i;
                }

                wifi_rssi = mainWifi.getScanResults().get(wifi_num).level;
                wifi_fre = mainWifi.getScanResults().get(wifi_num).frequency;
                wifi_distance = getDistance(wifi_fre, wifi_rssi);

            }
        }, 1);
    }


    public float getDistance(int frequency, int level) {
        double exp = (coff1 - (coff2 * Math.log10(frequency)) + Math.abs(level)) / coff2;
        float distance = (float) pow(10.0, exp);
        return distance;
    }

    @Override
    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        refresh();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (!wifiWasEnabled) {
            mainWifi.setWifiEnabled(false);
        }
    }


    public int match(Vector<BeaconItem> items) {
        for (int j = 0; j < 1; j++) {
            if (items.get(j).getAddress().equals(candy1)) {
                p[2 * j] = r_candy1[0];
                p[2 * j + 1] = r_candy1[1];
                name[j] = "candy1";
            }
// else if (items.get(j).getAddress().equals(candy2)) {
//                p[2 * j] = r_candy2[0];
//                p[2 * j + 1] = r_candy2[1];
//                name[j] = "candy2";
//            }
            else if (items.get(j).getAddress().equals(candy3)) {
                p[2 * j] = r_candy3[0];
                p[2 * j + 1] = r_candy3[1];
                name[j] = "candy3";
            } else {
                return -1;
            }
        }
        return 0;
    }


}
