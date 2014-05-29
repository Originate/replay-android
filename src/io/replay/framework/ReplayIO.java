package io.replay.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.ReplayRequestQueue;
import com.android.volley.Request;

public class ReplayIO {

	private static boolean debugMode;
	private static String apiKey;
	private static String clientUUID;
	private static boolean enabled;
	private static ReplayAPIManager replayAPIManager;
	private static ReplayRequestQueue replayQueue;
	private static ReplayIO mInstance;
	private static Context mContext;
	private static boolean initialized;
	private static SharedPreferences mPrefs;
	
	private ReplayIO(Context context) {
		mContext = context;
		initialized = false;

		mPrefs = mContext.getSharedPreferences("ReplayIOPreferences", Context.MODE_PRIVATE);
	}
	
	public static ReplayIO init(Context context, String apiKey) {
		if (mInstance == null) {
			mInstance = new ReplayIO(context);
		}
		
		enabled = isEnabled();
		debugMode = isDebugMode();
		replayAPIManager = new ReplayAPIManager(apiKey, getClientUUID(context), ReplaySessionManager.sessionUUID(context));
		replayQueue = ReplayRequestQueue.newReplayRequestQueue(context, null);
		replayQueue.setDispatchInterval(getDispatchInterval());
		replayQueue.start();
		try {
			replayQueue.load(mContext);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialized = true;
		return mInstance;
	}
	
	/**
	 * Update API key 
	 * @param key
	 */
	public static void trackWithAPIKey(String key) {
		apiKey = key;
		replayAPIManager = new ReplayAPIManager(apiKey, getClientUUID(mContext), ReplaySessionManager.sessionUUID(mContext));
	}
	
	/**
	 * Send event with data to server 
	 * @param eventName
	 * @param data
	 */
	public static void trackEvent(String eventName, final Map<String, String> data) {
		checkInitialized();
		if (!enabled) return;
		Request<?> request;
		try {
			request = replayAPIManager.requestForEvent(eventName, data);
			replayQueue.add(request);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Update alias
	 * @param userAlias
	 */
	public static void updateAlias(String userAlias) {
		checkInitialized();
		if (!enabled) return;
		Request<?> request;
		try {
			request = replayAPIManager.requestForAlias(userAlias);
			replayQueue.add(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set interval for dispatches
	 * @param interval
	 */
	public static void setDispatchInterval(int interval) {
		replayQueue.setDispatchInterval(interval);
		
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putInt(ReplayConfig.PREF_DISPATCH_INTERVAL, interval);
		editor.commit();
	}
	
	/**
	 * Get interval for dispatches
	 * @return 
	 */
	public static int getDispatchInterval() {
		
		return mPrefs.getInt(ReplayConfig.PREF_DISPATCH_INTERVAL, 0);
	}
	
	/**
	 * Dispatch one event in the queue immediately
	 */
	public static void dispatch() {
		checkInitialized();
		if (!enabled) return;
		replayQueue.dispatchNow();
	}
	
	/**
	 * Enable ReplayIO
	 */
	public static void enable() {
		checkInitialized();
		enabled = true;
		
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(ReplayConfig.PREF_ENABLED, true);
		editor.commit();
	}
	
	/**
	 * Disable ReplayIO
	 */
	public static void disable() {
		checkInitialized();
		enabled = false;
		
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(ReplayConfig.PREF_ENABLED, false);
		editor.commit();
	}
	
	/**
	 * true if ReplayIO is enabled
	 * @return 
	 */
	public static boolean isEnabled() {
		checkInitialized();
		enabled = mPrefs.getBoolean(ReplayConfig.PREF_ENABLED, true);
		return enabled;
	}
	
	/**
	 * Set debug mode
	 * @param debugMode
	 */
	public static void setDebugMode(boolean debug) {
		checkInitialized();
		debugMode = debug;
		
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(ReplayConfig.PREF_DEBUG_MODE_ENABLED, debugMode);
		editor.commit();
	}
	
	/**
	 * true if debug mode is enabled
	 * @return
	 */
	public static boolean isDebugMode() {
		checkInitialized();
		debugMode = mPrefs.getBoolean(ReplayConfig.PREF_DEBUG_MODE_ENABLED, false);
		return debugMode;
	}
	
	/** 
	 * Call from ReplayApplication when the app entered background 
	 */
	public static void stop() {
		checkInitialized();
		replayQueue.stop();
		
		try {
			replayQueue.persist(mContext);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ReplaySessionManager.endSession(mContext);
	}
	
	/**
	 * Call from ReplayApplication when the app entered foreground
	 */
	public static void run() {
		checkInitialized();
		replayQueue = ReplayRequestQueue.newReplayRequestQueue(mContext, null);
		//replayQueue.start();
		replayQueue.setDispatchInterval(getDispatchInterval());
		replayAPIManager.updateSessionUUID(ReplaySessionManager.sessionUUID(mContext));
		
		try {
			replayQueue.load(mContext);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * tell if the replayio is running, ie. ReplayRequestQueue is running
	 * @return 
	 */
	public static boolean isRunning() {
		checkInitialized();
		return replayQueue.isRunning();
	}
	
	private static void checkInitialized() {
		if (!initialized) {
			debugLog("ReplayIO not initialized");
			return;
		}
	}
	/**
	 * Get or generate a UUID as a the client UUID
	 * @param context
	 * @return
	 */
	public synchronized static String getClientUUID(Context context) {
		if (clientUUID == null) {
			if(!mPrefs.contains(ReplayConfig.KEY_CLIENT_ID)) {
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
	 * Print debug log if debugMode is on
	 * @param log
	 */
	public static void debugLog(String log) {
		if (debugMode) {
			Log.d("REPLAY_IO", log);
		}
	}
}
