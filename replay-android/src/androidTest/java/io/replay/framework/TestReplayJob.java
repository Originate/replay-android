package io.replay.framework;

import java.util.concurrent.atomic.AtomicInteger;

public class TestReplayJob extends ReplayJob {

    public static final AtomicInteger id = new AtomicInteger(0);
    public final String idStr;


    public TestReplayJob(ReplayRequest request) {
        super(request);
        idStr = "Test Job " + id.incrementAndGet();
    }

    @Override
    public void onAdded() {
        super.onAdded();
    }

    @Override
    public void onRun() throws Throwable {
        System.out.println("running!");
        //super.onRun();
        //politely doesn't try to call the network
    }
}
