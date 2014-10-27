package io.replay.framework;

import android.os.Handler;
import android.os.Looper;

/**A Thread with a looping Handler. This class should be extended to provide additional functionality.
 *
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
    Handler handler() {
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
    void quit() {
        Looper.myLooper().quit();
    }
}
