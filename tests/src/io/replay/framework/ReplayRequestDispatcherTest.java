package io.replay.framework;

import io.replay.framework.ReplayAPIManager;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;

import org.json.JSONException;

import android.test.AndroidTestCase;

public class ReplayRequestDispatcherTest extends AndroidTestCase {

	private ReplayAPIManager apiManager;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		apiManager = new ReplayAPIManager("api_key", "client_uuid", "session_uuid");
	}
	
	public void testSetDispatcherIntervalZero() throws NoSuchFieldException, IllegalAccessException, 
			IllegalArgumentException, JSONException, InterruptedException {
		ReplayRequestQueue queue = new ReplayRequestQueue(apiManager);
		queue.start();
		
		ReplayRequestDispatcher dispatcher = getDispatcher(queue);
		
		Field dispatchIntervalField = ReplayRequestDispatcher.class.getDeclaredField("dispatchInterval");
		dispatchIntervalField.setAccessible(true);
		
		// interval should be 0 by default
		assertEquals(0, (int) dispatchIntervalField.get(dispatcher));
		
		// make sure dispatch not start at beginning
		Field dispatchingField = ReplayRequestDispatcher.class.getDeclaredField("dispatching");
		dispatchingField.setAccessible(true);
		dispatchingField.setBoolean(dispatcher, false);
		
		// the queue should be empty at start
		assertEquals(0, getQueueSize(queue));
		
		queue.add(apiManager.requestForEvent("event", null));
		queue.add(apiManager.requestForEvent("event", null));
		queue.add(apiManager.requestForEvent("event", null));
		// the queue should not be empty when request is added
		
		assertEquals(3, getQueueSize(queue));
		
		int waited = 0;
		while (true) {
			Thread.sleep(100);
			waited += 100;
			
			// the queue should be empty immediately
			if (waited >= 1500) {
				assertEquals(0, getQueueSize(queue));
				break;
			}
		}
	}
	
	public void testSetDispatcherIntervalMinus() throws NoSuchFieldException, IllegalAccessException, 
			IllegalArgumentException, JSONException, InterruptedException {
		ReplayRequestQueue queue = new ReplayRequestQueue(apiManager);
		queue.start();
		
		ReplayRequestDispatcher dispatcher = getDispatcher(queue);
		
		Field dispatchIntervalField = ReplayRequestDispatcher.class.getDeclaredField("dispatchInterval");
		dispatchIntervalField.setAccessible(true);
		
		// interval should be 0 by default
		assertEquals(0, (int) dispatchIntervalField.get(dispatcher));
		
		// interval should be what it was set to
		queue.setDispatchInterval(-1);
		assertEquals(-1, (int) dispatchIntervalField.get(dispatcher));
		
		// make sure dispatch not start at beginning
		Field dispatchingField = ReplayRequestDispatcher.class.getDeclaredField("dispatching");
		dispatchingField.setAccessible(true);
		dispatchingField.setBoolean(dispatcher, false);
		
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
			
			// the queue should not be dispatched before manually dispatch
			if (waited < 3000) {
				assertEquals(3, getQueueSize(queue));
			} else {
				if (!dispatchOnce) {
					queue.dispatchNow();
					assertEquals(true, (boolean) dispatchingField.get(dispatcher));
					dispatchOnce = true;
				}
				
				// the queue should be empty shortly after dispatch
				if (waited >= 4500) {
					assertEquals(0, getQueueSize(queue));
					break;
				}
			}
		}
	}
	
	
	public void testSetDispatcherInterval5() throws NoSuchFieldException, IllegalAccessException, 
			IllegalArgumentException, JSONException, InterruptedException {
		ReplayRequestQueue queue = new ReplayRequestQueue(apiManager);
		queue.start();
		
		ReplayRequestDispatcher dispatcher = getDispatcher(queue);
		
		Field dispatchIntervalField = ReplayRequestDispatcher.class.getDeclaredField("dispatchInterval");
		dispatchIntervalField.setAccessible(true);
		
		// interval should be 0 by default
		assertEquals(0, (int) dispatchIntervalField.get(dispatcher));
		
		// interval should be what it was set to
		queue.setDispatchInterval(5);
		assertEquals(5, (int) dispatchIntervalField.get(dispatcher));
		
		// make sure dispatch not start at beginning
		Field dispatchingField = ReplayRequestDispatcher.class.getDeclaredField("dispatching");
		dispatchingField.setAccessible(true);
		dispatchingField.setBoolean(dispatcher, false);
		
		// the queue should be empty at start
		assertEquals(0, getQueueSize(queue));
		
		queue.add(apiManager.requestForEvent("event", null));
		queue.add(apiManager.requestForEvent("event", null));
		queue.add(apiManager.requestForEvent("event", null));
		
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
			if (waited >= 6500) {
				assertEquals(0, getQueueSize(queue));
				break;
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
		LinkedHashSet<ReplayRequest> requests = (LinkedHashSet<ReplayRequest>) currentRequests.get(queue);
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
	private ReplayRequestDispatcher getDispatcher(ReplayRequestQueue queue) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {

		Field dispatcherField = ReplayRequestQueue.class.getDeclaredField("mDispatcher");
		dispatcherField.setAccessible(true);
		ReplayRequestDispatcher dispatcher = (ReplayRequestDispatcher) dispatcherField.get(queue);
		
		return dispatcher;
	}
	
}
