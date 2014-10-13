package io.replay.framework;

import android.content.Context;
import android.test.AndroidTestCase;

import org.json.JSONException;

public class ReplayQueueTest extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test setDispatcherInterval(0), this will fail if server side is not on.
     * Slow network connection will cause failure, too.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws org.json.JSONException
     * @throws InterruptedException
     */

    public void testSetDispatcherIntervalZero() throws NoSuchFieldException, IllegalAccessException,
            IllegalArgumentException, JSONException, InterruptedException {
        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(0);
        mConfig.setFlushAt(10);
        mConfig.setMaxQueue(15);
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        queue.stop();

        //make sure queue is empty for start of tests
        assertEquals(0, queue.count());

        //add events to queue
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");

        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        assertEquals(3, queue.count());

        queue.start();

        //The queue should automatically flush
        Thread.sleep(3000);
        assertEquals(0, queue.count());
    }

    /**
     * Test setDispatcherInterval(-1), this will fail if server side is not on.
     * Slow network connection will cause failure, too.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws org.json.JSONException
     * @throws InterruptedException
     */
   /*
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
        ql.enqueueEvent("event", null);
        ql.enqueueEvent("event", null);
        assertEquals(2, queue.count());

        //The queue should not flush after any amount of time
        Thread.sleep(10000);
        assertEquals(2, queue.count());

        //The queue should flush with flush() is called
        ql.sendFlush();
        Thread.sleep(2000);
        assertEquals(0, queue.count());
        queue.stop();
    }*/

    /**
     * Test setDispatcherInterval(5), this will fail if server side is not on.
     * Slow network connection will cause failure, too.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws org.json.JSONException
     * @throws InterruptedException
     */
/*
    public void testSetDispatcherInterval5() throws NoSuchFieldException, IllegalAccessException,
                                                          IllegalArgumentException, JSONException, InterruptedException {
        Context context = getContext();
        final int interval = 300;

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(interval);
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        queue.start();

        //make sure queue is empty for start of tests
        assertEquals(0, queue.count());

        //add events to queue
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");

        queue.enqueue(new TestReplayJob(new ReplayRequest(RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(RequestType.EVENTS, json)));
        assertEquals(4, queue.count());

        //make sure the queue is flushed after dispatchInterval milliseconds have passed
        Thread.sleep(interval * 3);
        assertEquals(0, queue.count());
        queue.stop();
    }
*/

    /**
     * Test setDispatcherInterval(5), this will fail if server side is not on.
     * Slow network connection will cause failure, too.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws org.json.JSONException
     * @throws InterruptedException
     */
    public void testMaxQueue() throws NoSuchFieldException, IllegalAccessException,
            IllegalArgumentException, JSONException, InterruptedException {

        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(3600000); // 60 min == infinity?
        mConfig.setFlushAt(15);
        mConfig.setMaxQueue(5);
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        queue.start();

        //make sure queue is empty for start of tests
        assertEquals(0, queue.count());

        //add events to queue
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");

        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        assertEquals(5, queue.count());

        //After maxQueue events are added, the system shouldn't allow any more
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        assertEquals(5, queue.count());

        //flush the queue so future tests work
        queue.clear();
    }

    /**
     * Test setDispatcherInterval(5), this will fail if server side is not on.
     * Slow network connection will cause failure, too.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws org.json.JSONException
     * @throws InterruptedException
     */
    public void testFlushAt() throws NoSuchFieldException, IllegalAccessException,
            IllegalArgumentException, JSONException, InterruptedException {

        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(36000000); // 10 hours == infinity?
        mConfig.setFlushAt(5);
        mConfig.setMaxQueue(10);
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        queue.start();

        //make sure queue is empty for start of tests
        assertEquals(0, queue.count());

        //add events to queue
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");

        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        assertEquals(4, queue.count());

        //After flushAt events are added, the queue should automatically flush
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        Thread.sleep(2000);
        assertEquals(0, queue.count());
        queue.stop();
    }

}
