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
    private String distinct_id;
    private String session_id;
    private String client_id;
    private Integer dispatch_interval;
    private Integer flush_at;
    private Integer max_queue;

    /**
     * Creates a default options
     */
    public Config() {
        this(Defaults.DEBUG_MODE_ENABLED, Defaults.ENABLED,
                Defaults.API_KEY,
                Defaults.DISPATCH_INTERVAL, Defaults.FLUSH_AT, Defaults.MAX_QUEUE);
    }

    /**
     * Creates an option with the provided settings
     */
    Config(boolean debug_mode_enabled, boolean enabled,
           String api_key,
           Integer dispatch_interval, Integer flush_at, Integer max_queue) {

        this.debug_mode_enabled = debug_mode_enabled;
        this.enabled = enabled;
        this.api_key = api_key;
        this.dispatch_interval = dispatch_interval;
        this.flush_at = flush_at;
        this.max_queue = max_queue;
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

    public Integer getFlushAt() {
        return flush_at;
    }

    public Config setFlushAt(Integer flushAt){
        this.flush_at = flushAt;
        return this;
    }

    public Integer getMaxQueue() {
        return max_queue;
    }

    public Config setMaxQueue(Integer maxQueue){
        this.max_queue = maxQueue;
        return this;
    }
}
