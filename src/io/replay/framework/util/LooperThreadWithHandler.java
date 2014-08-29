package io.replay.framework.util;

import android.os.Handler;
import android.os.Looper;

import io.replay.framework.ReplayIO;

/**
 * Created by parthpadgaonkar on 8/13/14.
 */
public class LooperThreadWithHandler extends Thread {
    protected Handler handler;

    private void waitForReady() {
        while (this.handler == null) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    ReplayIO.errorLog("Failed while waiting for singleton thread ready.");
                }
            }
        }
    }

    @Override
    public void run() {
        Looper.prepare();
        synchronized (this) {
            handler = new Handler();
            notifyAll();
        }
        Looper.loop();
    }

    /** Gets this thread's handler */
    public Handler handler() {
        waitForReady();
        return handler;
    }

    /** Quits the current looping thread */
    public void quit() {
        Looper.myLooper().quit();
    }
}
