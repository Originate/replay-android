package io.replay.framework.tests.model;

import io.replay.framework.ReplayWatchdogService;
import java.util.concurrent.CountDownLatch;
import android.content.Intent;

public class WatchDogServiceWrapper extends ReplayWatchdogService {
    private CountDownLatch latch;

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        latch.countDown();
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

}