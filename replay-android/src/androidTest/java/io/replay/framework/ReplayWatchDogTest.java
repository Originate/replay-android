package io.replay.framework;


import android.content.Context;
import android.content.Intent;
import android.test.ServiceTestCase;

import java.util.concurrent.CountDownLatch;

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
    }

    public void testWatchDog() throws InterruptedException {
        Context context = getContext();

        // Create Queue
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        mConfig.setDispatchInterval(3600000); // 60 min == infinity?
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.stop();

        //Add jobs to the queue
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        queue.enqueue(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        assertEquals(3,queue.count());

        //Invoke the watchdog service
        startService(new Intent(getSystemContext(), ReplayWatchdogService.class));
        try {
            latch.await();
            Thread.sleep(2000);
            //forces the queue to re-poll its DB; otherwise, will return incorrect count.
            queue = new ReplayQueue(context, mConfig);
            queue.stop(); //this is to prove that the queue isn't flushing jobs that the Service didn't get to.

            assertEquals(0, queue.count());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
