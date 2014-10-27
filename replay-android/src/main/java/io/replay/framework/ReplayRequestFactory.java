package io.replay.framework;

import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

import io.replay.framework.ReplayRequest.RequestType;

class ReplayRequestFactory {

    private static final String KEY_REPLAY_KEY = "replay_key";
    private static final String KEY_EVENT_NAME = "event_name";
    private static final String KEY_TIME = "timestamp";
    private static final String KEY_PROPERTIES = "properties";
    private static final String KEY_BROWSER_INFO = "browser_info";

    private ReplayRequestFactory() {} //private constructor

    static ReplayRequest createRequest(Context context, RequestType type, String eventName, ReplayJsonObject properties, Object... extras) {
        ReplayJsonObject json = init(context, type, eventName, properties);
        properties.mergeJSON(new ReplayJsonObject(extras));
        return new ReplayRequest(type, json);
    }

    static ReplayRequest createRequest(Context context, RequestType type, String eventName, ReplayJsonObject properties, Map<String, ?> extras) {
        ReplayJsonObject json = init(context, type, eventName, properties);
        properties.mergeJSON(new ReplayJsonObject(extras));
        return new ReplayRequest(type, json);
    }

    private static ReplayJsonObject init(Context context, RequestType type, String eventName, ReplayJsonObject properties) {
        ReplayJsonObject json = new ReplayJsonObject();

        ReplayPrefs prefs = ReplayPrefs.get(context);
        String distinctID = prefs.getDistinctID();
        if (!Util.isNullOrEmpty(distinctID)) {
            json.put(ReplayPrefs.KEY_DISTINCT_ID, distinctID);
        }
        json.put(KEY_REPLAY_KEY, ReplayIO.getConfig().getApiKey());
        json.put(ReplayPrefs.KEY_CLIENT_ID, prefs.getClientID());
        json.put(KEY_BROWSER_INFO, new ReplayJsonObject());
        //TODO find out what info is required here. Chances are, we're collecting it elsewhere

        if (type == RequestType.EVENTS && !Util.isNullOrEmpty(eventName)) {
            json.put(KEY_EVENT_NAME, eventName);
        }
        json.put(KEY_PROPERTIES, properties);
        return json;
    }

    static void updateTimestamp(ReplayRequest request) {
        ReplayJsonObject json = request.getJsonBody();
        JSONObject prop = json.getJsonObject(KEY_PROPERTIES);
        if (prop != null) {
            ReplayJsonObject props = (ReplayJsonObject) prop;
            long delta = (System.nanoTime() - request.getCreatedAt()) / 1000000L; //convert ns -> ms
            if (delta > 0) props.put(KEY_TIME, delta); //if the system has restarted, createdAt is useless.
            json.put(KEY_PROPERTIES, props); //it's clobberin' time!
        }
        request.setJsonBody(json);
    }
}