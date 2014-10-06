package io.replay.framework.model;

import android.content.Context;
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

import org.json.JSONObject;

import io.replay.framework.ReplayIO;
import io.replay.framework.model.ReplayRequest.RequestType;
import io.replay.framework.util.ReplayPrefs;

public class ReplayRequestFactory {

    private static final String KEY_EVENT_NAME = "event_name";
    private static final String NETWORK_KEY = "network";
    private static final String PROPERTIES_KEY = "properties";
    private static ReplayRequestFactory instance;
    private static Context mContext;
    private static ReplayPrefs mPrefs;

    /** ReplayIO API key */
    public static final String KEY_REPLAY_KEY = "replay_key";
    private static final String MODEL_KEY="device_model";
    private static final String MANUFACTURER_KEY="device_manufacturer";
    private static final String OS_KEY="client_os";
    private static final String SDK_KEY="client_sdk";
    private static final String DISPLAY_KEY="display";
    private static final String TIME_KEY="timestamp";
    private static final String MOBILE_KEY="mobile";
    private static final String WIFI_KEY="wifi";
    private static final String BLUETOOTH_KEY="bluetooth";
    private static final String CARRIER_KEY="carrier";

    private final static ReplayJsonObject base = new ReplayJsonObject();

    public static ReplayRequestFactory init(Context context) {
        return instance == null ? instance = new ReplayRequestFactory(context.getApplicationContext()) : instance;
    }

    public ReplayRequestFactory(Context context) {
        mPrefs = ReplayPrefs.get(context);
        mContext = context;
    }

    /**Merges event metadata
     *
     * @param request
     */
    public static void mergePassiveData(ReplayRequest request){
        ReplayJsonObject toReturn = new ReplayJsonObject( request.getJsonBody() ); //copy constructor

        toReturn.put(ReplayPrefs.KEY_DISTINCT_ID, "meow");
        toReturn.put(KEY_REPLAY_KEY, ReplayIO.getConfig().getApiKey());
        toReturn.put(ReplayPrefs.KEY_CLIENT_ID, mPrefs.getClientID());

        ReplayJsonObject tmp = new ReplayJsonObject();
        toReturn.put("browser_info",tmp );

        updateTimestamp(toReturn, request.getCreatedAt());
        request.setJsonBody(toReturn);
    }

    private static void updateTimestamp(ReplayJsonObject json, long createdAt) {
        JSONObject p = json.getJsonObject(PROPERTIES_KEY);
        if(p != null){
            ReplayJsonObject props = (ReplayJsonObject) p;
            long delta = (System.nanoTime() - createdAt) / 1000000L ;
            props.put(TIME_KEY, delta);
            json.put(PROPERTIES_KEY, props); //it's clobberin' time!
        }
    }

    /**
     * Build the ReplayRequest object for an event request.
     *
     * @param event The event name.
     * @param data  The name-value paired data.
     * @return ReplayRequest object.
     */
    public static ReplayRequest requestForEvent(String event, Object[] data)  {
        ReplayJsonObject props = collectPassiveData();
        ReplayJsonObject extras = new ReplayJsonObject(data);
        props.mergeJSON(extras);

        ReplayJsonObject json = new ReplayJsonObject();
        json.put(PROPERTIES_KEY,props);
        json.put(KEY_EVENT_NAME, event);

        return new ReplayRequest(RequestType.EVENTS, json);
    }

    /**
     * Build the ReplayRequest object for an traits request.
     *
     * @return ReplayRequest object.
     */
    public static ReplayRequest requestForTraits(Object[] data) {
        ReplayJsonObject extras = new ReplayJsonObject(data);
        ReplayJsonObject json = new ReplayJsonObject();
        json.put(PROPERTIES_KEY,extras);

        return new ReplayRequest(RequestType.TRAITS, json);
    }

    /**
     * Send additional properties that are automatically tracked.
     *
     */
    private static ReplayJsonObject collectPassiveData(){
        ReplayJsonObject props = new ReplayJsonObject();
        try {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            Criteria crit = new Criteria();
            crit.setPowerRequirement(Criteria.POWER_LOW);
            crit.setAccuracy(Criteria.ACCURACY_FINE); //used to be Criteria.ACCURACY_COARSE
            String provider = locationManager.getBestProvider(crit, true);

            if (provider != null) {
                try {
                    Location location = locationManager.getLastKnownLocation(provider); // this is a cached location
                    props.put("latitude", Double.toString(location.getLatitude()));
                    props.put("longitude", Double.toString(location.getLongitude()));
                } catch (SecurityException ex) {
                    //The application may not have permission to access location
                }
            }

            props.put(OS_KEY, VERSION.RELEASE);

            props.put(SDK_KEY, Integer.toString(VERSION.SDK_INT));

            WindowManager window = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();

            int width;
            int height;
            if(VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR2){
                Point size = new Point();
                display.getSize(size);
                width = size.x;
                height = size.y;
            }else{
                height = display.getHeight();
                width = display.getWidth();
            }

            props.put(DISPLAY_KEY,width+"x"+height);

            props.put(MANUFACTURER_KEY, Build.MANUFACTURER);

            props.put(MODEL_KEY, Build.MODEL);


            //network
            ReplayJsonObject network = new ReplayJsonObject();
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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

            final TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
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