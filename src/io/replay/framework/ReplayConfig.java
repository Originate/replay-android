package io.replay.framework;

public interface ReplayConfig {

    /** ReplayIO API key */
    public static final String KEY_REPLAY_KEY = "replay_key";
    public static final String KEY_CLIENT_ID = "client_id";
    public static final String KEY_SESSION_ID = "session_id";
    public static final String KEY_DATA = "data";
    public static final String KEY_DISTINCT_ID = "distinct_id";
    public static final String KEY_EVENT_NAME = "event_name";
    public static final String KEY_PROPERTIES = "properties";

    /** The server URL requests will be posted to */
    public static final String REPLAY_URL = "http://dyn.originatechina.com:3000/";

    /** The ReplayRequest type: alias. */
    public static final String REQUEST_TYPE_ALIAS = "alias";
    /** The ReplayRequest type: events. */
    public static final String REQUEST_TYPE_EVENTS = "events";

    public static final String PREFERENCES = "ReplayIOPreferences";
    /** SharedPreferences keys */
    public static final String PREF_DISPATCH_INTERVAL = "dispatchInterval";
    public static final String PREF_ENABLED = "enabled";
    public static final String PREF_DEBUG_MODE_ENABLED = "debugMode";
    public static final String PREF_DISTINCT_ID = "distinctId";
}
