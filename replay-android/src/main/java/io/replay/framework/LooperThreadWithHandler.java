package io.replay.framework;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;

/**A Thread with a looping Handler. This class should be extended to provide additional functionality.
 *
 */
abstract class LooperThreadWithHandler extends Thread {
    private final Callback handlerCallback;
    private Handler handler;

    /**
     * This constructor will prepend "Thread-" to any incoming thread name.
     *
     * @param threadName
     */
    LooperThreadWithHandler(String threadName) {
        this("Thread-" + (threadName == null ? LooperThreadWithHandler.class.getSimpleName() : threadName), null);
    }

    /**
     * This constructor will prepend "Thread-" to any incoming thread name.
     *
     * @param threadName
     */
    LooperThreadWithHandler(String threadName, Callback handlerCallback) {
        super(threadName);
        this.handlerCallback = handlerCallback;
    }

    @Override
    public void run() {
        Looper.prepare();
        synchronized (this) {
            handler = new Handler(handlerCallback);
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
