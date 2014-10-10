package io.replay.framework;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import java.util.Map;

import io.replay.framework.ReplayRequest.RequestType;

/**
 * Created by parthpadgaonkar on 8/28/14.
 */
class QueueLayer extends LooperThreadWithHandler {
    private ReplayQueue queue;
    private Context mContext;
    private final ReplayJsonObject deviceInfo;


    public QueueLayer(ReplayQueue queue, Context context) {
        this.queue = queue;
        mContext = context;
        deviceInfo = InfoManager.buildInfo(context, ReplayPrefs.get(context));
    }

    @Override
    public synchronized void start() {
        super.start();
        queue.start();
    }

    void enqueueEvent(final String event, final Object[] data) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                ReplayRequest request = ReplayRequestFactory.createRequest(mContext, RequestType.EVENTS, event, deviceInfo, data);
                queue.enqueue(request);
            }
        });
    }

    void enqueueEvent(final String event, final Map<String,?> data) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                ReplayRequest request = ReplayRequestFactory.createRequest(mContext, RequestType.EVENTS, event, deviceInfo, data);
                queue.enqueue(request);
            }
        });
    }

    void enqueueTrait(final Map<String,?> data) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                ReplayRequest request = ReplayRequestFactory.createRequest(mContext, RequestType.TRAITS, null, deviceInfo, data);
                queue.enqueue(request);
            }
        });
    }

    void enqueueTrait(final Object[] data) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                ReplayRequest request = ReplayRequestFactory.createRequest(mContext, RequestType.TRAITS, null, deviceInfo, data);
                queue.enqueue(request);
            }
        });
    }

    void sendFlush() {
        handler().post(new Runnable() {
            @Override
            public void run() {
                queue.flush();
            }
        });
    }

    void enqueueJob(final ReplayJob job) {
        if (BuildConfig.BUILD_TYPE.equals("release")) {
            throw new IllegalStateException("This method is used solely for testing - please use QueueLayer#enqueueEvent");
        } else {
            handler().post(new Runnable() {
                @Override
                public void run() {
                    queue.enqueue(job);
                }
            });
        }
    }

    static class InfoManager {
        static final String MODEL_KEY="device_model";
        static final String MANUFACTURER_KEY="device_manufacturer";
        static final String OS_KEY="client_os";
        static final String SDK_KEY="client_sdk";
        static final String DISPLAY_KEY="display";
        static final String MOBILE_KEY="mobile";
        static final String WIFI_KEY="wifi";
        static final String BLUETOOTH_KEY="bluetooth";
        static final String CARRIER_KEY="carrier";
        static final String NETWORK_KEY = "network";
        static final String LOCATION_LAT = "latitude";
        static final String LOCATION_LONG = "longitude";

        static ReplayJsonObject buildInfo(Context context, ReplayPrefs prefs){
            ReplayJsonObject props = new ReplayJsonObject();
            try {
                //location
                if(context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED){
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    Criteria crit = new Criteria();
                    crit.setPowerRequirement(Criteria.POWER_LOW);
                    crit.setAccuracy(Criteria.ACCURACY_COARSE); //used to be Criteria.ACCURACY_COARSE
                    String provider = locationManager.getBestProvider(crit, true);

                    if (provider != null) {
                        try {
                            Location location = locationManager.getLastKnownLocation(provider); // this is a cached location
                            props.put(LOCATION_LAT, Double.toString(location.getLatitude()));
                            props.put(LOCATION_LONG, Double.toString(location.getLongitude()));
                        } catch (SecurityException ex) {
                            //The application may not have permission to access location
                        }
                    }
                }


                //OS info
                props.put(OS_KEY, VERSION.RELEASE);
                props.put(SDK_KEY, Integer.toString(VERSION.SDK_INT));

                //display + make/model info
                WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = window.getDefaultDisplay();
                final int width;
                final int height;
                if(VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR2){
                    Point size = new Point();
                    display.getSize(size);
                    width = size.x;
                    height = size.y;
                }else{
                    height = display.getHeight();
                    width = display.getWidth();
                    props.put(DISPLAY_KEY,width+"x"+height);
                }

                props.put(DISPLAY_KEY,width+"x"+height);
                props.put(MANUFACTURER_KEY, Build.MANUFACTURER);
                props.put(MODEL_KEY, Build.MODEL);

                //network
                ReplayJsonObject network = new ReplayJsonObject();
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (networkInfo != null){
                    network.put(WIFI_KEY,String.valueOf(networkInfo.isConnected()));
                }

                networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH); //might fail (silently?) < 13;
                if (networkInfo != null){
                    network.put(BLUETOOTH_KEY,String.valueOf(networkInfo.isConnected()));
                }

                networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (networkInfo != null){
                    network.put(MOBILE_KEY,String.valueOf(networkInfo.isConnected()));
                }

                final TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                String carrier = telephonyManager.getNetworkOperatorName();
                network.put(CARRIER_KEY,carrier);

                props.put(NETWORK_KEY, network);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return props;
        }

    }
}

