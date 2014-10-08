package io.replay.framework;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.path.android.jobqueue.flush.DispatchTimerInterface;

/**
 * Factory class for {@link io.replay.framework.DispatchTimerFactory.DispatchTimer}.
 *
 * @see io.replay.framework.DispatchTimerFactory.DispatchTimer
 */
class DispatchTimerFactory {

    /**
     * Creates a DispatchTimer with the given parameters
     *
     * @see io.replay.framework.DispatchTimerFactory.DispatchTimer#DispatchTimer(long, boolean)
     */
    static DispatchTimer createTimer(long dispatchInMs, boolean resetAfterComplete) {
        if (dispatchInMs > 0L) {
            return new DispatchTimer(dispatchInMs, resetAfterComplete);
        } else { //dispatchInMs <=0
            return new ZeroDispatchTimer();
        }
    }

    /**
     * Implementation is very similar to {@link android.os.CountDownTimer}, but since that class marks most of its
     * implemented methods as <code>final</code>, it is infeasible to use.
     * <p>Unlike CountDownTimer, this class does not provide the option to "tick" multiple times; it "ticks" once
     * - when {@link #onFinish()} is called.
     * <p>Most notably, this class can reset itself ({@link #resetAfterComplete}) upon completion of the countdown, creating a loop that will be called until
     * {@link #cancel()} is called.
     */
    static class DispatchTimer implements DispatchTimerInterface { //extends CountDownTimer
        protected static final int MSG = 1;
        private long mStopTimeInFuture;
        private long dispatchInterval;
        protected TimerDispatcher clockAction;
        private boolean resetAfterComplete;
        private boolean running = false;

        /**
         * @param dispatchInMs       how long to wait before calling {@link com.path.android.jobqueue.flush.DispatchTimerInterface.TimerDispatcher#onTimeUp()}
         * @param resetAfterComplete if true, DispatchTimer will reset itself with the same parameters.
         */
        DispatchTimer(long dispatchInMs, boolean resetAfterComplete) {
            this.dispatchInterval = dispatchInMs;
            this.resetAfterComplete = resetAfterComplete;
        }

        @Override
        public synchronized void  start() {
            if (dispatchInterval <= 0) {
                onFinish();
                return;
            }
            mStopTimeInFuture = SystemClock.elapsedRealtime() + dispatchInterval;
            mHandler.sendMessage(mHandler.obtainMessage(MSG));
        }

        @Override
        public synchronized void cancel() {
            mHandler.removeMessages(MSG);
            running = false;
        }

        @Override
        public synchronized void onFinish() {
            ReplayLogger.d("dispatching flush!");
            clockAction.onTimeUp();
            running = false;

            if (resetAfterComplete) {
                this.start(); //restart the timer
            }
        }

        @Override
        public synchronized boolean isRunning() {
            return running;
        }

        @Override
        public synchronized void reset() {
            this.cancel();
            this.start();
        }

        @Override
        public void setListener(TimerDispatcher timerDispatcher) {
            this.clockAction = timerDispatcher;
        }

        // handles counting down
        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                synchronized (DispatchTimer.this) {
                    running = true;
                    final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();
                    if (millisLeft <= 0) {
                        onFinish();
                    } else { //if (millisLeft < mCountdownInterval) {
                        // no tick, just delay until done
                        sendMessageDelayed(obtainMessage(MSG), millisLeft);
                    }
                }
            }
        };
    }

    /**
     * Specialty subclass of {@link io.replay.framework.DispatchTimerFactory.DispatchTimer} that more efficiently handles the case
     * where the user wants no timer. A call to {@link #start()} will immediately call {@link #onFinish()}.
     */
    private static class ZeroDispatchTimer extends DispatchTimer {
        ZeroDispatchTimer() {
            super(0, false);
        }

        /**Immediately calls {@link #onFinish()}.
         * @see io.replay.framework.DispatchTimerFactory.DispatchTimer#start()
         */
        @Override
        public synchronized void start() {
            onFinish();
        }

        /** Does nothing.
         * @see io.replay.framework.DispatchTimerFactory.DispatchTimer#cancel()
         */
        @Override
        public synchronized void cancel() {
        } //do nothing

        /**Immediately calls {@link #onFinish()}.
         * @see io.replay.framework.DispatchTimerFactory.DispatchTimer#reset()
         */
        @Override
        public synchronized void reset() {
            onFinish();
        }

        /**returns <code>false</code>; ZeroDispatchTimer is never actively running. */
        @Override
        public synchronized boolean isRunning() {
            return false; //the zero-timer is never actively running.
        }

        @Override
        public synchronized void onFinish() {
            ReplayLogger.d("dispatching flush!");
            clockAction.onTimeUp();
        }
    }
}
