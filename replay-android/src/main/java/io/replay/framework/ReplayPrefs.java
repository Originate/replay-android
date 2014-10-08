package io.replay.framework;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by parthpadgaonkar on 8/14/14.
 */
class ReplayPrefs {
    private static ReplayPrefs instance;

    private static final String prefsName = "ReplayIOPreferences";

    static final String KEY_CLIENT_ID = "client_id";
    static final String KEY_SESSION_ID = "session_id";
    static final String KEY_DISTINCT_ID = "distinct_id";

    private final SharedPreferences mPrefs;

    static ReplayPrefs get(Context context) {
        return instance == null ? instance = new ReplayPrefs(context) : instance;
    }

    private ReplayPrefs(Context context) {
        mPrefs = context.getApplicationContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }

    void setSessionID(String sessionID){
        mPrefs.edit().putString(KEY_SESSION_ID, sessionID).commit();
    }

    String getSessionID(){
        return mPrefs.getString(KEY_SESSION_ID, "");
    }

    void setClientID(String clientUUID) {
        mPrefs.edit().putString(KEY_CLIENT_ID, clientUUID).commit();
    }

    String getClientID() {
        return mPrefs.getString(KEY_CLIENT_ID, "");
    }

    void setDistinctID(String distinctID) {
        mPrefs.edit().putString(KEY_DISTINCT_ID, distinctID).commit();
    }

    String getDistinctID(){
        return mPrefs.getString(KEY_DISTINCT_ID, "");
    }

}
