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
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONObject;

import io.replay.framework.ReplayConfig;
import io.replay.framework.ReplayConfig.RequestType;
import io.replay.framework.ReplayIO;
import io.replay.framework.util.ReplayPrefs;

public class ReplayRequestFactory {

    private static final String KEY_EVENT_NAME = "event_name";
    private static final String NETWORK_KEY = "network";
    private static final String PROPERTIES_KEY = "properties";
    private static ReplayRequestFactory instance;
    private static Context mContext;
    private static ReplayPrefs mPrefs;

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
        ReplayJsonObject properties = new ReplayJsonObject();
        collectPassiveData(properties);
        base.put(PROPERTIES_KEY, properties);
    }

    /**Merges event metadata
     *
     * @param request
     */
    public static void mergePassiveData(ReplayRequest request){
        ReplayJsonObject toReturn = new ReplayJsonObject( request.getJsonBody() ); //copy constructor

        base.put(ReplayPrefs.KEY_DISTINCT_ID, mPrefs.getDistinctID());
        base.put(ReplayConfig.KEY_REPLAY_KEY, ReplayIO.getConfig().getApiKey());
        base.put(ReplayPrefs.KEY_CLIENT_ID, mPrefs.getClientID());
        toReturn.mergeJSON(base); //add base passive data to json
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
        ReplayJsonObject json = new ReplayJsonObject(data);
        json.put(KEY_EVENT_NAME, event);

        return new ReplayRequest(RequestType.EVENTS, json);
    }

    /**
     * Build the ReplayRequest object for an alias request.
     *
     * @param alias The alias.
     * @return ReplayRequest object.
     */
    public static ReplayRequest requestForAlias(String alias) {
        ReplayJsonObject json = new ReplayJsonObject();
        json.put(RequestType.ALIAS.toString(), alias);

        return new ReplayRequest(RequestType.ALIAS, json);
    }

    /**
     * Send additional properties that are automatically tracked.
     *
     * @param previousJson the JsonObject to which to add the passive data
     */
    private static ReplayJsonObject collectPassiveData(ReplayJsonObject previousJson){
        try {
            ReplayJsonObject props = new ReplayJsonObject();

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

            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            props.put(DISPLAY_KEY,width+"x"+height);

            props.put(DISPLAY_KEY, display.getName());

            props.put(MANUFACTURER_KEY, Build.MANUFACTURER);

            props.put(MODEL_KEY, Build.MODEL);


            //network
            ReplayJsonObject network = new ReplayJsonObject();

            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            network.put(WIFI_KEY,String.valueOf(networkInfo.isConnected()));

            networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH); //might fail (silently, if there's a god) < 13;
            network.put(BLUETOOTH_KEY,String.valueOf(networkInfo.isConnected()));

            networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            network.put(MOBILE_KEY,String.valueOf(networkInfo.isConnected()));

            final TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String carrier = telephonyManager.getNetworkOperatorName();
            network.put(CARRIER_KEY,carrier);

            props.put(NETWORK_KEY, network);
            previousJson.put(PROPERTIES_KEY, props);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return previousJson;
    }

}