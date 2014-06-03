package com.android.volley;

import io.replay.framework.ReplayAPIManager;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;

import org.json.JSONException;

import android.test.AndroidTestCase;

public class ReplayNetworkDispatcherTest extends AndroidTestCase {

	private ReplayAPIManager apiManager;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		apiManager = new ReplayAPIManager("api_key", "client_uuid", "session_uuid");
	}
	
	public void testSetDispatcherInterval() throws NoSuchFieldException, IllegalAccessException, 
			IllegalArgumentException, JSONException, InterruptedException {
		ReplayRequestQueue queue = ReplayRequestQueue.newReplayRequestQueue(getContext(), null);
		queue.start();
		
		ReplayNetworkDispatcher dispatcher = getDispatcher(queue);
		
		Field dispatchIntervalField = ReplayNetworkDispatcher.class.getDeclaredField("dispatchInterval");
		dispatchIntervalField.setAccessible(true);
		
		// interval should be 0 by default
		assertEquals(0, (int) dispatchIntervalField.get(dispatcher));
		
		// interval should be what it was set to
		queue.setDispatchInterval(5);
		assertEquals(5, (int) dispatchIntervalField.get(dispatcher));
		
		// make sure dispatch not start at beginning
		Field dispatchingField = ReplayNetworkDispatcher.class.getDeclaredField("dispatching");
		dispatchingField.setAccessible(true);
		dispatchingField.setBoolean(dispatcher, false);
		
		// the queue should be empty at start
		assertEquals(0, getQueueSize(queue));
		
		queue.add(apiManager.requestForEvent("event", null));
		// the queue should not be empty when request is added
		
		assertEquals(1, getQueueSize(queue));

		int waited = 0;
		while (true) {
			Thread.sleep(100);
			waited += 100;
			
			// the queue should not be empty when the dispatch interval is not ended
			if (waited < 5000) {
				assertEquals(1, getQueueSize(queue));
			}
			
			// the queue should be empty shortly after dispatch
			if (waited >= 6000) {
				assertEquals(0, getQueueSize(queue));
				break;
			}
		}
	}
	
	public void testDispatchNow() throws NoSuchFieldException, IllegalAccessException, 
			IllegalArgumentException, JSONException, InterruptedException {
		ReplayRequestQueue queue = ReplayRequestQueue.newReplayRequestQueue(getContext(), null);
		queue.start();
		
		// Dispatcher is recreated when start queue
		ReplayNetworkDispatcher dispatcher = getDispatcher(queue);
		
		Field dispatchingField = ReplayNetworkDispatcher.class.getDeclaredField("dispatching");
		dispatchingField.setAccessible(true);
		dispatchingField.setBoolean(dispatcher, false);
		assertEquals(false, (boolean) dispatchingField.get(dispatcher));
		
		queue.setDispatchInterval(5);
		
		// the queue should be empty at start
		assertEquals(0, getQueueSize(queue));
		
		queue.add(apiManager.requestForEvent("event", null));
		queue.add(apiManager.requestForEvent("event", null));
		queue.add(apiManager.requestForEvent("event", null));
		
		// the queue should not be empty when request is added
		assertEquals(3, getQueueSize(queue));
		
		int waited = 0;
		boolean dispatchOnce = false;
		while (true) {
			Thread.sleep(100);
			waited += 100;
			
			// the queue should not be empty when the dispatch interval is not ended
			if (waited < 3000) {
				assertEquals(3, getQueueSize(queue));
			} else {
				if (!dispatchOnce) {
					queue.dispatchNow();
					assertEquals(true, (boolean) dispatchingField.get(dispatcher));
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
	 * Get the size of the underlying queue size of ReplayRequestQueue
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private int getQueueSize(ReplayRequestQueue queue) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		Field currentRequests = ReplayRequestQueue.class.getDeclaredField("mCurrentRequests");
		currentRequests.setAccessible(true);
		LinkedHashSet<Request<?>> requests = (LinkedHashSet<Request<?>>) currentRequests.get(queue);
		synchronized (requests) {
			return requests.size();
		}
	}
	
	/**
	 * Get the dispatcher of the ReplayRequestQueue
	 * @param queue
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private ReplayNetworkDispatcher getDispatcher(ReplayRequestQueue queue) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {

		Field dispatchersField = ReplayRequestQueue.class.getDeclaredField("mDispatchers");
		dispatchersField.setAccessible(true);
		ReplayNetworkDispatcher[] dispatchers = (ReplayNetworkDispatcher[]) dispatchersField.get(queue);
		
		ReplayNetworkDispatcher dispatcher = dispatchers[0];
		return dispatcher;
	}
	
}
