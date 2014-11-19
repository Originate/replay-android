package io.replay.framework;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.UiThreadTest;

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

    @UiThreadTest
    public void testSetDispatcherIntervalZero() throws NoSuchFieldException, IllegalAccessException,
            IllegalArgumentException, JSONException, InterruptedException {
        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(0);
        mConfig.setFlushAt(10);
        mConfig.setMaxQueue(100);
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
    @UiThreadTest
    public void testSetDispatcherInterval5() throws NoSuchFieldException, IllegalAccessException,
                                                          IllegalArgumentException, JSONException, InterruptedException {
        Context context = getContext();
        final int interval = 5000;

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

        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        assertEquals(4, queue.count());

        //make sure the queue is flushed after dispatchInterval milliseconds have passed

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
    @UiThreadTest
    public void testMaxQueue() throws NoSuchFieldException, IllegalAccessException,
            IllegalArgumentException, JSONException, InterruptedException {

        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(179999); // 60 min == infinity?
        mConfig.setFlushAt(150);
        mConfig.setMaxQueue(100);
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        queue.start();

        //make sure queue is empty for start of tests
        assertEquals(0, queue.count());

        //add events to queue
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");

        for (int i = 0; i < 120; i+=5) {
            queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
            queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
            queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
            queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
            queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        }
        assertEquals(100, queue.count());

        //After maxQueue events are added, the system shouldn't allow any more
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        assertEquals(100, queue.count());

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
    @UiThreadTest
    public void testFlushAt() throws NoSuchFieldException, IllegalAccessException,
            IllegalArgumentException, JSONException, InterruptedException {

        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(179999); // 10 hours == infinity?
        mConfig.setFlushAt(10);
        mConfig.setMaxQueue(100);
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
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        assertEquals(10, queue.count());

        //After flushAt events are added, the queue should automatically flush
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        Thread.sleep(2000);
        assertEquals(0, queue.count());
        queue.stop();
    }

}
