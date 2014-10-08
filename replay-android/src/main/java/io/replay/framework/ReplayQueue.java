package io.replay.framework;

import android.content.Context;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.config.Configuration.Builder;
import com.path.android.jobqueue.flush.DispatchTimerInterface;
import com.path.android.jobqueue.log.CustomLogger;

/**
 * Created by parthpadgaonkar on 8/28/14.
 */
class ReplayQueue {
    private final JobManager jobqueue;
    private final DispatchTimerInterface dispatchTimer;
    private final int MAX_QUEUE;

    public ReplayQueue(Context context, Config replayOptions) {
        Configuration queueOptions = getJobQueueConfig(context, replayOptions);
        jobqueue = new JobManager(context, queueOptions);
        dispatchTimer = queueOptions.getDispatchTimer();
        MAX_QUEUE = replayOptions.getMaxQueue();
    }

    private static Configuration getJobQueueConfig(final Context context, final Config replayOptions) {
        CustomLogger logger = new CustomLogger() {
            @Override
            public boolean isDebugEnabled() {
                return replayOptions.isDebug();
            }

            @Override
            public void d(String text, Object... args) {
                ReplayLogger.d("ReplayIO", text, args);
            }

            @Override
            public void e(Throwable t, String text, Object... args) {
                ReplayLogger.e(t, text, args);
            }

            @Override
            public void e(String text, Object... args) {
                ReplayLogger.e(text, args);
            }
        };
        return new Builder(context)
              .minConsumerCount(1)
              .maxConsumerCount(1)      // we only want 1 thread executing jobs
              .consumerKeepAlive(120)   // 2 min kill-time on the thread
              .flushAt(replayOptions.getFlushAt())
              .customLogger(logger)
              .dispatchTimer(DispatchTimerFactory.createTimer(replayOptions.getDispatchInterval(), true))
              .build();
    }

    public void start() {
        jobqueue.start();
        dispatchTimer.start();
    }

    public void stop() {
        jobqueue.stop();
    }

    public void enqueue(ReplayRequest request) {
        if(jobqueue.count() < MAX_QUEUE) {
            jobqueue.addJob(new ReplayJob(request));
        }else {
            ReplayLogger.w("ReplayIO Queue", "request %s was dropped because max size of %s was reached", request.toString(), MAX_QUEUE);
        }
    }

    public void enqueue(ReplayJob job) {
        if(jobqueue.count() < MAX_QUEUE) {
            jobqueue.addJob(job);
        }else {
            ReplayLogger.w("ReplayIO Queue", "request %s was dropped because max size of %s was reached", job.toString(), MAX_QUEUE);
        }
    }

    public void flush() {
        dispatchTimer.onFinish(); //DispatchTimer.onFinish implicitly calls start()
    }

    public void clear(){
        jobqueue.clear();
    }

    public int count() {
        return jobqueue.count();
    }

}