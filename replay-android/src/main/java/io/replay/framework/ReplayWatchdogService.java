package io.replay.framework;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

class ReplayWatchdogService extends IntentService {

    private static final String API_KEY = "api_key";

    public static Intent createIntent(Context c, String apiKey){
        Intent i = new Intent(c, ReplayWatchdogService.class);
        i.putExtra(API_KEY, apiKey);
        return i;
    }

    public ReplayWatchdogService() {
        super("ReplayWatchdogService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Config mConfig = new Config();
        String key = workIntent.getStringExtra(API_KEY);
        if(Util.isNullOrEmpty(key)){
            mConfig.setApiKey(key);
        }else return;
        final ReplayQueue queue = new ReplayQueue(this, mConfig);
        if (queue.count()>0) {
            queue.flush();
        }
    }
}