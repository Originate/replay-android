package io.replay.framework.util;

/**
 * Client configuration
 */
public class Config {

    /**
     * Whether or not debug logging is enabled to ADT logcat
     */
    private boolean debug_mode_enabled;
    private boolean enabled;
    private String api_key;
    private String session_id;
    private String client_id;
    private String distinct_id;
    private Integer dispatch_interval;

    /**
     * Creates a default options
     */
    public Config() {
        this(Defaults.DEBUG_MODE_ENABLED, Defaults.ENABLED,
                Defaults.API_KEY, Defaults.SESSION_ID, Defaults.CLIENT_ID, Defaults.DISTINCT_ID,
                Defaults.DISPATCH_INTERVAL);
    }

    /**
     * Creates an option with the provided settings
     */
    Config(boolean debug_mode_enabled, boolean enabled,
           String api_key, String session_id, String client_id, String distinct_id,
           Integer dispatch_interval) {

        setDebug(debug_mode_enabled);
        setEnabled(enabled);
        setApiKey(api_key);
        setSessionId(session_id);
        setClientId(client_id);
        setDistinctId(distinct_id);
        setDispatchInterval(dispatch_interval);
    }

    public boolean isDebug() {
        return debug_mode_enabled;
    }

    public Config setDebug(boolean debug){
        this.debug_mode_enabled = debug;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Config setEnabled(boolean enabled){
        this.enabled = enabled;
        return this;
    }

    public String getApiKey() {
        return api_key;
    }

    public Config setApiKey(String apiKey){
        this.api_key = apiKey;
        return this;
    }

    public String getSessionId() {
        return session_id;
    }

    public Config setSessionId(String sessionId){
        this.session_id = sessionId;
        return this;
    }

    public String getClientId() {
        return client_id;
    }

    public Config setClientId(String clientId){
        this.client_id = clientId;
        return this;
    }

    public String getDistinctId() {
        return distinct_id;
    }

    public Config setDistinctId(String distinctId){
        this.distinct_id = distinctId;
        return this;
    }

    public Integer getDispatchInterval() {
        return dispatch_interval;
    }

    public Config setDispatchInterval(Integer dispatchInterval){
        this.dispatch_interval = dispatchInterval;
        return this;
    }
}
