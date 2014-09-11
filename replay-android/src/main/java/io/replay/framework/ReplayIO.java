package io.replay.framework;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.os.SystemClock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import io.replay.framework.error.ReplayIONoKeyException;
import io.replay.framework.error.ReplayIONotInitializedException;
import io.replay.framework.model.ReplayWatchdogService;
import io.replay.framework.model.ReplayRequestFactory;
import io.replay.framework.queue.QueueLayer;
import io.replay.framework.queue.ReplayQueue;
import io.replay.framework.util.Config;
import io.replay.framework.util.ReplayLogger;
import io.replay.framework.util.ReplayParams;
import io.replay.framework.util.ReplayPrefs;
import io.replay.framework.util.Util;

public class ReplayIO {

    private static AlarmManager am;
    private static PendingIntent orphanFinder;

    private static String MODEL_KEY="device_model";
    private static String MANUFACTURER_KEY="device_manufacturer";
    private static String OS_KEY="client_os";
    private static String SDK_KEY="client_sdk";
    private static String DISPLAY_KEY="display";
    private static String TIME_KEY="timestamp";
    private static String MOBILE_KEY="mobile";
    private static String WIFI_KEY="wifi";
    private static String BLUETOOTH_KEY="bluetooth";
    private static String CARRIER_KEY="carrier";

    private static boolean debugMode;
    private static boolean enabled;
    private static Context mContext;
    private static boolean initialized;
    private static Config mConfig;

    private static int started;
    private static int resumed;
    private static int paused;
    private static int stopped;
    private static ReplayPrefs mPrefs;
    private static ReplayQueue replayQueue;
    private static QueueLayer queueLayer;


    /**
     * Initializes the ReplayIO client. Loads the configuration parameters <code>/res/values/replay_io.xml</code>,
     * including the Replay API key, which is required to be present in order to communicate with the server.
     *
     * It is acceptable to call this class from the main UI thread.
     *
     * @param context The application context.  Use application context instead of activity context
     *                to avoid the risk of memory leak.
     */
    public static void init(Context context){
        if(initialized) return;

        mConfig = ReplayParams.getOptions(context.getApplicationContext());
        init(context, mConfig);
    }

    /**
     * Initializes the ReplayIO client. Loads the configuration parameters <code>/res/values/replay_io.xml</code>,
     * including the Replay API key, which is required to be present in order to communicate with the server.
     *
     * It is acceptable to call this class from the main UI thread.
     *
     * @param context The application context.  Use application context instead of activity context
     *                to avoid the risk of memory leak.
     * @param apiKey  the Replay API key
     */
    public static void init(Context context, String apiKey){
        if(initialized) return;

        mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey(apiKey);
        init(context, mConfig);
    }

    /**
     * Initializes the ReplayIO client. Loads the configuration parameters from the provided <code>options</code>
     * object, including the Replay API key, which is required to be present in order to communicate with the server.
     *
     * It is acceptable to call this class from the main UI thread.
     *
     * @param context The application context.  Use application context instead of activity context
     *                to avoid the risk of memory leak.
     * @param options a full Config object that contains initialization parameters.
     */
    public static void init(Context context, Config options) throws ReplayIONoKeyException {
        String detailMessage = "ReplayIO - %s should not be %s.";

        if(context == null){
            throw new IllegalArgumentException(String.format(detailMessage, "context", "null"));
        }

        if(Util.isNullOrEmpty(options.getApiKey())){
            throw new IllegalArgumentException(String.format(detailMessage, "API key", "null or empty"));
        }

        mContext = context.getApplicationContext();
        Context appContext = context.getApplicationContext(); //cache locally for performance reasons

        // load the default settings
        enabled = mConfig.isEnabled();
        debugMode = mConfig.isDebug();

        mPrefs = ReplayPrefs.get(appContext);

        mPrefs.setClientID(getClientUUID());
        mPrefs.setDistinctID("");

        // initialize ReplayAPIManager
        ReplayRequestFactory.init(appContext);

        // initialize ReplayQueue
        replayQueue = new ReplayQueue(context, mConfig);
        queueLayer = new QueueLayer(replayQueue);
        replayQueue.start();

        initialized = true;
    }

    /**
     * Create a dictionary of network properties .
     *
     */
    public static Map<String,String> getNetworkData() {

        Map<String,String> network = new HashMap<String, String>();

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean usingWifi = networkInfo.isConnected();
        network.put(WIFI_KEY,String.valueOf(usingWifi));

        networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
        boolean usingBlueTooth = networkInfo.isConnected();
        network.put(BLUETOOTH_KEY,String.valueOf(usingBlueTooth));

        networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean usingCellular = networkInfo.isConnected();
        network.put(MOBILE_KEY,String.valueOf(usingCellular));

        final TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String carrier = telephonyManager.getNetworkOperatorName();
        network.put(CARRIER_KEY,carrier);

        return network;
    }



