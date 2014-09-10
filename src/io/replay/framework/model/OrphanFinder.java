package io.replay.framework.model;

import android.content.Context;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

import io.replay.framework.queue.ReplayQueue;
import io.replay.framework.util.Config;
import io.replay.framework.util.ReplayParams;

public class OrphanFinder extends IntentService {

    private static final String API_KEY = "api_key";

    public static Intent createIntent(Context c, String apiKey){
        Intent i = new Intent(c, OrphanFinder.class);
        i.putExtra(API_KEY, apiKey);
        return i;
    }

    public OrphanFinder() {
        super("OrphanFinder");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        final Handler handler = new Handler();
        Config mConfig = new Config();
        mConfig.setApiKey(workIntent.getStringExtra(API_KEY));
        final ReplayQueue queue = new ReplayQueue(this, mConfig);
        if (queue.count()>0) {
            queue.flush();
        }
    }
}