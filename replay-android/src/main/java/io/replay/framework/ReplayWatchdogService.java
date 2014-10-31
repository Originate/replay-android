package io.replay.framework;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * If the app (or, God forbid, the library) crashes, ReplayWatchdogService will be called 6 hours hence.
 * <br>(A "crash" is defined as {@link Activity#onCreate(Bundle)} being called without a corresponding {@link Activity#onStop()}).
 *
 *The Service will wake up, check the queue for unsent events - if they exist, the Service will send them. In either case, after
 * this task is finished, the service will request to die, thus freeing up system resources.
 *
 */
public class ReplayWatchdogService extends IntentService {

    private static final String API_KEY = "api_key";

    static Intent createIntent(Context c, String apiKey){
        Intent i = new Intent(c, ReplayWatchdogService.class);
        i.putExtra(API_KEY, apiKey);
        return i;
    }

    public ReplayWatchdogService() {
        super("ReplayWatchdogService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String key = workIntent.getStringExtra(API_KEY);
        Config config;
        if (!Util.isNullOrEmpty(key)) {
            config = new Config(key);
        } else return; //there's nothing we can do.
        final ReplayQueue queue = new ReplayQueue(this, config);
        if (queue.count()>0) {
            queue.flush();
        }
    }
}