    /**
     * Send additional properties that are automatically tracked .
     *
     * @param data      {@link java.util.Map} object stores key-value pairs.
     */
    public static Map<String,String> addPassiveData(Map<String,String> data){
        try {
            Date currentTime = new Date();
            DateFormat ausFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            ausFormat.setTimeZone(TimeZone.getTimeZone("Universal"));
            data.put(TIME_KEY,ausFormat.format(currentTime));

            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            Criteria crit = new Criteria();
            crit.setPowerRequirement(Criteria.POWER_LOW);
            crit.setAccuracy(Criteria.ACCURACY_FINE); //used to be Criteria.ACCURACY_COARSE
            String provider = locationManager.getBestProvider(crit, true);

            if (provider != null) {
                android.location.Location location;
                try {
                    location = locationManager.getLastKnownLocation(provider); // this is a cached location
                } catch (SecurityException ex) {
                    //The application may not have permission to access location
                    location = null;
                }
                if (location != null) {
                    data.put("latitude", String.valueOf(location.getLatitude()));
                    data.put("longitude", String.valueOf(location.getLongitude()));
                }
            }

            data.put(OS_KEY,Build.VERSION.RELEASE);

            data.put(SDK_KEY,Build.VERSION.SDK);

            WindowManager window = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            data.put(DISPLAY_KEY,display.getName());

            data.put(MANUFACTURER_KEY, Build.MANUFACTURER);

            data.put(MODEL_KEY, Build.MODEL);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Send event with data to server.
     *
     * @param eventName Name of the event.
     * @param data      {@link java.util.Map} object stores key-value pairs.
     */
    public static void trackEvent(String eventName, final Map<String, String> data) {
        checkInitialized();
        if (!enabled) return;
        queueLayer.createAndEnqueue(eventName, addPassiveData(data), getNetworkData());
    }

    /**
     * Update alias. Send a alias request to server side.
     *
     * @param userAlias New alias.
     */
    public static void updateAlias(String userAlias) {
        checkInitialized();
        if (!enabled) return;
        //TODO add passive data here
        queueLayer.createAndEnqueue(userAlias);
    }

    /**
     * Dispatch immediately.  When triggered, all requests in queue will be sent.
     */
    public static void dispatch() {
        checkInitialized();
        if (!enabled) return;
        queueLayer.sendFlush();
    }

    /**
     * Enable ReplayIO.  Allow sending requests.
     */
    public static void enable() {
        checkInitialized();
        enabled = true;
        mConfig.setEnabled(true);
    }

    /**
     * Disable ReplayIO.  Disallow sending requests.
     */
    public static void disable() {
        checkInitialized();
        enabled = false;
        mConfig.setEnabled(false);
    }

    /**
     * Tell if ReplayIO is enabled.
     *
     * @return True if is enabled, false otherwise.
     */
    public static boolean isEnabled() {
        checkInitialized();
        return mConfig.isEnabled();
    }

    /**
     * Set debug mode.  When debug mode is on, logs will be printed.
     *
     * @param debug Boolean value to set to.
     */
    public static void setDebugMode(boolean debug) {
        checkInitialized();
        debugMode = debug;

        mConfig.setDebug(debug);
    }

    /**
     * Tell if debug mode is enabled.
     *
     * @return True if enabled, false otherwise.
     */
    public static boolean isDebugMode() {
        return mConfig.isDebug();
    }

    /**
     * Called when the app entered background.  {@link io.replay.framework.queue.ReplayQueue} will stop running,
     * requests in queue will be saved to disk. Session will be ended, too.
     *.
     * @see io.replay.framework.ReplayApplication
     */
    public static void stop() {
        checkInitialized();
        replayQueue.stop();

        ReplaySessionManager.endSession(mContext);
    }

    /**
     * Called when the app entered foreground.  {@link io.replay.framework.queue.ReplayQueue} will be restarted.
     * A new session is started. If there are persisted requests, load them into queue.
     *
     * @see io.replay.framework.ReplayApplication
     */
    public static void run() {
        checkInitialized();
        if(replayQueue == null){
            replayQueue = new ReplayQueue(mContext, mConfig);
        }else {
            replayQueue.start();
        mPrefs.setSessionID(ReplaySessionManager.sessionUUID(mContext));
        }

        mPrefs.setSessionID(ReplaySessionManager.sessionUUID(mContext));
    }

    /**
     * Stop if ReplayIO is not initialized.
     *
     * @throws io.replay.framework.error.ReplayIONotInitializedException when called before {@link #init(android.content.Context, io.replay.framework.util.Config)}.
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
        if (null == mConfig || !initialized) {
            throw new ReplayIONotInitializedException();
        }
        if (Util.isNullOrEmpty(mPrefs.getClientID())) {
            mPrefs.setClientID(UUID.randomUUID().toString());
            ReplayIO.debugLog("Generated new client UUID");
        }
        return mPrefs.getClientID();
    }

    /**
     * Print debug log if debug mode is on.
     *
     * @param log The debug log to be printed.
     */
    public static void debugLog(String log) {
        if (debugMode) {
            ReplayLogger.d("REPLAY_IO", log);
        }
    }

    /**
     * Print error log if debug mode is on.
     *
     * @param log The error log to be printed.
     */
    public static void errorLog(String log) {
        if (debugMode) {
            ReplayLogger.e("REPLAY_IO", log);
        }
    }

    /**
     * Call this in your Activity's onStart() method, to track the status of your app.
     */
    public static void activityStart() {
        am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        orphanFinder = PendingIntent.getService(mContext, 0,
                ReplayWatchdogService.createIntent(mContext,mConfig.getApiKey()), 0);
        am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 43200000L, orphanFinder); //12 hours

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
        am.cancel(orphanFinder);

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
       /* try {
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
        }*/
        /*TODO this shouldn't be using a integers to keep track of app state - however,
          it's out of scope for this particular branch. */
        //TODO this is duplicated in ReplayLifecycleHandler
    }

    /**
     * Set the distinct ID to the value passed in.
     *
     * @param distinctId New ID.
     */
    public static void identify(String distinctId) {
        checkInitialized();
        mPrefs.setDistinctID(distinctId);
    }

    /**
     * Clear the saved distinct ID. Convenience method for calling <code>ReplayIO.identify("");</code>.
     */
    public static void identify() {
        identify("");
    }

    /**
     * Get the distinct ID.
     *
     * @return The distinct ID.
     */
    private static String getDistinctId() {
        checkInitialized();
        return mPrefs.getDistinctID();
    }

    public static Config getConfig(){
        return mConfig;
    }

}
