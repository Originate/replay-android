package io.replay.framework;

import android.content.Context;
import android.test.AndroidTestCase;

import org.json.JSONException;

import java.util.HashMap;

public class ReplayQueueLayerTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreateAndEnqueue() throws InterruptedException{
        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        QueueLayer ql = new QueueLayer(queue);
        ql.start();

        //Queue should be empty
        assertEquals(0, queue.count());

        //check to make sure events can be enqueued
        ql.createAndEnqueue("Event", new HashMap<String, Object>());
        Thread.sleep(1000);
        assertEquals(1,queue.count());
    }

    public void testEnqueueJob() throws JSONException, InterruptedException{
        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        QueueLayer ql = new QueueLayer(queue);
        ql.start();

        //Queue should be empty
        assertEquals(0,queue.count());

        //check to make sure events can be enqueued
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");

        ql.enqueueJob(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        Thread.sleep(1000);
        assertEquals(1,queue.count());
    }

    public void testEnqueueRequest() throws JSONException, InterruptedException{
        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        QueueLayer ql = new QueueLayer(queue);
        ql.start();

        //Queue should be empty
        assertEquals(0,queue.count());

        //check to make sure events can be enqueued
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");

        ql.enqueueJob(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        Thread.sleep(1000);
        assertEquals(1,queue.count());
    }

    public void testFlush() throws InterruptedException{
        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        QueueLayer ql = new QueueLayer(queue);
        ql.start();

        //Queue should be empty
        assertEquals(0,queue.count());

        //enqueue event
        ql.createAndEnqueue("Event",new HashMap<String, Object>());

        //check to make sure events can be flushed
        ql.sendFlush();
        Thread.sleep(2000);

        assertEquals(0,queue.count());
    }


}
