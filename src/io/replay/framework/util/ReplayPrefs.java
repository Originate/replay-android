package io.replay.framework.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by parthpadgaonkar on 8/14/14.
 */
public class ReplayPrefs {
    private static ReplayPrefs instance;

    private static final String prefsName = "ReplayIOPreferences";

    public static final String KEY_CLIENT_ID = "client_id";
    public static final String KEY_SESSION_ID = "session_id";
    public static final String KEY_DISTINCT_ID = "distinct_id";

    private static final String PREF_DISPATCH_INTERVAL = "dispatchInterval";
    private static final String PREF_ENABLED = "enabled";
    private static final String PREF_DEBUG_MODE_ENABLED = "debugMode";
    private static final String PREF_DISTINCT_ID = "distinctId";
    private static final String PREF_API_KEY = "apiKey";

    private final SharedPreferences mPrefs;

    public static ReplayPrefs get(Context context) {
        return instance == null ? instance = new ReplayPrefs(context) : instance;
    }

    private ReplayPrefs(Context context) {
        mPrefs = context.getApplicationContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }

    public void setSessionID(String sessionID){
        mPrefs.edit().putString(KEY_SESSION_ID, sessionID).commit();
    }

    public String getSessionID(){
        return mPrefs.getString(KEY_SESSION_ID, "");
    }

    public void setAPIKey(String key){
        mPrefs.edit().putString(PREF_API_KEY, key).commit();
    }

    public String getAPIKey(){
        return mPrefs.getString(PREF_API_KEY, "");
    }

    public void setEnabled(boolean enable){
        mPrefs.edit().putBoolean(PREF_ENABLED, enable).commit();
    }

    public boolean getEnabled(){
        return mPrefs.getBoolean(PREF_ENABLED, false);
    }

    public void setDebugMode(boolean debug){
        mPrefs.edit().putBoolean(PREF_DEBUG_MODE_ENABLED, debug).commit();
    }

    public boolean getDebugMode(){
        return mPrefs.getBoolean(PREF_DEBUG_MODE_ENABLED, false);
    }

    public void setClientUUID(String clientUUID) {
        mPrefs.edit().putString(KEY_CLIENT_ID, clientUUID).commit();
    }

    public String getClientUUID(){
        return mPrefs.getString(KEY_CLIENT_ID, "");
    }

    public void setDistinctID(String distinctID) {
        mPrefs.edit().putString(PREF_DISTINCT_ID, distinctID).commit();
    }

    public String getDistinctID(){
        return mPrefs.getString(KEY_DISTINCT_ID, "");
    }

    public int getDispatchInterval(){
        return mPrefs.getInt(PREF_DISPATCH_INTERVAL, 0);
    }

    public void setDispatchInterval(int interval) {
        mPrefs.edit().putInt(PREF_DISPATCH_INTERVAL, interval).commit();
    }
}
