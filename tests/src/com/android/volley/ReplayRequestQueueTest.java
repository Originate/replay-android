package com.android.volley;

import io.replay.framework.ReplayAPIManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashSet;

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
		
		// add 101 request, so it will goes into two files
		queue = ReplayRequestQueue.newReplayRequestQueue(getContext(), null);
		queue.stop(); // stop dispatcher actually
		
		for (int i=0; i < 101; i++) {
			queue.add(newRequest("event"+i));
		}
	}
	
	private Request<?> newRequest(String event) throws JSONException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key", "value");
		return apiManager.requestForEvent(event, map);
	}
	
	public void testPersist() throws JSONException, IOException, InterruptedException {
		queue.persist(getContext());
		
		File cacheDir = new File(getContext().getCacheDir(), "persist");
    	File[] files = cacheDir.listFiles();
    	assertEquals(2, files.length);
    	
    	int count = 0;
    	for (File file : files) {
	    	BufferedReader br = new BufferedReader(new FileReader(file));
	    	for (String line; (line = br.readLine()) != null; file.delete() ) {
	    		JSONObject json = new JSONObject(line);
	    		JSONObject data = json.getJSONObject("data");
	    		
	    		assertEquals("event"+count, data.getString("event"));
	    		assertEquals("value", data.getString("key"));
	    		count ++;
	    	}
	    	br.close();
    	}
    	assertEquals(101, count);
	}
	
	public void testLoad() throws IOException, JSONException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException, AuthFailureError {
		queue.persist(getContext());
		

		Field currentRequests = ReplayRequestQueue.class.getDeclaredField("mCurrentRequests");
		currentRequests.setAccessible(true);
		LinkedHashSet<Request<?>> requests = (LinkedHashSet<Request<?>>) currentRequests.get(queue);
		assertEquals(101, requests.size());
		requests.clear();
		assertEquals(0, requests.size());
		
		queue.load(getContext());

		assertEquals(101, requests.size());
		
		int count = 0;
		for (Request<?> request : requests) {
			JSONObject json = new JSONObject(new String(request.getBody()));
			JSONObject data = json.getJSONObject("data");
			
			assertEquals("event"+count, data.getString("event"));
    		assertEquals("value", data.getString("key"));
    		count ++;
		}
		
	}
}
