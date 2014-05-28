package com.android.volley;

import io.replay.framework.ReplayAPIManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

public class ReplayRequestQueueTest extends AndroidTestCase {

	private ReplayAPIManager apiManager;
	private ReplayRequestQueue queue;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		apiManager = new ReplayAPIManager("api_key", "client_uuid", "session_uuid");
	}
	
	private Request<?> newRequest(String event) throws JSONException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key", "value");
		return apiManager.requestForEvent(event, map);
	}
	
	public void testPersist() throws JSONException, IOException {
		queue = ReplayRequestQueue.newReplayRequestQueue(getContext(), null);
		for (int i=0; i < 101; i++) {
			queue.add(newRequest("event"+i));
		}
		
		queue.persist(getContext());
		
		File cacheDir = new File(getContext().getCacheDir(), "persist");
    	File[] files = cacheDir.listFiles();
    	assertEquals(files.length, 2);
    	
    	int count = 0;
    	for (File file : files) {
	    	BufferedReader br = new BufferedReader(new FileReader(file));
	    	for (String line; (line = br.readLine()) != null; file.delete() ) {
	    		JSONObject json = new JSONObject(line);
	    		
	    		JSONObject data = json.getJSONObject("data");
	    		
	    		assertEquals(data.getString("event"), "event"+count);
	    		assertEquals(data.getString("key"), "value");
	    		count ++;
	    	}
	    	br.close();
    	}
    	assertEquals(count, 101);
	}
}
