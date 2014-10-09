package io.replay.framework;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.SystemClock;

import java.util.Map;
import java.util.UUID;

public final class ReplayIO {

    private static AlarmManager alarmManager;
    private static PendingIntent watchdogIntent;
    private static boolean watchdogEnabled = false;

    private static boolean enabled;
    private static Context mContext;
    private static boolean initialized;
    private static Config mConfig;

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

        Config options = ReplayParams.getOptions(context.getApplicationContext());
        init(context, options);
    }

    /**
     * Initializes the ReplayIO client. Loads the configuration parameters <code>/res/values/replay_io.xml</code>,
     * including the Replay API key, which is required to be present in order to communicate with the server.
     *
     * It is acceptable to call this class from the main UI thread.
     *
     * @param context The application context.  We use application context instead of activity context
     *                to avoid the risk of memory leak.
     * @param apiKey  the Replay API key
     */
    public static void init(Context context, String apiKey){
        if(initialized) return;

        Config options = ReplayParams.getOptions(context.getApplicationContext());
        options.setApiKey(apiKey);
        init(context, options);
    }

    /**
     * Initializes the ReplayIO client. Loads the configuration parameters from the provided <code>options</code>
     * object, including the Replay API key, which is required to be present in order to communicate with the server.
     *
     * It is acceptable to call this class from the main UI thread.
     *
     * @param context The application context.  We use application context instead of activity context
     *                to avoid the risk of memory leak.
     * @param options a full Config object that contains initialization parameters.
     */
    @SuppressLint("NewApi")
    public static void init(Context context, Config options) throws ReplayIONoKeyException {
        if (initialized) return;

        String detailMessage = "ReplayIO - %s should not be %s.";

        if(context == null){
            throw new IllegalArgumentException(String.format(detailMessage, "context", "null"));
        }

        if(Util.isNullOrEmpty(options.getApiKey())){
            throw new IllegalArgumentException(String.format(detailMessage, "API key", "null or empty"));
        }

        mContext = context.getApplicationContext();
        final Context appContext = context.getApplicationContext(); //cache locally for performance reasons

        // load the default settings
        mConfig = options;
        enabled = mConfig.isEnabled();
        mPrefs = ReplayPrefs.get(appContext);
        mPrefs.setClientID(getOrGenerateClientUUID());
        mPrefs.setDistinctID("");

        //create new SessionID
        ReplaySessionManager.getOrCreateSessionUUID(appContext);

        // initialize ReplayQueue
        replayQueue = new ReplayQueue(context, mConfig);
        queueLayer = new QueueLayer(replayQueue, appContext);

        //hook into lifecycle if we're >=ICS and if we don't already have hooks
        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH ) {
            //determine if ReplayActivity is being used
            boolean subclassExists = false;
            try { //reflection, but in the average case this code will run in <35ms
                PackageInfo packageInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), PackageManager.GET_ACTIVITIES);
                for (ActivityInfo info : packageInfo.activities) {
                    Class<?> clazz =  Class.forName(info.name);
                    if(clazz == null) continue;
                    if (ReplayActivity.class.isAssignableFrom(clazz)) { //ReplayActivity is the superclass
                        subclassExists = true;
                        break;
                    }
                }
            }
            catch (ClassCastException     e)  { e.printStackTrace(); }
            catch (ClassNotFoundException e)  { e.printStackTrace(); }
            catch (NameNotFoundException  e)  { e.printStackTrace(); }
            catch (NullPointerException   e)  { e.printStackTrace(); }

            if (!subclassExists) {
                ((Application) appContext).registerActivityLifecycleCallbacks(new ReplayLifecycleHandler());
                ReplayLogger.d("ReplayIO", "added ActivityLifecycleCallbacks");
            }
        }

        initialized = true;
    }

    /**
     * Send event to the server.
     *
     * @param eventName Name of the event
     */
    public static void track(String eventName){
        track(eventName, (Object[]) null);
    }

    /**
     * Send event with data to server.
     *
     * @param eventName Name of the event.
     * @param data      Extra information to be tracked in the main JSON object.
     */
    public static void track(String eventName, Object...data) {
        checkInitialized();
        if (!enabled) return;
        if (!Util.isNullOrEmpty(eventName)) {
            queueLayer.enqueueEvent(eventName, data);
        } else throw new IllegalArgumentException("EventName should not be null or empty");
    }

    /**
     * Send event with data to server.
     *
     * @param eventName Name of the event.
     * @param data      Extra information to be tracked in the main JSON object.
     */
    public static void track(String eventName, Map<String,?> data) {
        checkInitialized();
        if (!enabled) return;
        if (!Util.isNullOrEmpty(eventName)) {
            queueLayer.enqueueEvent(eventName, data);
        } else throw new IllegalArgumentException("EventName should not be null or empty");
    }

    /**
     * Update traits. Send a traits request to server side.
     *
     * @param data      Extra information to be tracked in the main JSON object.
     */
    public static void updateTraits(Object... data ) {
        checkInitialized();
        if (!enabled) return;
        if (data != null && data.length != 0) {
            queueLayer.enqueueTrait(data);
        } else throw new IllegalArgumentException("Error: traits data cannot be null or empty.");
    }

    /**
     * Update traits. Send a traits request to server side.
     *
     * @param data      Extra information to be tracked in the main JSON object.
     */
    public static void updateTraits(Map<String,?> data) {
        checkInitialized();
        if (!enabled) return;
        if (data != null && !data.isEmpty()) {
            queueLayer.enqueueTrait(data);
        } else throw new IllegalArgumentException("Error: traits data cannot be null or empty.");
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
     * Tell if debug mode is enabled.
     *
     * @return True if enabled, false otherwise.
     */
    public static boolean isDebugMode() {
        return mConfig.isDebug();
    }

    /**
     * Set debug mode.  When debug mode is on, logs will be printed.
     *
     * @param debug Boolean value to set to.
     */
    public static void setDebugMode(boolean debug) {
        checkInitialized();

        mConfig.setDebug(debug);
    }

    /**
     * Called when the app entered background.  {@link ReplayQueue} will stop running,
     * , and the Session will be ended, too.
     *.
     */
    public static void stop() {
        checkInitialized();
        replayQueue.stop();

        ReplaySessionManager.endSession(mContext);
    }

    /**
     * Called when the app entered foreground.  {@link io.replay.framework.ReplayQueue} will be restarted.
     * A new session is started. If there are persisted requests, load them into queue.
     *
     */
    public static void start() {
        checkInitialized();
        if(replayQueue == null){
            replayQueue = new ReplayQueue(mContext, mConfig);
        }else {
            replayQueue.start();
        }

        mPrefs.setSessionID(ReplaySessionManager.getOrCreateSessionUUID(mContext));
    }

    /**
     * Stop if ReplayIO is not initialized.
     *
     * @throws io.replay.framework.ReplayIONotInitializedException when called before {@link #init(android.content.Context, io.replay.framework.Config)}.
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
    public static String getOrGenerateClientUUID() {
        if (null == mConfig) {
            throw new ReplayIONotInitializedException();
        }
        if (Util.isNullOrEmpty(mPrefs.getClientID())) {
            mPrefs.setClientID(UUID.randomUUID().toString());
            ReplayLogger.d("Generated new client UUID");
        }
        return mPrefs.getClientID();
    }

    /** Initializes the Replay.io client, kicks off all worker threads,
     *  and notifies the client that this activity has been created.
     *
     * @param context an instance of the Activity
     */
    public static void onActivityCreate(Context context) {
        ReplayIO.init(context);
        initWatchdog();
    }

    /** Initializes the Replay.io client, kicks off all worker threads,
     *  and notifies the client that this activity has been created.
     *
     * @param context an instance of the Activity
     * @param apiKey the Replay API key
     */
    public static void onActivityCreate(Context context, String apiKey) {
        ReplayIO.init(context, apiKey);
        initWatchdog();
    }

    /** Initializes the Replay.io client, kicks off all worker threads,
     *  and notifies the client that this activity has been created.
     *
     * @param context an instance of the Activity
     * @param options a full Config object that contains initialization parameters.
     */

    public static void onActivityCreate(Context context, Config options) {
        ReplayIO.init(context, options);
        initWatchdog();
    }

    /** Initializes the Replay.io client, kicks off all worker threads,
     *  and notifies the client that this activity has been created.
     *
     * @param context an instance of the Activity
     */
    public static void onActivityStart(Context context) {
        ReplayIO.init(context);
        initWatchdog();
    }

    /** Initializes the Replay.io client, kicks off all worker threads,
     *  and notifies the client that this activity has been created.
     *
     * @param context an instance of the Activity
     * @param apiKey the Replay API key
     */
    public static void onActivityStart(Context context, String apiKey) {
        ReplayIO.init(context, apiKey);
        initWatchdog();
    }

    /**Initializes the Replay.io client, kicks off all worker threads,
     *  and notifies the client that this activity has been created.
     *
     * @param context an instance of the Activity
     * @param options a full Config object that contains initialization parameters.
     */
    public static void onActivityStart(Context context, Config options){
        ReplayIO.init(context, options);
        initWatchdog();
    }

    /**
     * Called when the activity has been resumed.
     *
     * @param context the activity
     */
    public static void onActivityResume(Context context) {
        ReplayIO.init(context);
    }

    /**
     * Called when the activity is paused.
     *
     * @param context the activity
     */
    public static void onActivityPause(Context context) {
        flushQueue(context.getApplicationContext());
    }

    /**
     * Called when the activity has been stopped.
     *
     * @param context
     */
    public static void onActivityStop(Context context) {
        flushQueue(context.getApplicationContext());
        if(watchdogEnabled){
            alarmManager.cancel(watchdogIntent);
            watchdogEnabled ^= true;
        }
    }

    private static void initWatchdog() {
        if(!watchdogEnabled){
            if(alarmManager == null){
                alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            }
            watchdogIntent = PendingIntent.getService(mContext, 0,
                   ReplayWatchdogService.createIntent(mContext, mConfig.getApiKey()), PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 43200000L, watchdogIntent); //12 hours
            watchdogEnabled ^= true;
        }
    }

    private static void flushQueue(Context context) {
        if (queueLayer == null) {
            if (mConfig == null) {
                mConfig = ReplayParams.getOptions(context);
            }
            if (replayQueue == null) {
                replayQueue = new ReplayQueue(context, mConfig);
            }
            queueLayer = new QueueLayer(replayQueue, context);
        }
        queueLayer.sendFlush();
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

    public static int count(){
        return replayQueue.count();
    }

    public static Config getConfig(){
        return mConfig;
    }
}
