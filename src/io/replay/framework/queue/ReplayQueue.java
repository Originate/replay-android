package io.replay.framework.queue;

import android.content.Context;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.config.Configuration.Builder;
import com.path.android.jobqueue.flush.DispatchTimerInterface;
import com.path.android.jobqueue.log.CustomLogger;

import io.replay.framework.model.ReplayJob;
import io.replay.framework.model.ReplayRequest;
import io.replay.framework.util.DispatchTimerFactory;
import io.replay.framework.util.ReplayLogger;
import io.replay.framework.util.ReplayPrefs;

/**
 * Created by parthpadgaonkar on 8/28/14.
 */
public class ReplayQueue {
    private final JobManager jobqueue;
    private final DispatchTimerInterface dispatchTimer;

    public ReplayQueue(Context context) {
        Configuration config = getJobQueueConfig(context);
        jobqueue = new JobManager(context, config);
        dispatchTimer = config.getDispatchTimer();
    }

    private static Configuration getJobQueueConfig(final Context context) {
        CustomLogger logger = new CustomLogger() {
            @Override
            public boolean isDebugEnabled() {
                return ReplayPrefs.get(context).getDebugMode();
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
              .maxConsumerCount(1) //we only want 1 thread executing jobs
              .consumerKeepAlive(120) //2 min kill-time on the thread
              .flushAt(40) //TODO this should be from Options
              .customLogger(logger)
              .dispatchTimer(DispatchTimerFactory.createTimer(300000, true)) //TODO dispatch time should be from Options
              .build();
    }

    public void start() {
        jobqueue.start();
    }

    public void stop() {
        jobqueue.stop();
    }

    public void enqueue(ReplayRequest request) {
        jobqueue.addJob(new ReplayJob(request));
    }

    public void flush() {
        dispatchTimer.onFinish(); //DispatchTimer.onFinish implicitly calls start()
    }

    public int count() {
        return jobqueue.count();
    }

}