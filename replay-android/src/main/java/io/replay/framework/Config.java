package io.replay.framework;

/**
 * Client configuration
 */
class Config {

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
    Config() {
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

    boolean isDebug() {
        return debugModeEnabled;
    }

    Config setDebug(boolean debug){
        this.debugModeEnabled = debug;
        return this;
    }

    boolean isEnabled() {
        return enabled;
    }

    Config setEnabled(boolean enabled){
        this.enabled = enabled;
        return this;
    }

    String getApiKey() {
        return apiKey;
    }

    Config setApiKey(String apiKey){
        this.apiKey = apiKey;
        return this;
    }

    int getDispatchInterval() {
        return dispatchInterval;
    }

    /**Set the timer for this many MS
     *
     * @param dispatchInterval
     * @return
     */
    Config setDispatchInterval(int dispatchInterval){
        this.dispatchInterval = dispatchInterval;
        return this;
    }

    int getFlushAt() {
        return flushAt;
    }

    Config setFlushAt(int flushAt){
        this.flushAt = flushAt;
        return this;
    }

    int getMaxQueue() {
        return maxQueue;
    }

    Config setMaxQueue(int maxQueue){
        this.maxQueue = maxQueue;
        return this;
    }
}
