package io.replay.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
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
	
	private ReplayIO(Context context) {
		mContext = context;
		initialized = false;
	}
	
	public static ReplayIO init(Context context, String apiKey) {
		if (mInstance == null) {
			mInstance = new ReplayIO(context);
		}
		enabled = true;
		replayAPIManager = new ReplayAPIManager(apiKey, getClientUUID(context), ReplaySessionManager.sessionUUID(context));
		replayQueue = ReplayRequestQueue.newReplayRequestQueue(context, null);
		initialized = true;
		return mInstance;
	}
	
	/**
	 * Update API key 
	 * @param key
	 */
	public static void trackWithAPIKey(String key) {
		apiKey = key;
	}
	
	/**
	 * Send event with data to server 
	 * @param eventName
	 * @param data
	 */
	public static void trackEvent(String eventName, final Map<String, String> data) {
		checkInitialized();
		if (!enabled) return;
		Request<?> request = replayAPIManager.requestForEvent(eventName, data);
		replayQueue.add(request);
	}
	
	/**
	 * Update alias
	 * @param userAlias
	 */
	public static void updateAlias(String userAlias) {
		checkInitialized();
		if (!enabled) return;
		Request<?> request = replayAPIManager.requestForAlias(userAlias);
		replayQueue.add(request);
	}
	
	/**
	 * Set interval of dispatches
	 * @param interval
	 */
	public static void setDispatchInterval(int interval) {
		replayQueue.setDispatchInterval(interval);
	}
	
	/**
	 * Get interval of dispatches
	 * @return 
	 */
	public static int getDispatchInterval() {
		return replayQueue.getDispatchInterval();
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
		enabled = true;
	}
	
	/**
	 * Disable ReplayIO
	 */
	public static void disable() {
		enabled = false;
	}
	
	/**
	 * true if ReplayIO is enabled
	 * @return 
	 */
	public static boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Set debug mode
	 * @param debugMode
	 */
	public static void setDebugMode(boolean debug) {
		debugMode = debug;
	}
	
	/**
	 * true if debug mode is enabled
	 * @return
	 */
	public static boolean isDebugMode() {
		return debugMode;
	}
	
	/** 
	 * Call from ReplayApplication when the app entered background 
	 */
	public static void stop() {
		checkInitialized();
		replayQueue.stop();
		ReplaySessionManager.endSession(mContext);
	}
	
	/**
	 * Call from ReplayApplication when the app entered foreground
	 */
	public static void run() {
		checkInitialized();
		replayQueue.start();
		replayAPIManager.updateSessionUUID(ReplaySessionManager.sessionUUID(mContext));
	}
	
	/**
	 * tell if the replayio is running, ie. ReplayRequestQueue is running
	 * @return 
	 */
	public static boolean isRunning() {
		return replayQueue.isRunning();
	}
	
	private static void checkInitialized() {
		if (!initialized) {
			Log.e("REPLAY_IO", "ReplayIO not initialized");
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
			File clientIDFile = new File(context.getFilesDir(),ReplayConfig.KEY_CLIENT_ID);

			try {
				if (!clientIDFile.exists()) {
					writeClientIDFile(clientIDFile);
				}
				clientUUID = readClientIDFile(clientIDFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
				
		}
		return clientUUID;
	}

	/**
	 * Read client UUID from file
	 * @param clientIDFile
	 * @return
	 * @throws IOException
	 */
	private static String readClientIDFile(File clientIDFile) throws IOException {
		RandomAccessFile f = new RandomAccessFile(clientIDFile, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}
	
	/**
	 * Write client UUID into file
	 * @param clientIDFile
	 * @throws IOException
	 */
	private static void writeClientIDFile(File clientIDFile) throws IOException {
		FileOutputStream out = new FileOutputStream(clientIDFile);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes());
		out.close();
	}
}
