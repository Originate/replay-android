package io.replay.framework;

import java.util.Map;

/**
 * Created by parthpadgaonkar on 8/28/14.
 */
class QueueLayer extends LooperThreadWithHandler {
    private ReplayQueue queue;

    QueueLayer(ReplayQueue queue) {
        this.queue = queue;
        queue.start();
    }

    void enqueue(final ReplayRequest data) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                enqueueAction(data);
            }
        });
    }

    /**
     * Convenience method that creates a {@link ReplayRequest} object and automatically enqueues it.
     *
     * @param event
     * @param data
     */
    void createAndEnqueue(final String event, final Object[] data) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                ReplayRequest request = ReplayRequestFactory.requestForEvent(event, data);
                enqueueAction(request);
            }
        });
    }

    void createAndEnqueue(final String event, final Map<String,?> data) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                ReplayRequest request = ReplayRequestFactory.requestForEvent(event, data);
                enqueueAction(request);
            }
        });
    }

    void createAndEnqueueTraits(final Map<String,?> data) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                ReplayRequest request = ReplayRequestFactory.requestForTraits(data);
                enqueueAction(request);
            }
        });
    }

    void createAndEnqueueTraits(final Object[] data) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                ReplayRequest request = ReplayRequestFactory.requestForTraits(data);
                enqueueAction(request);
            }
        });
    }

    private void enqueueAction(ReplayRequest data) {
        queue.enqueue(data);
    }

    void sendFlush() {
        handler().post(new Runnable() {
            @Override
            public void run() {
                queue.flush();
            }
        });
    }

    void enqueueJob(final ReplayJob job) {
        if(!BuildConfig.BUILD_TYPE.equals("release"))
            handler().post(new Runnable() {
            @Override
            public void run() {
                queue.enqueue(job);
            }
        });
        else{
            throw new IllegalStateException("This method is used solely for testing - please use QueueLayer#");
        }
    }
}

