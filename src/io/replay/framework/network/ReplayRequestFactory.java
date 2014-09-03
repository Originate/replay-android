package io.replay.framework.network;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.replay.framework.ReplayAPIManager;
import io.replay.framework.ReplayIO;
import io.replay.framework.ReplayRequest;
import io.replay.framework.util.ReplayPrefs;

public class ReplayRequestFactory {

    private static final String KEY_EVENT_NAME = "event_name";
    private static ReplayRequestFactory instance;
    private final ReplayPrefs mPrefs;

    public static ReplayRequestFactory get(Context context) {
        return instance == null ? instance = new ReplayRequestFactory(context) : instance;
    }

    public ReplayRequestFactory(Context context) {
        mPrefs = ReplayPrefs.get(context);
    }


    /**
     * Build the ReplayRequest object for an event request.
     *
     * @param event The event name.
     * @param data  The name-value paired data.
     * @return ReplayRequest object.
     * @throws org.json.JSONException
     */
    public ReplayRequest requestForEvent(String event, Map<String, String> data) throws JSONException {
        return new ReplayRequest(ReplayAPIManager.REQUEST_TYPE_EVENTS, jsonForEvent(event, data, mPrefs));
    }

    /**
     * Build the ReplayRequest object for an alias request.
     *
     * @param alias The alias.
     * @return ReplayRequest object.
     * @throws org.json.JSONException
     */
    public ReplayRequest requestForAlias(String alias) throws JSONException {
        return new ReplayRequest(ReplayAPIManager.REQUEST_TYPE_ALIAS, jsonForAlias(alias, mPrefs));
    }

    /**
     * Generate the JSONObject for a event request.
     *
     * @param event The event name.
     * @param data  The name-value paired data.
     * @param mPrefs
     * @return The JSONObject of data.
     * @throws org.json.JSONException
     */
    private static JSONObject jsonForEvent(String event, Map<String, String> data, ReplayPrefs mPrefs) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ReplayAPIManager.KEY_REPLAY_KEY, ReplayIO.getConfig().getApiKey());
        json.put(ReplayPrefs.KEY_CLIENT_ID, mPrefs.getClientID());
        json.put(ReplayPrefs.KEY_SESSION_ID, mPrefs.getSessionID());
        if (mPrefs.getDistinctID() != null && !(mPrefs.getDistinctID().length() == 0)) {
            json.put(ReplayPrefs.KEY_DISTINCT_ID, mPrefs.getDistinctID());
        }
        json.put(KEY_EVENT_NAME, event);
        if (null == data) {
            data = new HashMap<String, String>();
        }
        data.put(KEY_EVENT_NAME, event);
        json.put(ReplayAPIManager.KEY_DATA, new JSONObject(data));
        return json;
    }

    /**
     * Generate the JSONObject for a alias request.
     *
     * @param alias The alias.
     * @param mPrefs
     * @return The JSONObject of data.
     * @throws org.json.JSONException
     */
    private static JSONObject jsonForAlias(String alias, ReplayPrefs mPrefs) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ReplayAPIManager.KEY_REPLAY_KEY, ReplayIO.getConfig().getApiKey());
        json.put(ReplayPrefs.KEY_CLIENT_ID, mPrefs.getClientID());
        json.put(ReplayPrefs.KEY_DISTINCT_ID, mPrefs.getDistinctID());
        json.put("alias", alias);
        return json;
    }

}