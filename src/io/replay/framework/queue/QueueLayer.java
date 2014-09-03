package io.replay.framework.queue;

import io.replay.framework.ReplayIO;
import io.replay.framework.model.ReplayRequest;
import io.replay.framework.util.LooperThreadWithHandler;

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
                if(queue.count() < MAX_QUEUE){
                    queue.enqueue(data);
                }else {
                    ReplayIO.debugLog("Request was dropped because event queue's max size has been reached.");
                }
            }
        });
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

