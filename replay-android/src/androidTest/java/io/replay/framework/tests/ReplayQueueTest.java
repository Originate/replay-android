package io.replay.framework.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import io.replay.framework.network.ReplayNetworkManager;
import io.replay.framework.queue.ReplayQueue;
import io.replay.framework.model.ReplayRequest;

public class ReplayQueueTest extends AndroidTestCase {

	private ReplayNetworkManager apiManager;
	private ReplayQueue queue;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		apiManager = new ReplayNetworkManager("api_key", "client_uuid", "session_uuid", "distinct_id");
		
		// add 101 request, so it will goes into two files
		queue = new ReplayQueue(apiManager);
		queue.setDispatchInterval(-1);

		for (int i=0; i < 101; i++) {
			queue.enqueue(newRequest("event"+i));
		}
	}
	
	private ReplayRequest newRequest(String event) throws JSONException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key", "value");
		return apiManager.requestForEvent(event, map);
	}
	
	public void testPersist() throws JSONException, IOException, InterruptedException {
		queue.saveQueueToDisk(getContext());
		
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
	
	public void testLoad() throws IOException, JSONException, NoSuchFieldException,
            IllegalAccessException, IllegalArgumentException {
		queue.saveQueueToDisk(getContext());

		Field mRequests = ReplayQueue.class.getDeclaredField("mRequests");
		mRequests.setAccessible(true);
		LinkedList<ReplayRequest> requests = (LinkedList<ReplayRequest>) mRequests.get(queue);
		assertEquals(101, requests.size());
		requests.clear();
		assertEquals(0, requests.size());
		
		queue.loadQueueFromDisk(getContext());

		assertEquals(101, requests.size());
		
		int count = 0;
		for (ReplayRequest request : requests) {
			JSONObject json = new JSONObject(new String(request.getBody()));
			JSONObject data = json.getJSONObject("data");
			
			assertEquals("event"+count, data.getString("event"));
    		assertEquals("value", data.getString("key"));
    		count ++;
		}
		
	}

    /**
     * Test setDispatcherInterval(0), this will fail if server side is not on.
     * Slow network connection will cause failure, too.
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws org.json.JSONException
     * @throws InterruptedException
     */
	public void testSetDispatcherIntervalZero() throws NoSuchFieldException, IllegalAccessException, 
			IllegalArgumentException, JSONException, InterruptedException {
		ReplayQueue queue = new ReplayQueue(apiManager);
		queue.start();
		
		Field dispatchIntervalField = ReplayQueue.class.getDeclaredField("dispatchInterval");
		dispatchIntervalField.setAccessible(true);
		
		// interval should be 0 by default
		assertEquals(0, ((Integer) dispatchIntervalField.get(queue)).intValue());
		
		// the queue should be empty at start
		assertEquals(0, getQueueSize(queue));
		
		queue.enqueue(apiManager.requestForEvent("event", null));
		queue.enqueue(apiManager.requestForEvent("event", null));
		queue.enqueue(apiManager.requestForEvent("event", null));
		// the queue should not be empty when request is added
		
		assertEquals(3, getQueueSize(queue));
		
		int waited = 0;
		while (true) {
			Thread.sleep(100);
			waited += 100;
			
			// the queue should be empty immediately
			if (waited >= 2000) {
				assertEquals(0, getQueueSize(queue));
				break;
			}
		}
	}

    /**
     * Test setDispatcherInterval(-1), this will fail if server side is not on.
     * Slow network connection will cause failure, too.
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws org.json.JSONException
     * @throws InterruptedException
     */
	public void testSetDispatcherIntervalMinus() throws NoSuchFieldException, IllegalAccessException, 
			IllegalArgumentException, JSONException, InterruptedException {
		ReplayQueue queue = new ReplayQueue(apiManager);
		queue.start();
		
		Field dispatchIntervalField = ReplayQueue.class.getDeclaredField("dispatchInterval");
		dispatchIntervalField.setAccessible(true);
		
		// interval should be 0 by default
		assertEquals(0, ((Integer) dispatchIntervalField.get(queue)).intValue());
		
		// interval should be what it was set to
		queue.setDispatchInterval(-1);
		assertEquals(-1, ((Integer) dispatchIntervalField.get(queue)).intValue());
		
		// the queue should be empty at start
		assertEquals(0, getQueueSize(queue));
		
		queue.enqueue(apiManager.requestForEvent("event", null));
		queue.enqueue(apiManager.requestForEvent("event", null));
		queue.enqueue(apiManager.requestForEvent("event", null));
		
		// the queue should not be empty when request is added
		assertEquals(3, getQueueSize(queue));
		
		int waited = 0;
		boolean dispatchOnce = false;
		while (true) {
			Thread.sleep(100);
			waited += 100;
			
			// the queue should not be dispatched before manually dispatch
			if (waited < 3000) {
				assertEquals(3, getQueueSize(queue));
			} else {
				if (!dispatchOnce) {
					queue.dispatch();
					dispatchOnce = true;
				}
				
				// the queue should be empty shortly after dispatch
				if (waited >= 5000) {
					assertEquals(0, getQueueSize(queue));
					break;
				}
			}
		}
	}

    /**
     * Test setDispatcherInterval(5), this will fail if server side is not on.
     * Slow network connection will cause failure, too.
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws org.json.JSONException
     * @throws InterruptedException
     */
	public void testSetDispatcherInterval5() throws NoSuchFieldException, IllegalAccessException, 
			IllegalArgumentException, JSONException, InterruptedException {
		ReplayQueue queue = new ReplayQueue(apiManager);
		queue.start();
		
		Field dispatchIntervalField = ReplayQueue.class.getDeclaredField("dispatchInterval");
		dispatchIntervalField.setAccessible(true);
		
		// interval should be 0 by default
		assertEquals(0, ((Integer) dispatchIntervalField.get(queue)).intValue());
		
		// interval should be what it was set to
		queue.setDispatchInterval(5);
		assertEquals(5, ((Integer) dispatchIntervalField.get(queue)).intValue());
		
		// the queue should be empty at start
		assertEquals(0, getQueueSize(queue));
		
		queue.enqueue(apiManager.requestForEvent("event", null));
		queue.enqueue(apiManager.requestForEvent("event", null));
		queue.enqueue(apiManager.requestForEvent("event", null));
		
		// the queue should not be empty when request is added
		assertEquals(3, getQueueSize(queue));
		
		int waited = 0;
		while (true) {
			Thread.sleep(100);
			waited += 100;
			
			// the queue should not be empty when the dispatch interval is not ended
			if (waited < 5000) {
				assertEquals(3, getQueueSize(queue));
			}
			
			// the queue should be empty shortly after dispatch
			if (waited >= 7000) {
				assertEquals(0, getQueueSize(queue));
				break;
			}
		}
	}
		
	/**
	* Get the size of the underlying queue size of ReplayQueue.
	* @return Size of the queue.
	* @throws NoSuchFieldException
	* @throws IllegalAccessException
	* @throws IllegalArgumentException
	*/
	private int getQueueSize(ReplayQueue queue) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		Field mRequests = ReplayQueue.class.getDeclaredField("mRequests");
		mRequests.setAccessible(true);
		LinkedList<ReplayRequest> requests = (LinkedList<ReplayRequest>) mRequests.get(queue);
		synchronized (requests) {
			return requests.size();
		}
	}	
}
