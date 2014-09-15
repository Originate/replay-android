package io.replay.framework.model;

import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import io.replay.framework.ReplayConfig;
import io.replay.framework.ReplayConfig.RequestType;
import io.replay.framework.ReplayIO;
import io.replay.framework.network.ReplayNetworkManager;
import io.replay.framework.util.ReplayPrefs;

public class ReplayRequestFactory {

    private static final String KEY_EVENT_NAME = "event_name";
    private static final String NETWORK_KEY = "network";
    private static ReplayRequestFactory instance;
    private static Context mContext;

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

    public static ReplayRequestFactory init(Context context) {
        return instance == null ? instance = new ReplayRequestFactory(context.getApplicationContext()) : instance;
    }

    private final static Map<String, String> base = new HashMap<String, String>(3);

    public ReplayRequestFactory(Context context) {
        ReplayPrefs mPrefs = ReplayPrefs.get(context);
        mContext = context;
        base.put(ReplayConfig.KEY_REPLAY_KEY, ReplayIO.getConfig().getApiKey());
        base.put(ReplayPrefs.KEY_CLIENT_ID, mPrefs.getClientID());
        base.put(ReplayPrefs.KEY_DISTINCT_ID, mPrefs.getDistinctID());
    }


    /**
     * Build the ReplayRequest object for an event request.
     *
     * @param event The event name.
     * @param data  The name-value paired data.
     * @return ReplayRequest object.
     * @throws org.json.JSONException
     */
    public static ReplayRequest requestForEvent(String event, Map<String, String> data) throws JSONException {
        return new ReplayRequest(RequestType.EVENTS, jsonForEvent(event, data));
    }

    /**
     * Build the ReplayRequest object for an alias request.
     *
     * @param alias The alias.
     * @return ReplayRequest object.
     * @throws org.json.JSONException
     */
    public static ReplayRequest requestForAlias(String alias) throws JSONException {
        return new ReplayRequest(RequestType.ALIAS, jsonForAlias(alias));
    }

    /**
     * Generate the JSONObject for a event request.
     *
     * @param event The event name.
     * @param data  The name-value paired data. Can be null.
     * @return The JSONObject of data.
     * @throws org.json.JSONException
     */
    private static JSONObject jsonForEvent(String event, Map<String, String> data) throws JSONException {
        JSONObject json = new JSONObject(base);
        if (null == data) {
            data = new HashMap<String, String>();
        }
        JSONObject properties = new JSONObject(addPassiveData(data));
        properties.put(NETWORK_KEY, new JSONObject(getNetworkData()));
        data.put(KEY_EVENT_NAME, event);

        json.put(ReplayNetworkManager.KEY_DATA, properties);
        return json;
    }

    /**
     * Generate the JSONObject for a alias request.
     *
     * @param alias The alias.
     * @return The JSONObject of data.
     * @throws org.json.JSONException
     */
    private static JSONObject jsonForAlias(String alias) throws JSONException {
        JSONObject json = new JSONObject(base);
        json.put(RequestType.ALIAS.toString(), alias);
        return json;
    }

    /**
     * Create a dictionary of network properties .
     *
     */
    public static Map<String,String> getNetworkData() {

        Map<String,String> network = new HashMap<String, String>();

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean usingWifi = networkInfo.isConnected();
        network.put(WIFI_KEY,String.valueOf(usingWifi));

        networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
        boolean usingBlueTooth = networkInfo.isConnected();
        network.put(BLUETOOTH_KEY,String.valueOf(usingBlueTooth));

        networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean usingCellular = networkInfo.isConnected();
        network.put(MOBILE_KEY,String.valueOf(usingCellular));

        final TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String carrier = telephonyManager.getNetworkOperatorName();
        network.put(CARRIER_KEY,carrier);

        return network;
    }

    /**
     * Send additional properties that are automatically tracked .
     *
     * @param data      {@link Map} object stores key-value pairs.
     */
    public static Map<String,String> addPassiveData(Map<String,String> data){
        try {
            Date currentTime = new Date();
            DateFormat ausFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            ausFormat.setTimeZone(TimeZone.getDefault());
            data.put(TIME_KEY,ausFormat.format(currentTime));

            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            Criteria crit = new Criteria();
            crit.setPowerRequirement(Criteria.POWER_LOW);
            crit.setAccuracy(Criteria.ACCURACY_FINE); //used to be Criteria.ACCURACY_COARSE
            String provider = locationManager.getBestProvider(crit, true);

            if (provider != null) {
                Location location;
                try {
                    location = locationManager.getLastKnownLocation(provider); // this is a cached location
                } catch (SecurityException ex) {
                    //The application may not have permission to access location
                    location = null;
                }
                if (location != null) {
                    data.put("latitude", String.valueOf(location.getLatitude()));
                    data.put("longitude", String.valueOf(location.getLongitude()));
                }
            }

            data.put(OS_KEY, Build.VERSION.RELEASE);

            data.put(SDK_KEY, Integer.toString(VERSION.SDK_INT));

            WindowManager window = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            data.put(DISPLAY_KEY,display.getName());

            data.put(MANUFACTURER_KEY, Build.MANUFACTURER);

            data.put(MODEL_KEY, Build.MODEL);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

}