package io.replay.framework;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by parthpadgaonkar on 8/13/14.
 */
class LooperThreadWithHandler extends Thread {
    private Handler handler;

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

    private void waitForReady() {
        while (this.handler == null) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    ReplayLogger.e(e, "Failed while waiting for singleton thread ready.");
                }
            }
        }
    }

    /** Quits the current looping thread */
    public void quit() {
        Looper.myLooper().quit();
    }
}
