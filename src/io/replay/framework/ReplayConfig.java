package io.replay.framework;

public interface ReplayConfig {

    /** ReplayIO API key */
    public static final String KEY_REPLAY_KEY = "replay_key";

    public static final String KEY_DATA = "data";

    public static final String KEY_PROPERTIES = "properties";

    /** The server URL requests will be posted to */
    public static final String REPLAY_URL = "http://dyn.originatechina.com:3000/";

    /** The ReplayRequest type: alias. */
    public static final String REQUEST_TYPE_ALIAS = "alias";
    /** The ReplayRequest type: events. */
    public static final String REQUEST_TYPE_EVENTS = "events";




}
