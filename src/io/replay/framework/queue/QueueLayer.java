package io.replay.framework.queue;

import android.content.Context;

import io.replay.framework.model.ReplayRequest;
import io.replay.framework.util.LooperThreadWithHandler;

/**
 * Created by parthpadgaonkar on 8/28/14.
 */
public class QueueLayer extends LooperThreadWithHandler {

    private ReplayQueue queue;

    public QueueLayer(Context context, ReplayQueue queue) {
        this.queue = queue;
        queue.start();
    }

    public void enqueue(final ReplayRequest data) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                queue.enqueue(data);
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

