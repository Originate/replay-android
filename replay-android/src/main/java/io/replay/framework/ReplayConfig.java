package io.replay.framework;

public interface ReplayConfig {

    /** ReplayIO API key */
    public static final String KEY_REPLAY_KEY = "replay_key";

    /** The server URL requests will be posted to */
    public static final String REPLAY_URL = "http://dyn.originatechina.com:3000/";


    public static enum RequestType{
    /** The ReplayRequest type: alias. */
        ALIAS,
    /** The ReplayRequest type: events. */
        EVENTS;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }


}
