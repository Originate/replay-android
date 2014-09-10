package io.replay.framework.model;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

import io.replay.framework.queue.ReplayQueue;
import io.replay.framework.util.Config;
import io.replay.framework.util.ReplayParams;

public class OrphanFinder extends IntentService {

    public OrphanFinder() {
        super("OrphanFinder");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        final Handler handler = new Handler();
        Config mConfig = ReplayParams.getOptions(this.getApplicationContext());
        final ReplayQueue queue = new ReplayQueue(this, mConfig);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                queue.flush();
            }
        }, 100);
    }
}