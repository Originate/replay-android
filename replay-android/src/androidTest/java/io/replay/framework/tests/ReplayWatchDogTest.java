package io.replay.framework.tests;


import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.content.Intent;
import android.test.ServiceTestCase;

import io.replay.framework.ReplayConfig;
import io.replay.framework.ReplayWatchdogService;
import io.replay.framework.model.ReplayJsonObject;
import io.replay.framework.model.ReplayRequest;
import io.replay.framework.queue.ReplayQueue;
import io.replay.framework.tests.model.TestReplayJob;
import io.replay.framework.tests.model.WatchDogServiceWrapper;
import io.replay.framework.util.Config;
import io.replay.framework.util.ReplayParams;

public class ReplayWatchDogTest extends ServiceTestCase<WatchDogServiceWrapper> {

    private CountDownLatch latch;

    public ReplayWatchDogTest() {
        super(WatchDogServiceWrapper.class);
    }

    @Override
    protected void setupService() {
        super.setupService();

        latch = new CountDownLatch(1);
        getService().setLatch(latch);

        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(3600000); // 60 min == infinity?
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        queue.start();

        //add events to queue
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");

        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayConfig.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayConfig.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayConfig.RequestType.EVENTS, json)));
        queue.stop();
    }

    public void testHandleIntent() throws InterruptedException {
        Context context = getContext();

        // Create Queue
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(3600000); // 60 min == infinity?
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.start();

        //Add jobs to the queue
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayConfig.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayConfig.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayConfig.RequestType.EVENTS, json)));
        assertEquals(3,queue.count());

        //Invoke the watchdog service
        startService(new Intent(getSystemContext(), ReplayWatchdogService.class));
        latch.await();
        Thread.sleep(2000);

        //get the new queue
        queue = new ReplayQueue(context, mConfig);


        assertEquals(0,queue.count());
    }


}
