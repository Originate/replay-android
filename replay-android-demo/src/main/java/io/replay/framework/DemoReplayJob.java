package io.replay.framework;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by parthpadgaonkar on 9/18/14.
 *
 * DEMO CLASS
 */
public class DemoReplayJob extends ReplayJob {

    public static final AtomicInteger id = new AtomicInteger(0);
    public final String idStr;


    public DemoReplayJob(ReplayRequest request) {
        super(request);
        idStr = "Test Job " + id.incrementAndGet();

    }

    @Override
    public void onAdded() {
        super.onAdded();
    }

    @Override
    public void onRun() throws Throwable {
        try { //politely attempts to contact the server once.
            super.onRun();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return;
        }
        ReplayLogger.d(idStr, "Successfully ran job");
    }
}
