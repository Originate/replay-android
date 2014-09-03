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

    public void setClientID(String clientUUID) {
        mPrefs.edit().putString(KEY_CLIENT_ID, clientUUID).commit();
    }

    public String getClientID() {
        return mPrefs.getString(KEY_CLIENT_ID, "");
    }

    public void setDistinctID(String distinctID) {
        mPrefs.edit().putString(KEY_DISTINCT_ID, distinctID).commit();
    }

    public String getDistinctID(){
        return mPrefs.getString(KEY_DISTINCT_ID, "");
    }

}
