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
	
	private ReplayIO(Context context) {
		mContext = context;
	}
	
	public static ReplayIO init(Context context, String apiKey) {
		if (mInstance == null) {
			mInstance = new ReplayIO(context);
		}
		mInstance.enabled = true;
		mInstance.replayAPIManager = new ReplayAPIManager(apiKey, getClientUUID(context), ReplaySessionManager.sessionUUID(context));
		mInstance.replayQueue = ReplayRequestQueue.newReplayRequestQueue(context, null);
		return mInstance;
	}
	
	public void trackWithAPIKey(String apiKey) {
		this.apiKey = apiKey;
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
	
	public void setDisaptchInterval(int interval) {
		replayQueue.setDispatchInterval(interval);
	}
	
	public void dispatch() {
		if (!enabled) return;
		replayQueue.dispatchNow();
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
	
	public boolean isDebugMode() {
		return debugMode;
	}
	
	public void applicationDidEnterBackground() {
		ReplaySessionManager.endSession(mContext);
	}
	
	public void applicationWillEnterForeground() {
		replayAPIManager.updateSessionUUID(ReplaySessionManager.sessionUUID(mContext));
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
