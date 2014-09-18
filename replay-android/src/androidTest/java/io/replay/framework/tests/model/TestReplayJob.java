package io.replay.framework.tests.model;

import io.replay.framework.model.ReplayJob;
import io.replay.framework.model.ReplayRequest;

/**
 * Created by parthpadgaonkar on 9/18/14.
 */
public class TestReplayJob extends ReplayJob {
    public TestReplayJob(ReplayRequest request) {
        super(request);
    }

    @Override
    public void onAdded() {
        super.onAdded();
    }

    @Override
    public void onRun() throws Throwable {
        //super.onRun();
        //politely doesn't try to call the network
    }
}
