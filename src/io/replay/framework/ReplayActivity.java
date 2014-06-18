package io.replay.framework;

import android.app.Activity;

/**
 * Created by richard on 6/18/14.
 */
public class ReplayActivity extends Activity {

    private static int started;
    private static int resumed;
    @SuppressWarnings("unused")
    private static int paused;
    private static int stopped;

    @Override
    public void onStart(){
        super.onStart();
        ReplayIO.activityStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        ReplayIO.activityResume();
    }

    @Override
    public void onPause() {
        ReplayIO.activityPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        ReplayIO.activityStop();
        super.onStop();
    }
}
