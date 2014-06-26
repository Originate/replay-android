package io.replay.framework;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ReplayIO {

    private static boolean debugMode;
    private static String replayApiKey;
    private static String clientUUID;
    private static boolean enabled;
    private static ReplayAPIManager replayAPIManager;
    private static ReplayQueue replayQueue;
    private static ReplayIO mInstance;
    private static Context mContext;
    private static boolean initialized;
    private static SharedPreferences mPrefs;

    private static int started;
    private static int resumed;
    @SuppressWarnings("unused")
    private static int paused;
    private static int stopped;

    /**
     * Private constructor to create an instance.
     *
     * @param context The application context.
     */
    private ReplayIO(Context context) {
        mContext = context;
        initialized = false;

        mPrefs = mContext.getSharedPreferences("ReplayIOPreferences", Context.MODE_PRIVATE);
    }

    /**
     * Initializes ReplayIO client.  Previous state of enable/disable
     * and debugMode are loaded. {@link ReplayQueue} is initialized and started.
     * Previous value of dispatchInterval is loaded, too. If there are persisted requests
     * on disk, load them into queue.
     *
     * @param context The application context.  Use application context instead of activity context
     *                to avoid the risk of memory leak.
     * @param apiKey  The API key from <a href="http://replay.io">replay.io</a>.
     * @return An initialized ReplayIO object.
     */
    public static ReplayIO init(Context context, String apiKey) {
        if (mInstance == null) {
            mInstance = new ReplayIO(context);
            replayApiKey = apiKey;
        }
        // load the default settings
        enabled = mPrefs.getBoolean(ReplayConfig.PREF_ENABLED, true);
        debugMode = mPrefs.getBoolean(ReplayConfig.PREF_DEBUG_MODE_ENABLED, false);

        // initialize ReplayAPIManager
        replayAPIManager = new ReplayAPIManager(replayApiKey, getOrGenerateClientUUID(),
                ReplaySessionManager.sessionUUID(context),
                mPrefs.getString(ReplayConfig.PREF_DISTINCT_ID, ""));

        // initialize ReplayQueue
        replayQueue = new ReplayQueue(replayAPIManager);
        replayQueue.setDispatchInterval(mPrefs.getInt(ReplayConfig.PREF_DISPATCH_INTERVAL, 0));
        replayQueue.start();

        try {
            replayQueue.loadQueueFromDisk(mContext);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initialized = true;
        return mInstance;
    }

    /**
     * Update the API key.  The {@link ReplayAPIManager} instance will be updated, too.
     *
     * @param apiKey The API key from <a href="http://replay.io>replay.io</a>.
     */
    public static void trackWithAPIKey(String apiKey) {
        checkInitialized();
        replayApiKey = apiKey;
        replayAPIManager = new ReplayAPIManager(replayApiKey, getOrGenerateClientUUID(),
                ReplaySessionManager.sessionUUID(mContext), getDistinctId());
    }

    /**
     * Send event with data to server.
     *
     * @param eventName Name of the event.
     * @param data      {@link Map} object stores key-value pairs.
     */
    public static void trackEvent(String eventName, final Map<String, String> data) {
        checkInitialized();
        if (!enabled) return;
        ReplayRequest request;
        try {
            request = replayAPIManager.requestForEvent(eventName, data);
            replayQueue.enqueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update alias.  Send a alias request to server side.
     *
     * @param userAlias New alias.
     */
    public static void updateAlias(String userAlias) {
        checkInitialized();
        if (!enabled) return;
        ReplayRequest request;
        try {
            request = replayAPIManager.requestForAlias(userAlias);
            replayQueue.enqueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set interval for dispatches.  <br>
     * When interval is set to negative, dispatcher will wait for manual {@link ReplayIO#dispatch()}.<br>
     * When interval is set to 0, dispatcher will send the newly added requests immediately.<br>
     * When interval is set to positive, dispatcher will work periodically.
     *
     * @param interval Interval in seconds.
     */
    public static void setDispatchInterval(int interval) {
        checkInitialized();
        replayQueue.setDispatchInterval(interval);

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(ReplayConfig.PREF_DISPATCH_INTERVAL, interval);
        editor.commit();
    }

    /**
     * Get interval for dispatches.
     *
     * @return Dispatch interval in seconds.
     */
    public static int getDispatchInterval() {
        checkInitialized();
        return mPrefs.getInt(ReplayConfig.PREF_DISPATCH_INTERVAL, 0);
    }

    /**
     * Dispatch immediately.  When triggered, all request in queue will be sent.
     */
    public static void dispatch() {
        checkInitialized();
        if (!enabled) return;
        replayQueue.dispatch();
    }

    /**
     * Enable ReplayIO.  Allow sending requests.
     */
    public static void enable() {
        checkInitialized();
        enabled = true;

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(ReplayConfig.PREF_ENABLED, true);
        editor.commit();
    }

    /**
     * Disable ReplayIO.  Disallow sending requests.
     */
    public static void disable() {
        checkInitialized();
        enabled = false;

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(ReplayConfig.PREF_ENABLED, false);
        editor.commit();
    }

    /**
     * Tell if ReplayIO is enabled.
     *
     * @return True if is enabled, false otherwise.
     */
    public static boolean isEnabled() {
        checkInitialized();
        enabled = mPrefs.getBoolean(ReplayConfig.PREF_ENABLED, true);
        return enabled;
    }

    /**
     * Set debug mode.  When debug mode is on, logs will be printed.
     *
     * @param debug Boolean value to set to.
     */
    public static void setDebugMode(boolean debug) {
        checkInitialized();
        debugMode = debug;

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(ReplayConfig.PREF_DEBUG_MODE_ENABLED, debugMode);
        editor.commit();
    }

    /**
     * Tell if debug mode is enabled.
     *
     * @return True if enabled, false otherwise.
     */
    public static boolean isDebugMode() {
        checkInitialized();
        debugMode = mPrefs.getBoolean(ReplayConfig.PREF_DEBUG_MODE_ENABLED, false);
        return debugMode;
    }

    /**
     * Called when the app entered background.  {@link ReplayQueue} will stop running,
     * requests in queue will be saved to disk. Session will be ended, too.
     *.
     * @see ReplayApplication
     */
    public static void stop() {
        checkInitialized();
        replayQueue.stop();

        try {
            replayQueue.saveQueueToDisk(mContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ReplaySessionManager.endSession(mContext);
    }

    /**
     * Called when the app entered foreground.  {@link ReplayQueue} will be restarted.
     * A new session is started. If there are persisted requests, load them into queue.
     *
     * @see ReplayApplication
     */
    public static void run() {
        checkInitialized();
        replayQueue = new ReplayQueue(replayAPIManager);
        replayQueue.start();
        replayQueue.setDispatchInterval(getDispatchInterval());
        replayAPIManager.updateSessionUUID(ReplaySessionManager.sessionUUID(mContext));

        try {
            replayQueue.loadQueueFromDisk(mContext);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tell if the ReplayIO is running, ie. ReplayQueue is running.
     *
     * @return True if it is running, false otherwise.
     */
    public static boolean isRunning() {
        if (!initialized) {
            return false;
        }
        return replayQueue.isRunning();
    }

    /**
     * Stop if ReplayIO is not initialized.
     *
     * @throws ReplayIONotInitializedException when called before {@link #init(android.content.Context, String)}.
     */
    private static void checkInitialized() throws ReplayIONotInitializedException {
        if (!initialized) {
            throw new ReplayIONotInitializedException();
        }
    }

    /**
     * Get or generate a UUID as the client UUID.  Generated client UUID will be saved.
     *
     * @return Client UUID.
     */
    public static String getClientUUID() {
        if (null == mPrefs) {
            throw new ReplayIONotInitializedException();
        }

        return getOrGenerateClientUUID();
    }

    private static String getOrGenerateClientUUID() {
        if (clientUUID == null) {
            if (!mPrefs.contains(ReplayConfig.KEY_CLIENT_ID)) {
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString(ReplayConfig.KEY_CLIENT_ID, UUID.randomUUID().toString());
                editor.commit();
                ReplayIO.debugLog("Generated new client uuid");
            }
            return mPrefs.getString(ReplayConfig.KEY_CLIENT_ID, "");
        }
        return clientUUID;
    }

    /**
     * Print debug log if debug mode is on.
     *
     * @param log The debug log to be printed.
     */
    public static void debugLog(String log) {
        if (debugMode) {
            Log.d("REPLAY_IO", log);
        }
    }

    /**
     * Print error log if debug mode is on.
     *
     * @param log The error log to be printed.
     */
    public static void errorLog(String log) {
        if (debugMode) {
            Log.e("REPLAY_IO", log);
        }
    }

    /**
     * Call this in your Activity's onStart() method, to track the status of your app.
     */
    public static void activityStart() {
        started++;

        checkAppVisibility();
    }

    /**
     * Call this in your Activity's onResume() method, to track the status of your app.
     */
    public static void activityResume() {
        resumed++;

        checkAppVisibility();
    }

    /**
     * Call this in your Activity's onPause() method, tracking the status of your app.
     */
    public static void activityPause() {
        paused++;
    }

    /**
     * Call this in your Activity's onStop() method, tracking the status of your app.
     */
    public static void activityStop() {
        stopped++;

        checkAppVisibility();
    }

    private static boolean isApplicationVisible() {
        return started > stopped;
    }

    private static boolean isApplicationInForeground() {
        return resumed > stopped;
    }

    private static void checkAppVisibility() {
        try {
            if (!isApplicationVisible()) {
                if (ReplayIO.isRunning()) {
                    ReplayIO.debugLog("App goes to background. Stop!");
                    ReplayIO.stop();
                }
            } else {
                if (!ReplayIO.isRunning()) {
                    ReplayIO.debugLog("App goes to foreground. Run!");
                    ReplayIO.run();
                }
            }
        } catch (ReplayIONotInitializedException e) {
            ReplayIO.errorLog(e.getMessage());
        }
    }

    /**
     * Set the distinct ID to the value passed in.
     *
     * @param distinctId New ID.
     */
    public static void identify(String distinctId) {
        checkInitialized();
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(ReplayConfig.PREF_DISTINCT_ID, distinctId);
        editor.commit();

        replayAPIManager = new ReplayAPIManager(replayApiKey, getOrGenerateClientUUID(),
                ReplaySessionManager.sessionUUID(mContext), distinctId);
    }

    /**
     * Clear the saved distinct ID.
     */
    public static void identify() {
        checkInitialized();
        if (mPrefs.contains(ReplayConfig.PREF_DISTINCT_ID)) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.remove(ReplayConfig.PREF_DISTINCT_ID);
            editor.commit();
        }
    }

    /**
     * Get the distinct ID.
     *
     * @return The distinct ID.
     */
    private static String getDistinctId() {
        checkInitialized();
        return mPrefs.getString(ReplayConfig.PREF_DISTINCT_ID, "");
    }

}
