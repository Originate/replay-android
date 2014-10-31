package io.replay.framework;

/**Config is the programmatic representation of the configuration xml that you place in <code>/res/values</code>.
 * It enforces certain minimum and maximum values by throwing {@link IllegalArgumentException}s. These values can be
 * found as static constants in this class.
 *
 */
public class Config {

    private static final int MIN_DISPATCH_INTERVAL = 5*1000; //5 seconds
    private static final int MIN_MAX_QUEUE = 100;
    private static final int MIN_FLUSH_AT = 10;
    private static final int MAX_DISPATCH_INTERVAL = 30*60*1000; //30 minutes
    private static final int MAX_MAX_QUEUE = 10000;
    private static final int MAX_FLUSH_AT = 1000;
    private static final String base = "%s cannot have a value %s than %d";

    private boolean debugModeEnabled;
    private boolean enabled;
    private String apiKey;
    private int dispatchInterval;
    private int flushAt;
    private int maxQueue;

    static class Defaults {
        static final boolean DEBUG_MODE_ENABLED = false;
        static final boolean ENABLED = true;
        static final String API_KEY = "";
        static final Integer DISPATCH_INTERVAL = 60*1000; //1 minutes
        static final Integer FLUSH_AT = 100;
        static final Integer MAX_QUEUE = 1200;
    }

    /**
     * Creates a default options
     */
    public Config(String apiKey){
        this(Defaults.DEBUG_MODE_ENABLED, Defaults.ENABLED, apiKey,
                  Defaults.DISPATCH_INTERVAL, Defaults.FLUSH_AT, Defaults.MAX_QUEUE);
    }

    /**Creates an option with the provided settings
     *
     * @param debugModeEnabled whether Replay.io logs to Logcat
     * @param enabled whether Replay.io accepts events
     * @param apiKey a Replay.io API key that may or may not be different from the one provided in the XML
     * @param dispatchInterval the interval (in MS) for when the queue should be flushed.
     * @param flushAt when the number of items in the queue reaches this number, the queue will be flushed
     * @param maxQueue the number at which we stop accepting items in the queue
     */
    public Config(boolean debugModeEnabled, boolean enabled, String apiKey,
           int dispatchInterval, int flushAt, int maxQueue) {

        String str = null;
        if (dispatchInterval !=0 && dispatchInterval < MIN_DISPATCH_INTERVAL){
            str = String.format(base, "\"dispatch interval\"", "less", MIN_DISPATCH_INTERVAL);
        } else if (dispatchInterval > MAX_DISPATCH_INTERVAL){
            str = String.format(base, "\"dispatch interval\"", "greater", + MAX_DISPATCH_INTERVAL);
        }

        if (flushAt < MIN_FLUSH_AT){
            str = String.format(base, "\"flush at\"", "less", + MIN_FLUSH_AT);
        } else if (flushAt > MAX_FLUSH_AT){
            str = String.format(base, "\"flush at\"", "greater", + + MAX_FLUSH_AT);
        }

        if (maxQueue < MIN_MAX_QUEUE){
            str = String.format(base, "\"max queue\"", "less", + MIN_MAX_QUEUE);
        }else if (maxQueue > MAX_MAX_QUEUE){
            str = String.format(base, "\"max queue\"", "greater", + + MAX_MAX_QUEUE);
        }

        if (Util.isNullOrEmpty(apiKey)){
            str = "ReplayIO - API key should not be null or empty.";
        }

        if(str != null) throw new IllegalArgumentException(str);

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
        if (Util.isNullOrEmpty(apiKey)){
            throw new IllegalArgumentException("ReplayIO - API key should not be null or empty.");
        }
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
        if (dispatchInterval !=0 && dispatchInterval < MIN_DISPATCH_INTERVAL){
            throw new IllegalArgumentException(String.format(base, "\"dispatch interval\"", "less", MIN_DISPATCH_INTERVAL));
        }
        else if (dispatchInterval > MAX_DISPATCH_INTERVAL){
            throw new IllegalArgumentException(String.format(base, "\"dispatch interval\"", "greater", + MAX_DISPATCH_INTERVAL));
        }

        this.dispatchInterval = dispatchInterval;
        return this;
    }

    public int getFlushAt() {
        return flushAt;
    }

    public Config setFlushAt(int flushAt){
        if (flushAt < MIN_FLUSH_AT){
            throw new IllegalArgumentException(String.format(base, "\"flush at\"", "less", + MIN_FLUSH_AT));
        }
        else if (flushAt > MAX_FLUSH_AT){
            throw new IllegalArgumentException(String.format(base, "\"flush at\"", "greater", + + MAX_FLUSH_AT));
        }
        this.flushAt = flushAt;
        return this;
    }

    public int getMaxQueue() {
        return maxQueue;
    }

    public Config setMaxQueue(int maxQueue){
        if (maxQueue < MIN_MAX_QUEUE){
            throw new IllegalArgumentException(String.format(base, "\"max queue\"", "less", + MIN_MAX_QUEUE));
        }
        else if (maxQueue > MAX_MAX_QUEUE){
            throw new IllegalArgumentException(String.format(base, "\"max queue\"", "greater", + + MAX_MAX_QUEUE));
        }
        this.maxQueue = maxQueue;
        return this;
    }
}
