package io.replay.framework.tests;

import org.json.JSONException;

import android.content.Context;
import android.test.AndroidTestCase;

import io.replay.framework.queue.QueueLayer;
import io.replay.framework.model.ReplayRequestFactory;
import io.replay.framework.queue.ReplayQueue;
import io.replay.framework.util.Config;
import io.replay.framework.util.ReplayParams;
import io.replay.framework.util.ReplayPrefs;

public class ReplayQueueTest extends AndroidTestCase {

	private ReplayQueue queue;
    private static Config mConfig;

	@Override
	public void setUp() throws Exception {
		super.setUp();
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
        Context context = getContext();
        Context appContext = context.getApplicationContext();

        // load parameters
        mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(0);
        ReplayRequestFactory.init(appContext);
        queue = new ReplayQueue(context, mConfig);
        QueueLayer ql = new QueueLayer(queue);


        //make sure queue is empty for start of tests
        ql.sendFlush();
        Thread.sleep(2000);
        assertEquals(0, queue.count());

        //add events to queue
        ql.createAndEnqueue("event", null);
        ql.createAndEnqueue("event", null);
        ql.createAndEnqueue("event", null);
		assertEquals(3, queue.count());

        //The queue should automatically flush
		Thread.sleep(2000);
		assertEquals(0, queue.count());
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
        Context context = getContext();
        Context appContext = context.getApplicationContext();

        // load parameters
        mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(-1);
        ReplayRequestFactory.init(appContext);
        queue = new ReplayQueue(context, mConfig);
        QueueLayer ql = new QueueLayer(queue);


        //make sure queue is empty for start of tests
        ql.sendFlush();
        Thread.sleep(2000);
        assertEquals(0, queue.count());

        //add events to queue
        ql.createAndEnqueue("event", null);
        ql.createAndEnqueue("event", null);
        assertEquals(2, queue.count());

        //The queue should not flush after any amount of time
        Thread.sleep(10000);
        assertEquals(2, queue.count());

        //The queue should flush with flush() is called
        ql.sendFlush();
        Thread.sleep(2000);
        assertEquals(0,queue.count());
        queue.stop();
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
        Context context = getContext();
        Context appContext = context.getApplicationContext();

        // load parameters
        mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(5000);
        ReplayRequestFactory.init(appContext);
        queue = new ReplayQueue(context, mConfig);
        QueueLayer ql = new QueueLayer(queue);


        //make sure queue is empty for start of tests
        ql.sendFlush();
        Thread.sleep(2000);
        assertEquals(0, queue.count());

        //add events to queue
        ql.createAndEnqueue("event", null);
        ql.createAndEnqueue("event", null);
        assertEquals(2, queue.count());

        //make sure the queue is flushed after dispatchInterval milliseconds have passed
        Thread.sleep(7000);
        assertEquals(0, queue.count());
        queue.stop();
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
    public void testMaxQueue() throws NoSuchFieldException, IllegalAccessException,
            IllegalArgumentException, JSONException, InterruptedException {
        Context context = getContext();
        Context appContext = context.getApplicationContext();

        // load parameters
        mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(0);
        mConfig.setMaxQueue(5);
        ReplayRequestFactory.init(appContext);
        queue = new ReplayQueue(context, mConfig);
        queue.start();

        //make sure queue is empty for start of tests
        queue.flush();
        Thread.sleep(10000);
        assertEquals(0, queue.count());

        //add events to queue
        queue.enqueue(ReplayRequestFactory.requestForEvent("event", null));
        queue.enqueue(ReplayRequestFactory.requestForEvent("event", null));
        queue.enqueue(ReplayRequestFactory.requestForEvent("event", null));
        queue.enqueue(ReplayRequestFactory.requestForEvent("event", null));
        queue.enqueue(ReplayRequestFactory.requestForEvent("event", null));
        assertEquals(5, queue.count());

        //After maxQueue events are added, the system shouldn't allow any more
        queue.enqueue(ReplayRequestFactory.requestForEvent("event", null));
        assertEquals(5, queue.count());

        //flush the queue so future tests work
        queue.flush();
        Thread.sleep(2000);
        queue.stop();
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
    public void testFlushAt() throws NoSuchFieldException, IllegalAccessException,
            IllegalArgumentException, JSONException, InterruptedException {
        Context context = getContext();
        Context appContext = context.getApplicationContext();

        // load parameters
        mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(-1);
        mConfig.setFlushAt(5);
        ReplayRequestFactory.init(appContext);
        queue = new ReplayQueue(context, mConfig);
        queue.start();

        //make sure queue is empty for start of tests
        queue.flush();
        Thread.sleep(10000);
        assertEquals(0, queue.count());

        //add events to queue
        queue.enqueue(ReplayRequestFactory.requestForEvent("event", null));
        queue.enqueue(ReplayRequestFactory.requestForEvent("event", null));
        queue.enqueue(ReplayRequestFactory.requestForEvent("event", null));
        queue.enqueue(ReplayRequestFactory.requestForEvent("event", null));
        assertEquals(4, queue.count());

        //After flushAt events are added, the queue should automatically flush
        queue.enqueue(ReplayRequestFactory.requestForEvent("event", null));
        Thread.sleep(2000);
        assertEquals(0, queue.count());
        queue.stop();
    }

}
