package io.replay.framework.util;

/**
 * Client configuration
 */
public class Config {

    /**
     * Whether or not debug logging is enabled to ADT logcat
     */
    private boolean debugModeEnabled;
    private boolean enabled;
    private String apiKey;
    private int dispatchInterval;
    private int flushAt;
    private int maxQueue;

    /**
     * Creates a default options
     */
    public Config() {
        this(Defaults.DEBUG_MODE_ENABLED, Defaults.ENABLED, Defaults.API_KEY,
                Defaults.DISPATCH_INTERVAL, Defaults.FLUSH_AT, Defaults.MAX_QUEUE);
    }

    /**
     * Creates an option with the provided settings
     */
    Config(boolean debugModeEnabled, boolean enabled, String apiKey,
           int dispatchInterval, int flushAt, int maxQueue) {

        this.debugModeEnabled = debugModeEnabled;
        this.enabled = enabled;
        this.apiKey = apiKey;
        this.dispatchInterval = dispatchInterval;
        this.flushAt = flushAt;
        this.maxQueue = maxQueue;
    }

    public boolean isDebug() {
        return debugModeEnabled;
    }

    public Config setDebug(boolean debug){
        this.debugModeEnabled = debug;
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
        return apiKey;
    }

    public Config setApiKey(String apiKey){
        this.apiKey = apiKey;
        return this;
    }

    public int getDispatchInterval() {
        return dispatchInterval;
    }

    /**Set the timer for this many MS
     *
     * @param dispatchInterval
     * @return
     */
    public Config setDispatchInterval(int dispatchInterval){
        this.dispatchInterval = dispatchInterval;
        return this;
    }

    public int getFlushAt() {
        return flushAt;
    }

    public Config setFlushAt(int flushAt){
        this.flushAt = flushAt;
        return this;
    }

    public int getMaxQueue() {
        return maxQueue;
    }

    public Config setMaxQueue(int maxQueue){
        this.maxQueue = maxQueue;
        return this;
    }
}
