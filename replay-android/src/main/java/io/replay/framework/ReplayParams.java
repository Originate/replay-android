package io.replay.framework;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by arichunter on 8/25/14.
 */
class ReplayParams {

    private static final String STRING_RESOURCE_KEY = "string";
    private static final String INTEGER_RESOURCE_KEY = "integer";
    private static final String BOOLEAN_RESOURCE_KEY = "bool";

    /* API parameters
    *  api_key - the ReplayIO api key for the developer
    *  enabled - boolean specifying whether event tracking via ReplayIO is enabled
    *  debug_mode_enabled - boolean specifying whether to print debugging information
     */
    private static final String ENABLED = "enabled";
    private static final String DEBUG_MODE_ENABLED = "debug_mode_enabled";
    private static final String API_KEY = "api_key";

    /* ReplayIO event queue parameters:
    *  Max Queue - The maximum number of events that can be stored in the queue. Once this value is
    *  events will be ignored
    *  Flush At - The maximum number of events that can be stored in the queue before the events are
    *  automatically sent to the server.
    *  Dispatch Interval - The duration (in milliseconds) between when events are sent to the
    *  server. If tis value is 0, then events are sent as soon as they are received.
     */
    private static final String DISPATCH_INTERVAL = "dispatch_interval";
    private static final String MAX_QUEUE = "max_queue";
    private static final String FLUSH_AT = "flush_at";

    private ReplayParams(){} //private constructor

    static Config getOptions(Context context) {
        Config options = new Config();

        Boolean enabled = getBoolean(context, ENABLED);
        if (enabled != null) options.setEnabled(enabled);

        Boolean debug = getBoolean(context, DEBUG_MODE_ENABLED);
        if (debug != null) options.setDebug(debug);

        String apiKey = getString(context, API_KEY);
        if (apiKey != null) options.setApiKey(apiKey);

        Integer dispatchInterval = getInteger(context, DISPATCH_INTERVAL);
        //Ensure that the value is not null
        if (dispatchInterval != null) {
            options.setDispatchInterval(dispatchInterval);
        }

        Integer maxQueue = getInteger(context, MAX_QUEUE);
        //Ensure that the value is not null
        if (maxQueue != null){
            options.setMaxQueue(maxQueue);
        }

        Integer flushAt = getInteger(context, FLUSH_AT);
        //Ensure that the value is not null
        if (flushAt != null){
            options.setFlushAt(flushAt);
        }

        return options;
    }

    private static String getString(Context context, String key) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier(key, STRING_RESOURCE_KEY, context.getPackageName());
        if (id > 0) {
            return resources.getString(id);
        } else {
            return null;
        }
    }

    private static Integer getInteger(Context context, String key) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier(key, INTEGER_RESOURCE_KEY, context.getPackageName());
        if (id > 0) {
            return resources.getInteger(id);
        } else {
            return null;
        }
    }

    private static Boolean getBoolean(Context context, String key) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier(key, BOOLEAN_RESOURCE_KEY, context.getPackageName());
        if (id > 0) {
            return resources.getBoolean(id);
        } else {
            return null;
        }
    }

}
