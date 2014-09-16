package io.replay.framework.queue;

import org.json.JSONException;

import io.replay.framework.ReplayIO;
import io.replay.framework.model.ReplayRequest;
import io.replay.framework.model.ReplayRequestFactory;
import io.replay.framework.util.LooperThreadWithHandler;
import io.replay.framework.util.ReplayLogger;

/**
 * Created by parthpadgaonkar on 8/28/14.
 */
public class QueueLayer extends LooperThreadWithHandler {

    private static final int MAX_QUEUE = ReplayIO.getConfig().getMaxQueue();
    private ReplayQueue queue;

    public QueueLayer(ReplayQueue queue) {
        this.queue = queue;
        queue.start();
    }

    public void enqueue(final ReplayRequest data) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                enqueueAction(data);
            }
        });
    }

    /**Convenience method that creates a {@link ReplayRequest} object and automatically enqueues it.
     *
     * @param event
     * @param data
     */
    public void createAndEnqueue(final String event, final Object[] data){
        handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    ReplayRequest request = ReplayRequestFactory.requestForEvent(event, data);
                    enqueueAction(request);
                } catch (JSONException e) {
                    ReplayLogger.e(e, "Exception while creating request %s: ", event);
                }
            }
        });
    }

    public void createAndEnqueue(final String alias){
        handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    ReplayRequest request = ReplayRequestFactory.requestForAlias(alias);
                    enqueueAction(request);
                } catch (JSONException e) {
                    ReplayLogger.e(e, "Exception while creating request %s: ", alias);
                }
            }
        });
    }

    private void enqueueAction(ReplayRequest data) {
        if(queue.count() < MAX_QUEUE){
            queue.enqueue(data);
        }else {
            ReplayLogger.w("ReplayIO Queue", "request %s was dropped because max size of %s was reached", data.toString(), MAX_QUEUE);
        }
    }

    public void sendFlush() {
        handler().post(new Runnable() {
            @Override
            public void run() {
                queue.flush();
            }
        });
    }
}

