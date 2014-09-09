package io.replay.framework.model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.replay.framework.network.ReplayNetworkManager;
import io.replay.framework.ReplayConfig;
import io.replay.framework.ReplayConfig.RequestType;
import io.replay.framework.ReplayIO;
import io.replay.framework.util.ReplayPrefs;

public class ReplayRequestFactory {

    private static final String KEY_EVENT_NAME = "event_name";
    private static final String NETWORK_KEY = "network";
    private static ReplayRequestFactory instance;

    public static ReplayRequestFactory init(Context context) {
        return instance == null ? instance = new ReplayRequestFactory(context) : instance;
    }

    private final static Map<String, String> base = new HashMap<String, String>(3);

    public ReplayRequestFactory(Context context) {
        ReplayPrefs mPrefs = ReplayPrefs.get(context.getApplicationContext());
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
    public static ReplayRequest requestForEvent(String event, Map<String, String> data, Map<String,String> network) throws JSONException {
        return new ReplayRequest(RequestType.EVENTS, jsonForEvent(event, data, network));
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
    private static JSONObject jsonForEvent(String event, Map<String, String> data, Map<String,String> network) throws JSONException {
        JSONObject json = new JSONObject(base);
        if (null == data) {
            data = new HashMap<String, String>();
        }
        JSONObject properties = new JSONObject(data);
        properties.put(NETWORK_KEY, new JSONObject(network));
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

}