package io.replay.framework;

/**
 * Client configuration
 */
public class Config {

    private static final int MIN_DISPATCH_INTERVAL = 5*1000; //5 seconds
    private static final int MIN_MAX_QUEUE = 100;
    private static final int MIN_FLUSH_AT = 10;
    private static final int MAX_DISPATCH_INTERVAL = 30*60*1000; //30 minutes
    private static final int MAX_MAX_QUEUE = 10000;
    private static final int MAX_FLUSH_AT = 1000;


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
    public Config(boolean debugModeEnabled, boolean enabled, String apiKey,
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

    public void applyConstraints(){
        if (dispatchInterval < MIN_DISPATCH_INTERVAL){
            throw new IllegalArgumentException("Dispatch Interval cannot have a value less than " + MIN_DISPATCH_INTERVAL);
        }
        else if (dispatchInterval > MAX_DISPATCH_INTERVAL){
            throw new IllegalArgumentException("Dispatch Interval cannot have a value greater than " + MAX_DISPATCH_INTERVAL);
        }

        if (flushAt < MIN_FLUSH_AT){
            throw new IllegalArgumentException("Flush At cannot have a value less than " + MIN_FLUSH_AT);
        }
        else if (flushAt > MAX_FLUSH_AT){
            throw new IllegalArgumentException("Flush At cannot have a value greater than " + MAX_FLUSH_AT);
        }

        if (maxQueue < MIN_MAX_QUEUE){
            throw new IllegalArgumentException("Max Queue cannot have a value less than " + MIN_MAX_QUEUE);
        }
        else if (maxQueue > MAX_MAX_QUEUE){
            throw new IllegalArgumentException("Max Queue cannot have a value greater than " + MAX_MAX_QUEUE);
        }

        if (apiKey==null || apiKey.equals("")){
            throw new IllegalArgumentException("ReplayIO - API key should not be null or empty.");
        }
    }
}
