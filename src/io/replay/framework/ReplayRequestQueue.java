package io.replay.framework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class ReplayRequestQueue {

	private static final String PERSIST_DIR = "persist";
	private volatile boolean isRunning;
	
	private final Set<ReplayRequest> mCurrentRequests = new LinkedHashSet<ReplayRequest>();
	
	private ReplayRequestDispatcher mDispatcher;
	
	private final LinkedBlockingQueue<ReplayRequest> mWaittingQueue = new LinkedBlockingQueue<ReplayRequest>();
	
	private ReplayAPIManager mManager;
	
	public ReplayRequestQueue(ReplayAPIManager manager) {
		mManager = manager;
		mDispatcher = new ReplayRequestDispatcher(mWaittingQueue, mManager, 
				new ReplayRequestDelivery(new Handler(Looper.getMainLooper())));
	}
	
	public void start() {
		stop();
		
		mDispatcher = new ReplayRequestDispatcher(mWaittingQueue, mManager, 
				new ReplayRequestDelivery(new Handler(Looper.getMainLooper())));
		mDispatcher.start();
		
		isRunning = true;
	}
	
	public void stop() {
		if (mDispatcher != null) {
			mDispatcher.quit();
		}
		
		isRunning = false;
	}
	
	public ReplayRequest add(ReplayRequest request) {
		request.setRequestQueue(this);
		
		synchronized (mCurrentRequests) {
			mCurrentRequests.add(request);
		}
		
		mWaittingQueue.add(request);
		
		return request;
	}
	
	public void finish(ReplayRequest request) {
		synchronized (mCurrentRequests) {
			mCurrentRequests.remove(request);
		}
	}
	
	public void setDispatchInterval(int interval) {
		mDispatcher.setDispatchInterval(interval);
	}
	
	public void dispatchNow() {
		mDispatcher.dipatchNow();
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
    /**
     * Store the events in queue to disk
     * @throws IOException 
     */
	public void persist(Context context) throws IOException {
    	File cacheDir = new File(context.getCacheDir(), PERSIST_DIR);
    	if (!cacheDir.exists()) {
    		if (!cacheDir.mkdirs()) {
    			ReplayIO.debugLog("Unable to create persist dir "+cacheDir.getAbsolutePath());
    		}
    	}
    	
    	synchronized(mCurrentRequests) {
	    	int count = 0;
	    	int totalCount = 0;
	    	int fileCount = 0;
	    	BufferedWriter bw = new BufferedWriter(new FileWriter(new File(cacheDir, "requests"+fileCount)));
	    	for (ReplayRequest request : mCurrentRequests ) {
	    		byte[] body = request.getBody();
	    		bw.write(new String(body));
	    		bw.newLine();
	    		
	    		count ++;
	    		totalCount ++;
	    		if (count >= 99) {
	    			count = 0;
	    			fileCount ++;
	    			bw.flush();
	    			bw.close();
	    			bw = new BufferedWriter(new FileWriter(new File(cacheDir, "requests"+fileCount)));
	    		}
	    	}
	    	bw.flush();
	    	bw.close();
	    	ReplayIO.debugLog("Persisted " + totalCount + " requests");
    	}
	}
	
    /**
     * Load the stored events into queue  
     * @throws IOException 
     * @throws JSONException 
     */
    public void load(Context context) throws IOException, JSONException {
    	File cacheDir = new File(context.getCacheDir(), PERSIST_DIR);
    	File[] files = cacheDir.listFiles();
    	if (files == null) {
    		return;
    	}
    	
    	int count = 0;
    	for (File file : files) {
	    	BufferedReader br = new BufferedReader(new FileReader(file));
	    	for (String line; (line = br.readLine()) != null; file.delete() ) {
	    		JSONObject json = new JSONObject(line);
	    		ReplayRequest request = null;
	    		if (json.has(ReplayConfig.KEY_DATA)) {
	    			request = new ReplayRequest(ReplayConfig.REQUEST_TYPE_EVENTS, json);
	    		} else if (json.has(ReplayConfig.REQUEST_TYPE_ALIAS)) {
	    			request = new ReplayRequest(ReplayConfig.REQUEST_TYPE_ALIAS, json);
	    		}
	    		if (request != null) {
	    			add(request);
	    			count ++;
	    		}
	    	}
	    	br.close();
    	}
    	ReplayIO.debugLog("Loaded " + count + " requests");
    }
}
