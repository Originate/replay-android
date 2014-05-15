package io.replay.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.UUID;

import android.content.Context;

import com.android.volley.ReplayRequestQueue;
import com.android.volley.Request;

public class ReplayIO {

	private boolean debugMode;
	private String apiKey;
	private static String clientUUID;
	private boolean enabled;
	private ReplayAPIManager replayAPIManager;
	private ReplayRequestQueue replayQueue;
	private static ReplayIO mInstance;
	private Context mContext;
	private boolean initialized;
	
	private ReplayIO(Context context) {
		mContext = context;
		initialized = false;
	}
	
	public static ReplayIO init(Context context, String apiKey) {
		if (mInstance == null) {
			mInstance = new ReplayIO(context);
		}
		mInstance.enabled = true;
		mInstance.replayAPIManager = new ReplayAPIManager(apiKey, getClientUUID(context), ReplaySessionManager.sessionUUID(context));
		mInstance.replayQueue = ReplayRequestQueue.newReplayRequestQueue(context, null);
		mInstance.initialized = true;
		return mInstance;
	}
	
	public static ReplayIO getInstance(Context context, String apiKey) {
		if (mInstance != null && mInstance.initialized) {
			return mInstance;
		} else {
			return init(context, apiKey);
		}
	}
	
	public static void trackWithAPIKey(String apiKey) {
		mInstance.apiKey = apiKey;
	}
	
	public void trackEvent(String eventName, Map<String, String> data) {
		if (!enabled) return;
		Request<?> request = replayAPIManager.requestForEvent(eventName, data);
		replayQueue.add(request);
	}
	
	public void updateAlias(String userAlias) {
		if (!enabled) return;
		Request<?> request = replayAPIManager.requestForAlias(userAlias);
		replayQueue.add(request);
	}
	
	public static void setDispatchInterval(int interval) {
		mInstance.replayQueue.setDispatchInterval(interval);
	}
	
	public static int getDispatchInterval() {
		return mInstance.replayQueue.getDispatchInterval();
	}
	
	public static void dispatch() {
		if (!mInstance.enabled) return;
		mInstance.replayQueue.dispatchNow();
	}
	
	public static void enable() {
		mInstance.enabled = true;
	}
	
	public static void disable() {
		mInstance.enabled = false;
	}
	
	public static boolean isEnabled() {
		return mInstance.enabled;
	}
	
	public static void setDebugMode(boolean debugMode) {
		mInstance.debugMode = debugMode;
	}
	
	public static boolean isDebugMode() {
		return mInstance.debugMode;
	}
	
	/** when the App enter background */
	public static void stop() {
		mInstance.replayQueue.stop();
		ReplaySessionManager.endSession(mInstance.mContext);
	}
	
	/** when the App entered foreground */
	public static void run() {
		mInstance.replayQueue.start();
		mInstance.replayAPIManager.updateSessionUUID(ReplaySessionManager.sessionUUID(mInstance.mContext));
	}
	
	/** tell if the replayio is running, ie. ReplayRequestQueue is running */
	public static boolean isRunning() {
		return mInstance.replayQueue.isRunning();
	}
	
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

	private static String readClientIDFile(File clientIDFile) throws IOException {
		RandomAccessFile f = new RandomAccessFile(clientIDFile, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}
	
	private static void writeClientIDFile(File clientIDFile) throws IOException {
		FileOutputStream out = new FileOutputStream(clientIDFile);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes());
		out.close();
	}
}
