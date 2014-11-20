package io.replay.framework.testClasses;

import android.content.Intent;

import java.util.concurrent.CountDownLatch;

import io.replay.framework.ReplayWatchdogService;

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