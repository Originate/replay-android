package io.replay.framework.tests;

import android.content.Context;
import android.test.AndroidTestCase;

import org.json.JSONException;

import io.replay.framework.ReplayConfig.RequestType;
import io.replay.framework.model.ReplayJsonObject;
import io.replay.framework.model.ReplayRequest;
import io.replay.framework.queue.QueueLayer;
import io.replay.framework.queue.ReplayQueue;
import io.replay.framework.tests.model.TestReplayJob;
import io.replay.framework.util.Config;
import io.replay.framework.util.ReplayParams;

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
        ql.createAndEnqueue("Event", null);
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

        ql.enqueueJob(new TestReplayJob(new ReplayRequest(RequestType.EVENTS, json)));
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

        ql.enqueueJob(new TestReplayJob(new ReplayRequest(RequestType.EVENTS, json)));
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
        ql.createAndEnqueue("Event",null);

        //check to make sure events can be flushed
        ql.sendFlush();
        Thread.sleep(1000);

        assertEquals(0,queue.count());
    }


}
